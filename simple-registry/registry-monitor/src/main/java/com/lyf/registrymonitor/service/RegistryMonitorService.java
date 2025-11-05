package com.lyf.registrymonitor.service;

import com.lyf.lib.exception.InvalidClientRequestException;
import com.lyf.registrymonitor.proxy.RegistryProxy;
import com.lyf.registrymonitor.proxy.RegistryProxyIns;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class RegistryMonitorService {

  private static final Logger logger = LoggerFactory.getLogger(RegistryMonitorService.class);

  final private RegistryProxyIns regProxyIns;

  public RegistryMonitorService(@Autowired RegistryProxyIns regProxyIns) {
    this.regProxyIns = regProxyIns;
  }

  public void disconnect() {
    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(RegistryProxy::disconnect);
  }

  public void connect() {
    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(RegistryProxy::connect);
  }

  public void register(String namespace, String name, String protocol, String url, String ids) {

    if(StringUtils.isAnyBlank(namespace, name, protocol, url, ids)) {
      throw new InvalidClientRequestException("Invalid request, missing namespace, service, protocol, url or ids.");
    }

    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(
      px -> px.push("register", StringUtils.join(new String[] { namespace, name, protocol, url, ids }, "|"))
    );
  }

  public void subscribe(String namespace, String msgType) {
    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(px -> px.subscribe(namespace, msgType));
  }

  public void subscribe(String cfgTopic) {
    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(px -> px.subscribe("cfg", cfgTopic));
  }

  public void unsubscribe(String namespace, String msgType) {
    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(px -> px.unsubscribe(namespace, msgType));
  }

  public void unsubscribe(String cfgTopic) {
    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(px -> px.unsubscribe("cfg", cfgTopic));
  }

  public String info() {
    RegistryProxy regPx = regProxyIns.ins();
    Optional.ofNullable(regPx).ifPresent(px -> px.push("info", ""));
    String sid = UUID.randomUUID().toString();
    MsgQueue.INFO.add(sid, "");
    return sid;
  }

  public Map<String, String> getInfo() {
    return MsgQueue.INFO.getAll();
  }

  public String getInfo(String sid) {
    return MsgQueue.INFO.get(sid);
  }

  public String getConfig(String key) {
    if(key == null) return null;
    return MsgQueue.CONFIG.get(key);
  }

  public Map<String, String> getConfigs() {
    return Collections.unmodifiableMap(new LinkedHashMap<>(MsgQueue.CONFIG.getAll()));
  }

}
