package org.generate.timer;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * DynamicTaskScheduler 测试，验证各类调度方式与查询/取消功能。
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SchedulerConfiguration.class)
public class DynamicTaskSchedulerTest {

  @Autowired
  private DynamicTaskScheduler dynamicTaskScheduler;
  @Autowired
  private ThreadPoolTaskScheduler scheduler;

  private final List<String> createdIds = new ArrayList<>();

  @AfterEach
  void tearDown() {
    createdIds.forEach(dynamicTaskScheduler::cancel);
    createdIds.clear();
  }

  @AfterAll
  static void afterAll(@Autowired ThreadPoolTaskScheduler scheduler) {
    scheduler.shutdown();
  }

  @Test
  void testScheduleFixedRate() throws Exception {
    AtomicInteger counter = new AtomicInteger();
    String id = "rateTask";
    createdIds.add(id);
    dynamicTaskScheduler.scheduleFixedRate(id, counter::incrementAndGet, Duration.ofMillis(100));
    Thread.sleep(350); // 期望执行至少3次
    Assertions.assertTrue(counter.get() >= 3, "固定速率任务执行次数不足: " + counter.get());
    Optional<String> desc = dynamicTaskScheduler.describe(id);
    Assertions.assertTrue(desc.isPresent());
    Assertions.assertTrue(desc.get().contains("FIXED_RATE"));
  }

  @Test
  void testScheduleFixedDelay() throws Exception {
    AtomicInteger counter = new AtomicInteger();
    String id = "delayTask";
    createdIds.add(id);
    dynamicTaskScheduler.scheduleFixedDelay(id, () -> {
      counter.incrementAndGet();
      try { Thread.sleep(50); } catch (InterruptedException ignored) {}
    }, Duration.ofMillis(100));
    Thread.sleep(500); // 预估执行 >=3 次
    Assertions.assertTrue(counter.get() >= 3, "固定延迟任务执行次数不足: " + counter.get());
  }

  @Test
  void testScheduleOnce() throws Exception {
    CountDownLatch latch = new CountDownLatch(1);
    String id = "onceTask";
    createdIds.add(id);
    dynamicTaskScheduler.scheduleOnce(id, latch::countDown, new java.util.Date(System.currentTimeMillis() + 100));
    boolean completed = latch.await(1, TimeUnit.SECONDS);
    Assertions.assertTrue(completed, "单次任务未在预期时间执行");
    Optional<String> desc = dynamicTaskScheduler.describe(id);
    Assertions.assertTrue(desc.isPresent());
  }

  @Test
  void testScheduleCron() throws Exception {
    AtomicInteger counter = new AtomicInteger();
    String id = "cronTask";
    createdIds.add(id);
    // 每秒执行一次
    dynamicTaskScheduler.scheduleCron(id, counter::incrementAndGet, "0/1 * * * * *");
    Thread.sleep(1200); // 等待至少1次触发
    Assertions.assertTrue(counter.get() >= 1, "Cron任务未触发");
    Assertions.assertTrue(dynamicTaskScheduler.describe(id).orElse("").contains("CRON"));
  }

  @Test
  void testCancel() throws Exception {
    AtomicInteger counter = new AtomicInteger();
    String id = "cancelTask";
    createdIds.add(id);
    dynamicTaskScheduler.scheduleFixedRate(id, counter::incrementAndGet, Duration.ofMillis(100));
    Thread.sleep(250);
    int beforeCancel = counter.get();
    boolean cancelled = dynamicTaskScheduler.cancel(id);
    Assertions.assertTrue(cancelled, "取消返回结果不正确");
    Thread.sleep(300);
    int afterCancel = counter.get();
    Assertions.assertEquals(beforeCancel, afterCancel, "任务取消后仍在执行: before=" + beforeCancel + " after=" + afterCancel);
    Assertions.assertFalse(dynamicTaskScheduler.exists(id));
  }

  @Test
  void testListDescriptions() throws Exception {
    // 建立两个任务
    dynamicTaskScheduler.scheduleFixedRate("listA", () -> {}, Duration.ofMillis(200));
    dynamicTaskScheduler.scheduleFixedDelay("listB", () -> {}, Duration.ofMillis(200));
    createdIds.add("listA");
    createdIds.add("listB");
    Thread.sleep(250);
    Collection<String> list = dynamicTaskScheduler.listDescriptions();
    Assertions.assertEquals(2, list.size());
    Assertions.assertTrue(list.stream().anyMatch(s -> s.contains("listA")));
    Assertions.assertTrue(list.stream().anyMatch(s -> s.contains("listB")));
  }
}

