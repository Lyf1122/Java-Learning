package com.lyf.techtools.event;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * EventListeningEngine 测试：验证注册、过滤、停止、队列溢出等行为。
 */
public class EventListeningEngineTest {

  private EventListeningEngine<DemoEvent> engine;

  @AfterEach
  void tearDown() {
    if (engine != null) {
      engine.stop();
    }
  }

  // 简单事件模型
  record DemoEvent(String type, int value) {}

  @Test
  void testBasicDispatch() throws Exception {
    engine = new EventListeningEngine<>(100, 4);
    AtomicInteger counter = new AtomicInteger();
    engine.register(new EventListener<>(e -> true, e -> counter.incrementAndGet()));
    engine.start();
    for (int i = 0; i < 20; i++) {
      engine.submit(new DemoEvent("ANY", i));
    }
    Thread.sleep(300); // 等待消费
    Assertions.assertEquals(20, counter.get(), "所有事件都应被消费");
  }

  @Test
  void testFilter() throws Exception {
    engine = new EventListeningEngine<>(50, 3);
    AtomicInteger typeACounter = new AtomicInteger();
    AtomicInteger allCounter = new AtomicInteger();
    engine.register(new EventListener<>(e -> "A".equals(e.type()), e -> typeACounter.incrementAndGet()));
    engine.register(new EventListener<>(e -> true, e -> allCounter.incrementAndGet()));
    engine.start();
    for (int i = 0; i < 30; i++) {
      engine.submit(new DemoEvent(i % 2 == 0 ? "A" : "B", i));
    }
    Thread.sleep(400);
    Assertions.assertEquals(30, allCounter.get(), "通配监听器应接收全部");
    Assertions.assertEquals(15, typeACounter.get(), "A类型应收到15个");
  }

  @Test
  void testStop() throws Exception {
    engine = new EventListeningEngine<>(30, 2);
    AtomicInteger counter = new AtomicInteger();
    engine.register(new EventListener<>(e -> true, e -> counter.incrementAndGet()));
    engine.start();
    for (int i = 0; i < 10; i++) {
      engine.submit(new DemoEvent("X", i));
    }
    Thread.sleep(200);
    int beforeStop = counter.get();
    engine.stop();
    for (int i = 0; i < 10; i++) {
      engine.submit(new DemoEvent("X", i)); // 不应再入队
    }
    Thread.sleep(200);
    Assertions.assertEquals(beforeStop, counter.get(), "停止后不应再处理新事件");
  }

  @Test
  void testQueueOverflow() throws Exception {
    int capacity = 10;
    engine = new EventListeningEngine<>(capacity, 1);
    AtomicInteger processed = new AtomicInteger();
    CountDownLatch latch = new CountDownLatch(1);
    engine.register(new EventListener<>(e -> true, e -> {
      processed.incrementAndGet();
      if (processed.get() >= capacity) {
        latch.countDown();
      }
      // 模拟慢处理，增加溢出概率
      Thread.sleep(50);
    }));
    engine.start();
    // 快速提交超过容量的事件
    for (int i = 0; i < capacity * 3; i++) {
      engine.submit(new DemoEvent("OVF", i));
    }
    // 等待至少处理满容量数量
    boolean ok = latch.await(2, TimeUnit.SECONDS);
    Assertions.assertTrue(ok, "应至少处理到队列容量数量");
    // 由于offer丢弃溢出，处理数应 <= total submitted
    Assertions.assertTrue(processed.get() <= capacity * 3, "处理数不应超过提交数");
  }
}

