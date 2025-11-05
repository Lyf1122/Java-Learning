package com.lyf.registrymonitor.service;

import com.lyf.registrymonitor.proxy.RegistryProxy;
import com.lyf.registrymonitor.proxy.RegistryProxyIns;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class RegistryMonitorServiceTest extends BaseServiceTest{

  @Autowired RegistryMonitorService service;

  @Test
  public void testDisconnect() {
    assertDoesNotThrow(() -> service.disconnect());
    assertDoesNotThrow(() -> service().disconnect());
  }

  @Test
  public void testConnect() {
    assertDoesNotThrow(() -> service.connect());
    assertDoesNotThrow(() -> service().connect());
  }

  @Test
  public void testRegister() {
    assertDoesNotThrow(() -> service().register("test", "evan", "http", "localhost:8080", "abc123"));
    assertDoesNotThrow(() -> service().register("test", "evan", "http", "localhost:8080", "abc123"));
  }

  @Test
  public void testSubscribe() {
    assertDoesNotThrow(() -> service.subscribe("test", "msg"));
    assertDoesNotThrow(() -> service().subscribe("test", "msg"));
    assertDoesNotThrow(() -> service.subscribe("ns.svc.k1"));
    assertDoesNotThrow(() -> service().subscribe("ns.svc.k1"));
  }

  @Test
  public void testUnsubscribe() {
    assertDoesNotThrow(() -> service.unsubscribe("test", "msg"));
    assertDoesNotThrow(() -> service().unsubscribe("test", "msg"));
    assertDoesNotThrow(() -> service.subscribe("ns.svc.k1"));
    assertDoesNotThrow(() -> service().subscribe("ns.svc.k1"));
  }

  @Test
  public void testInfo() {
    assertDoesNotThrow(() -> service.info());
    assertDoesNotThrow(() -> service().info());
  }

  private RegistryMonitorService service() {

    RegistryProxy px = Mockito.mock(RegistryProxy.class);
    RegistryProxyIns regProxyIns = () -> px;
    return new RegistryMonitorService(regProxyIns);
  }

}