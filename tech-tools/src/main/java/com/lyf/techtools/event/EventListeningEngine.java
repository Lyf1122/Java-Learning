package com.lyf.techtools.event;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class EventListeningEngine<E> {
  private final CopyOnWriteArrayList<EventListener<E>> listeners = new CopyOnWriteArrayList<>();
  private final BlockingQueue<E> queue;
  private final ExecutorService workerPool;
  private final AtomicBoolean running = new AtomicBoolean(false);

  public EventListeningEngine(int queueCapacity, int workers) {
    this.queue = new LinkedBlockingQueue<>(queueCapacity);
    this.workerPool = Executors.newFixedThreadPool(workers);
  }

  public void register(EventListener<E> listener) {
    listeners.add(listener);
  }

  public void unregister(EventListener<E> listener) {
    listeners.remove(listener);
  }

  public void submit(E event) {
    if (!running.get()) return;
    // 若队列满可扩展丢弃策略或阻塞
    queue.offer(event);
  }

  public void start() {
    if (running.compareAndSet(false, true)) {
      workerPool.execute(this::dispatchLoop);
    }
  }

  public void stop() {
    running.set(false);
    workerPool.shutdownNow();
  }

  private void dispatchLoop() {
    while (running.get()) {
      try {
        E event = queue.poll(2, TimeUnit.SECONDS);
        if (event == null) continue;
        for (EventListener<E> listener : listeners) {
          if (listener.match(event)) {
            workerPool.execute(() -> listener.invoke(event));
          }
        }
      } catch (InterruptedException ignored) {
        Thread.currentThread().interrupt();
      }
    }
  }
}
