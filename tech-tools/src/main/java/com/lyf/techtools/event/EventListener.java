package com.lyf.techtools.event;

public class EventListener<E> {
  private final EventFilter<E> filter;
  private final EventProcessor<E> processor;

  public EventListener(EventFilter<E> filter, EventProcessor<E> processor) {
    this.filter = filter;
    this.processor = processor;
  }

  public boolean match(E event) {
    return filter.accept(event);
  }

  public void invoke(E event) {
    try {
      processor.process(event);
    } catch (Exception ex) {
      // 可接入统一日志
    }
  }

}
