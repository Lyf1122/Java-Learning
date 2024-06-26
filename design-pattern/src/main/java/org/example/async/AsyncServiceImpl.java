package org.example.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Slf4j
@Service
public class AsyncServiceImpl implements AsyncService{

  @Async
  @Override
  public void asyncMethod() throws InterruptedException {
    Thread.sleep(3000);
    log.info("Thread: [{}], Calling external service...", Thread.currentThread().getName());
  }

  @Async
  @Override
  public Future<String> futureMethod() throws InterruptedException {
    Thread.sleep(5000);
    log.info("Thread: [{}], Calling external service...", Thread.currentThread().getName());
    return new AsyncResult<>("Task is done");
  }
}
