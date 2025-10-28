package com.lyf.lib.exception;

public class BadRequestError extends Error {

  public BadRequestError(String message, Exception e) {
    super(message, e);
  }

  public BadRequestError(String message) {
    super(message);
  }
}
