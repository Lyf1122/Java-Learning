package com.lyf.registrymonitor.configs;

import org.apache.commons.lang3.StringUtils;

public record ClusterConfig(
  String namespace, String service, String srvName, Integer srvPort, String srvServer, String appName, String rsMode, String mqServer, String cfServer
) {

  public String serviceUrl() {

    String url = StringUtils.isBlank(srvServer) ? String.format("%s.%s.svc.cluster.local:%d", service, namespace, srvPort) : srvServer;
    return StringUtils.startsWith(url, "http") ? url : "http:" + "//" + url;
  }

  public static Builder of() {
    return new Builder();
  }

  public static class Builder {

    private String namespace;
    private String service;
    private String srvName;
    private Integer srvPort;
    private String srvServer;
    private String appName;
    private String rsMode;
    private String mqServer;
    private String cfServer;

    public ClusterConfig build() {
      return new ClusterConfig(namespace, service, srvName, srvPort, srvServer, appName, rsMode, mqServer, cfServer);
    }

    public Builder namespace(String namespace) {
      this.namespace = namespace;
      return this;
    }

    public Builder service(String service) {
      this.service = service;
      return this;
    }

    public Builder srvName(String srvName) {
      this.srvName = srvName;
      return this;
    }

    public Builder srvPort(Integer srvPort) {
      this.srvPort = srvPort;
      return this;
    }

    public Builder srvServer(String srvServer) {
      this.srvServer = srvServer;
      return this;
    }

    public Builder appName(String appName) {
      this.appName = appName;
      return this;
    }

    public Builder rsMode(String rsMode) {
      this.rsMode = rsMode;
      return this;
    }

    public Builder mqServer(String mqServer) {
      this.mqServer = mqServer;
      return this;
    }

    public Builder cfServer(String cfServer) {
      this.cfServer = cfServer;
      return this;
    }
  }
}
