package com.lyf.registrymonitor;

import com.lyf.registrymonitor.configs.ClusterConfig;

public final class ApplicationProperties {
  private final static ApplicationProperties INS = new ApplicationProperties();
  private ClusterConfig config;

  private ApplicationProperties() {}

  public static ApplicationProperties ins() {
    return INS;
  }

  public ClusterConfig config() {
    return config;
  }

  ApplicationProperties config(ClusterConfig config) {
    this.config = config;
    return this;
  }
}
