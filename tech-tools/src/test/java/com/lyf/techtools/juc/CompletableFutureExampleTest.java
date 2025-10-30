package com.lyf.techtools.juc;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

class CompletableFutureExampleTest {
  @Test
  public void testExample() {
    CompletableFuture<Void> voidCompletableFuture = CompletableFutureExample.simpleSerialExecution();
    // 等待任务完成
    voidCompletableFuture.join();
  }
  @Test
  public void testParallelExecutionWithResults() {
    CompletableFuture<String> resultFuture = CompletableFutureExample.parallelExecutionWithResults();
    String result = resultFuture.join();
    Assertions.assertTrue(result.contains("Result from Task 1"));
    Assertions.assertTrue(result.contains("Result from Task 2"));
  }
}