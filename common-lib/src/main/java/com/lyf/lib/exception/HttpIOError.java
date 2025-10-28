package com.lyf.lib.exception;

public class HttpIOError extends Error {

  public HttpIOError(String message, Throwable e) {
    super(message, e);
  }

  public HttpIOError(String message) {
    super(message);
  }
}

