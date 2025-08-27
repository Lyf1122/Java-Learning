package org.example.controller;

import org.example.async.AsyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@RestController
@EnableAsync
public class AsynecController {

  private final static Logger log = LoggerFactory.getLogger(AsynecController.class);

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
