package org.generate.timer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * 调度器配置，提供ThreadPoolTaskScheduler与动态调度器Bean。
 * @author lyf
 */
@Configuration
@EnableScheduling
public class SchedulerConfiguration {

  @Bean
  public ThreadPoolTaskScheduler threadPoolTaskScheduler() {
    ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
    scheduler.setPoolSize(10);
    scheduler.setThreadNamePrefix("dynamic-scheduler-");
    scheduler.setRemoveOnCancelPolicy(true);
    scheduler.initialize();
    return scheduler;
  }

  @Bean
  public DynamicTaskScheduler dynamicTaskScheduler(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
    return new DynamicTaskScheduler(threadPoolTaskScheduler);
  }
}
