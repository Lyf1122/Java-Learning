package org.example.concurrent;

/**
 * 假设我们有一个 Web 应用，需要在每个请求中保存用户的登录信息（如用户 ID），并且每个线程（对应每个请求）需要访问和修改该用户信息。
 */

public class UserContext {
  private static final ThreadLocal<Integer> userIdHolder = new ThreadLocal<>();

  public static void setUserId(Integer userId) {
    userIdHolder.set(userId);
  }

  public static Integer getUserId() {
    return userIdHolder.get();
  }

  public static void clear() {
    userIdHolder.remove();
  }

}
