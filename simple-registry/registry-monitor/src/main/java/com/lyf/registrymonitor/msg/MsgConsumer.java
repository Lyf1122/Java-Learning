package com.lyf.registrymonitor.msg;

public interface MsgConsumer<T> {
  void process(T obj);
}
