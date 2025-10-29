package org.example.collections;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class ConcurrentHashMapOptimizations {
  // 优化1: 避免热点key - 使用分段计数
  /**
   * 分段计数的工作原理：
   *
   * 传统方式：key -> value
   *   "page_views" -> AtomicLong(1000000)
   *   所有线程竞争同一个锁
   *
   * 分段方式：key + segment -> value
   *   "page_views_0" -> AtomicLong(250000)
   *   "page_views_1" -> AtomicLong(250000)
   *   "page_views_2" -> AtomicLong(250000)
   *   "page_views_3" -> AtomicLong(250000)
   *   线程分散到4个不同的锁上
   */
  public static class SegmentedCounter {
    private final ConcurrentHashMap<String, AtomicLong>[] segments;
    private static final int SEGMENT_COUNT = 16;

    @SuppressWarnings("unchecked")
    public SegmentedCounter() {
      segments = new ConcurrentHashMap[SEGMENT_COUNT];
      for (int i = 0; i < SEGMENT_COUNT; i++) {
        segments[i] = new ConcurrentHashMap<>();
      }
    }

    public void increment(String key) {
      int segment = Math.abs(key.hashCode()) % SEGMENT_COUNT;
      segments[segment].compute(key, (k, v) -> {
        if (v == null) return new AtomicLong(1);
        v.incrementAndGet();
        return v;
      });
    }

    public long getCount(String key) {
      int segment = Math.abs(key.hashCode()) % SEGMENT_COUNT;
      AtomicLong counter = segments[segment].get(key);
      return counter != null ? counter.get() : 0;
    }
  }
}
