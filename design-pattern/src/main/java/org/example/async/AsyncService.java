package org.example.async;

import java.util.concurrent.Future;

public interface AsyncService {

  void asyncMethod() throws InterruptedException;

  Future<String> futureMethod() throws InterruptedException;
}
