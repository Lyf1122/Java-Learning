package org.generate.timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.PeriodicTrigger;

import java.time.Duration;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

/**
 * 动态定时任务调度器：支持 cron / 固定速率 / 固定延迟 / 单次执行 的注册、取消与查询。
 * 提供统一异常捕获与简单查询描述。
 * @author lyf
 */
public class DynamicTaskScheduler {

  private static final Logger log = LoggerFactory.getLogger(DynamicTaskScheduler.class);

  private final ThreadPoolTaskScheduler scheduler;
  private final Map<String, TaskHolder> tasks = new ConcurrentHashMap<>();

  public DynamicTaskScheduler(ThreadPoolTaskScheduler scheduler) {
    this.scheduler = scheduler;
  }

  // 任务包装，保存 Future 与描述
  private record TaskHolder(String id, ScheduledFuture<?> future, String type, String expression) {}

  // 注册 Cron 任务
  public synchronized boolean scheduleCron(String id, Runnable task, String cronExpression) {
    Objects.requireNonNull(cronExpression, "cron表达式不能为空");
    CronTrigger cronTrigger = new CronTrigger(cronExpression);
    return register(id, task, cronTrigger, "CRON", cronExpression);
  }

  // 固定速率(从开始时间按周期执行) 参数 period: 两次开始之间的间隔
  public synchronized boolean scheduleFixedRate(String id, Runnable task, Duration period) {
    Objects.requireNonNull(period, "周期不能为空");
    PeriodicTrigger trigger = new PeriodicTrigger(period.toMillis());
    trigger.setFixedRate(true);
    return register(id, task, trigger, "FIXED_RATE", period.toMillis() + "ms");
  }

  // 固定延迟(上次结束到下次开始之间的间隔) 参数 delay: 结束后的等待时间
  public synchronized boolean scheduleFixedDelay(String id, Runnable task, Duration delay) {
    Objects.requireNonNull(delay, "延迟不能为空");
    PeriodicTrigger trigger = new PeriodicTrigger(delay.toMillis());
    trigger.setFixedRate(false);
    return register(id, task, trigger, "FIXED_DELAY", delay.toMillis() + "ms");
  }

  // 指定时间单次执行
  public synchronized boolean scheduleOnce(String id, Runnable task, Date startTime) {
    if (exists(id)) {
      cancel(id);
    }
    ScheduledFuture<?> future = scheduler.schedule(wrap(id, task), startTime);
    tasks.put(id, new TaskHolder(id, future, "ONCE", startTime.toString()));
    return true;
  }

  // 注册核心逻辑
  private boolean register(String id, Runnable task, Trigger trigger, String type, String expr) {
    if (exists(id)) {
      cancel(id);
    }
    ScheduledFuture<?> future = scheduler.schedule(wrap(id, task), trigger);
    tasks.put(id, new TaskHolder(id, future, type, expr));
    log.info("注册任务: id={}, type={}, expr={}", id, type, expr);
    return true;
  }

  // 包装任务，统一异常处理
  private Runnable wrap(String id, Runnable delegate) {
    return () -> {
      long start = System.currentTimeMillis();
      try {
        delegate.run();
        long cost = System.currentTimeMillis() - start;
        log.debug("任务执行完成 id={}, cost={}ms", id, cost);
      } catch (Throwable t) {
        log.error("任务执行异常 id={}", id, t);
      }
    };
  }

  // 取消任务
  public synchronized boolean cancel(String id) {
    TaskHolder holder = tasks.remove(id);
    if (holder == null) {
      return false;
    }
    holder.future().cancel(false);
    log.info("已取消任务 id={}", id);
    return true;
  }

  // 是否存在
  public boolean exists(String id) {
    return tasks.containsKey(id);
  }

  // 查询单个任务描述
  public Optional<String> describe(String id) {
    return Optional.ofNullable(tasks.get(id)).map(t ->
      "id=" + t.id() + ", type=" + t.type() + ", expr=" + t.expression() + ", done=" + t.future().isDone());
  }

  // 列出全部任务的描述信息
  public Collection<String> listDescriptions() {
    return tasks.values().stream()
      .map(t -> "id=" + t.id() + ", type=" + t.type() + ", expr=" + t.expression() + ", done=" + t.future().isDone())
      .toList();
  }
}
