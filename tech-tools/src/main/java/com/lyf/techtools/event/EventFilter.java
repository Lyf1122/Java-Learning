package com.lyf.techtools.event;

@FunctionalInterface
public interface EventFilter<E> {
  boolean accept(E event);
}
