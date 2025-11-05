package com.lyf.registrymonitor.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MsgQueueTest {
  @Test
  public void testAddAsSingle() {

    MsgQueue<String> q = new MsgQueue<>(1, 0);

    q.add("K01", "V01");
    assertEquals("K01", q.lastKey());

    q.add("K02", "V02");
    assertEquals("K02", q.lastKey());

    assertEquals(1, q.size());
    assertNull(q.get("K01"));
    assertEquals("V02", q.get("K02"));

    assertEquals(1, q.size());
    q.add("K02", "V02XX");
    assertEquals("V02XX", q.get("K02"));
    assertEquals("K02", q.lastKey());
  }

  @Test
  public void testAdd() {

    MsgQueue<String> q = new MsgQueue<>();
    for(int i=0; i<1000; i++) {
      q.add(String.format("K%05d", i), String.format("VAL%05d", i));
    }
    assertEquals(1000, q.size());
    assertEquals("VAL00001", q.get("K00001"));
    assertEquals("VAL00999", q.get("K00999"));
  }
}