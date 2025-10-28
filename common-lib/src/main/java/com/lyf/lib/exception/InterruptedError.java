package com.lyf.lib.exception;

public class InterruptedError extends Error {

  public InterruptedError(String message, Exception e) {
    super(message, e);
  }

  public InterruptedError(String message) {
    super(message);
  }
}
