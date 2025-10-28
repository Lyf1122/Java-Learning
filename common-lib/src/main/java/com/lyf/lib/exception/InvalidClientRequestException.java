package com.lyf.lib.exception;

public class InvalidClientRequestException extends RuntimeException {

  public InvalidClientRequestException(String message) {
    super(message);
  }

  public InvalidClientRequestException(String message, Throwable e) {
    super(message, e);
  }

}
