package com.lyf.lib.exception;

public enum ErrorCodeEnum {

  INVALID_EMAIL("client-invalid-email", "Invalid Email"),
  INVALID_COUNTRY("client-invalid-country-code", "Invalid Country Code"),
  INVALID_COMMAND("client-invalid-command", "Invalid Command"),

  AUTH_CODE_TIMEOUT("auth-code-timeout", "Auth Code Timeout"),

  SYSTEM_ERROR("sys-error", "System Error"), // default

  ;

  ErrorCodeEnum(String code, String message) {
    this.code = code;
    this.message = message;
  }

  public String code() {
    return code;
  }

  public String message() {
    return message;
  }

  private final String code;
  private final String message;
}
