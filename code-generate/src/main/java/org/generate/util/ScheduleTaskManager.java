package org.generate.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

public class ScheduleTaskManager {

  private final static Logger logger = LoggerFactory.getLogger(ScheduleTaskManager.class);

  private final Map<String, ScheduledFuture<?>> scheduleTaskMap = new ConcurrentHashMap<>();
  private final List<ScheduleTask> taskList = new LinkedList<>();
  private final ThreadPoolTaskScheduler taskScheduler;

  public ScheduleTaskManager(ThreadPoolTaskScheduler taskScheduler) {
    this.taskScheduler = taskScheduler;
  }

  public void register(String name, String description, String cron, Runnable runnable) {
    ScheduledFuture<?> scheduledFuture = taskScheduler.schedule(runnable, new CronTrigger(cron));
    scheduleTaskMap.put(name, scheduledFuture);
    taskList.add(new ScheduleTask(name, description, cron, runnable));
    logger.info("Registered task: {}", name);
  }

  public void cancel(String name) {
    ScheduledFuture<?> scheduledFuture = scheduleTaskMap.get(name);
    if (scheduledFuture != null) {
      scheduledFuture.cancel(true);
      scheduleTaskMap.remove(name);
      logger.info("Cancelled task: {}", name);
    }
  }

  public record ScheduleTask(String name, String description, String cron, Runnable runnable) {}


}
