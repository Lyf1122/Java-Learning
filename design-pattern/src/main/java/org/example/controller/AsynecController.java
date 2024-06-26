package org.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.example.async.AsyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@EnableAsync
@Slf4j
public class AsynecController {
  @Autowired
  private AsyncService asyncService;

  @GetMapping("/async")
  public String asyncCallMethod() throws InterruptedException {
    long start = System.currentTimeMillis();
    log.info("Call async method, thread name: [{}]", Thread.currentThread().getName());
    asyncService.asyncMethod();
    return "task completes in :" + (System.currentTimeMillis() - start) + "milliseconds";
  }

  @GetMapping("/asyncFuture")
  public String asyncFuture() throws InterruptedException, ExecutionException {
    long start = System.currentTimeMillis();
    log.info("Call async method, thread name: [{}]", Thread.currentThread().getName());
    // future.get()会阻塞当前线程，直到异步任务执行完成
    Future<String> future = asyncService.futureMethod();
    String taskResult = future.get();
    return "task completes in :" + (System.currentTimeMillis() - start) + "milliseconds\n" + taskResult;
  }

}
