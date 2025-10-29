package com.lyf.registrymonitor.service;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MsgQueue<T> {

  private final Map<String, Pair<Integer, T>> map = new ConcurrentHashMap<>();
  private final Queue<String> queue = new ConcurrentLinkedQueue<>(new LinkedList<>());

  public final static MsgQueue<String> INFO = new MsgQueue<>(10,2);
//  public final static MsgQueue<EventDto> EVENT = new MsgQueue<>();
//  public final static MsgQueue<TaskDto> TASK = new MsgQueue<>();
  public final static MsgQueue<String> CONFIG = new MsgQueue<>();

  private final static int MAX = 15000;
  private final static int BUFFER = 10000;

  private final int max;
  private final int buffer;

  private String lastKey = "0000";

  public MsgQueue() {
    this(MAX, BUFFER);
  }

  public MsgQueue(int max, int buffer) {
    this.max = max;
    this.buffer = buffer;
  }

  public T get(String key) {
    Pair<Integer, T> pair = map.get(key);
    return (pair == null) ? null : pair.getRight();
  }

  public Map<String, T> getAll() {
    Map<String, T> res = new LinkedHashMap<>();
    map.forEach((k, v) -> res.put(k, v.getRight()));
    return res;
  }

  public void add(T elem) {
    add(lastKey, elem);
  }

  public void add(String key, T elem) {

    if(map.containsKey(key)) {

      Pair<Integer, T> oriPair = map.get(key);
      Integer idx = oriPair.getLeft();
      Pair<Integer, T> pair = new ImmutablePair<>(idx + 1, elem);
      lastKey = key;
      queue.add(key);
      map.put(key, pair);

    } else {

      clean();
      lastKey = key;
      queue.add(key);
      map.put(key, new ImmutablePair<>(0, elem));

    }
  }

  public int size() {
    return map.size();
  }

  public String lastKey() {
    return lastKey;
  }

  private void clean() {

    if(map.size() < max + buffer) {
      return;
    }

    while (map.size() >= max) {

      String key = queue.poll();
      Pair<Integer, T> oriPair = map.get(key);
      Integer idx = oriPair.getLeft();
      if(idx > 0) {
        Pair<Integer, T> pair = new ImmutablePair<>(idx - 1, oriPair.getRight());
        map.put(key, pair);
      } else {
        map.remove(key);
      }

    }
  }

}
