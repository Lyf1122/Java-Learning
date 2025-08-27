package org.example.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;

@Service
public class AsyncServiceImpl implements AsyncService{

  private final static Logger log = LoggerFactory.getLogger(AsyncServiceImpl.class);

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
