package com.lyf.techtools.event;

@FunctionalInterface
public interface EventProcessor<E> {
  void process(E event) throws Exception;
}
