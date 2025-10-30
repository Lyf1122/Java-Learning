package com.lyf.techtools.juc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureExample {

  private static final Logger logger = LoggerFactory.getLogger(CompletableFutureExample.class);
  private static final ExecutorService executor = Executors.newFixedThreadPool(10);

  /**
   * 简单的串行执行示例 - 任务A完成后执行任务B，任务B完成后执行任务C
   * @return
   */
  public static CompletableFuture<Void> simpleSerialExecution() {
    logger.info("Starting simple serial execution example...");
    return CompletableFuture.runAsync(
      () -> {
        logger.info("Task A started");
        // 模拟任务A的工作
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        logger.info("Task A completed");
      }, executor
    ).thenRunAsync(
      () -> {
        logger.info("Task B started");
        // 模拟任务B的工作
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        logger.info("Task B completed");
      }, executor
    ).thenRunAsync(
      () -> {
        logger.info("Task C started");
        // 模拟任务C的工作
        try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
        logger.info("Task C completed");
      }, executor
    );
  }

  public static CompletableFuture<String> parallelExecutionWithResults() {
    logger.info("Starting parallel execution with results example...");
    CompletableFuture<String> task1 = CompletableFuture.supplyAsync(() -> {
      logger.info("Task 1 started");
      try { Thread.sleep(1000); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
      logger.info("Task 1 completed");
      return "Result from Task 1";
    }, executor);

    CompletableFuture<String> task2 = CompletableFuture.supplyAsync(() -> {
      logger.info("Task 2 started");
      try { Thread.sleep(1500); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
      logger.info("Task 2 completed");
      return "Result from Task 2";
    }, executor);

    return task1.thenCombineAsync(task2, (result1, result2) -> {
      logger.info("Combining results");
      return result1 + " & " + result2;
    }, executor);
  }

}
