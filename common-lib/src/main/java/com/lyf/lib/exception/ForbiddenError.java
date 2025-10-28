package com.lyf.lib.exception;

public class ForbiddenError extends Error {

  private final String[] privileges;

  public ForbiddenError(String state, String action) {
    this(state, action, new String[0]);
  }


  public ForbiddenError(String state, String action, String[] privileges) {
    super(String.format("Unexpected Action in workflow, current task state [%s], action [%s]", state, action));
    this.privileges = privileges;
  }

  public ForbiddenError(String message) {
    this(message, new String[0]);
  }

  public ForbiddenError(String message, String[] privileges) {
    super(message);
    this.privileges = privileges;
  }

  public String[] missingPrivileges() {
    return privileges;
  }
}
