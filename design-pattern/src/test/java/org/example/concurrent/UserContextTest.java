package org.example.concurrent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserContextTest {
  @Test
  public void testThreadLocal1() throws InterruptedException {
    Thread t1 = new Thread(() -> {
      UserContext.setUserId(1);
      assertEquals(1, UserContext.getUserId());
    });

    Thread t2 = new Thread(() -> {
      UserContext.setUserId(2);
      assertEquals(2, UserContext.getUserId());
    });

    t1.start();
    t2.start();
    // 主线程会在t1执行完成前暂停执行
    t1.join();
    t2.join();

    assertNull(UserContext.getUserId());
  }

  @Test
  public void testThreadLocal2() throws InterruptedException {
    UserContext.setUserId(3);

    Thread t1 = new Thread(() -> {
      UserContext.setUserId(1);
      assertEquals(1, UserContext.getUserId());
    });

    Thread t2 = new Thread(() -> {
      UserContext.setUserId(2);
      assertEquals(2, UserContext.getUserId());
    });

    t1.start();
    t2.start();
    // 主线程会在t1执行完成前暂停执行
    t1.join();
    t2.join();

    assertEquals(3, UserContext.getUserId());
  }

  @Test
  public void testCleanUp() throws InterruptedException {
    Thread t = new Thread(
        () -> {
          UserContext.setUserId(1);
          assertEquals(1, UserContext.getUserId());
          // clear
          UserContext.clear();
          assertNull(UserContext.getUserId());
        }
    );
    t.start();
    t.join();
  }

}