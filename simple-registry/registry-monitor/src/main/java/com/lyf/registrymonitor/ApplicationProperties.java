package com.lyf.registrymonitor;

import com.lyf.registrymonitor.configs.ClusterConfig;
import com.lyf.registrymonitor.configs.KafkaConfig;

public final class ApplicationProperties {
  private final static ApplicationProperties INS = new ApplicationProperties();
  private ClusterConfig config;
  private KafkaConfig kafkaConfig;

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

  public KafkaConfig kafkaConfig() {
    return kafkaConfig;
  }

  ApplicationProperties kafkaConfig(KafkaConfig cfg) {
    this.kafkaConfig = cfg;
    return this;
  }
}
