package com.lyf.registrymonitor.proxy;

import com.lyf.registrymonitor.msg.MsgConsumer;
import org.apache.commons.lang3.StringUtils;

public interface RegistryProxy {

  static RegistryProxy ins(String mode) {
    RegistryProxy px = null;
    if(StringUtils.isNotBlank(mode)) {
      switch (mode) {
        case "simple" -> px = SimpleRegistryProxy.ins();
        case "kafka" -> throw new UnsupportedOperationException();
      }
    }
    return px;
  }

  void get(String msg);
  boolean isUp();
  void push(String cmd, String msg);
  void addMsgConsumer(String[] codes, MsgConsumer<String> consumer);

  void connect();
  void disconnect();

  void subscribe(String namespace, String msgType);
  void subscribe(String topic);
  void unsubscribe(String namespace, String msgType);
  void unsubscribe(String topic);

}
