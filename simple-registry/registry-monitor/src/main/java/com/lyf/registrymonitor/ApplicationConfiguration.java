package com.lyf.registrymonitor;

import com.lyf.registrymonitor.configs.ClusterConfig;
import com.lyf.registrymonitor.configs.KafkaConfig;
import com.lyf.registrymonitor.proxy.RegistryProxyIns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import javax.annotation.PostConstruct;

@Configuration
@PropertySource({"classpath:application-spring.properties"})
public class ApplicationConfiguration {

  private ClusterConfig clusterConfig;

  @Bean
  public ClusterConfig clusterConfig(
    @Value("${cluster.namespace:evan-loc}") String ns,
    @Value("${cluster.service:model}") String service, @Value("${cluster.srv-port:8080}") Integer srvPort,
    @Value("${cluster.srv-url:localhost:8080}") String srvUrl, @Value("${cluster.ins-name:ins-model}") String insName,
    @Value("${cluster.rs-mode:none}") String rsMode, @Value("${cluster.mq-server:localhost:5757}") String mqServer,
    @Value("${cluster.cf-server:localhost:5757}") String cfServer
  ) {
    this.clusterConfig = ClusterConfig.of().namespace(ns).appName(insName).service(service)
      .srvName("model").srvPort(srvPort).srvServer(srvUrl).rsMode(rsMode).mqServer(mqServer).cfServer(cfServer)
      .build();
    ApplicationProperties.ins().config(this.clusterConfig); // 绑定到单例
    return this.clusterConfig;
  }

  @PostConstruct
  public void afterInit() {
    // 预留: 可在此进行模式相关的预初始化（如 Kafka 模式的连接测试）
  }

  @Bean
  public RegistryProxyIns regProxyIns(@Autowired ClusterConfig config) {
    return () -> com.lyf.registrymonitor.proxy.RegistryProxy.ins(config.rsMode());
  }

  @Bean
  public KafkaConfig kafkaConfig(
    @Value("${kafka.enabled:true}") boolean enabled,
    @Value("${kafka.bootstrapServers:localhost:9092}") String bootstrapServers,
    @Value("${kafka.groupId:registry-monitor-group}") String groupId,
    @Value("${kafka.clientId:registry-monitor-client}") String clientId,
    @Value("${kafka.autoOffsetReset:latest}") String autoOffsetReset,
    @Value("${kafka.pollTimeoutMs:1000}") int pollTimeoutMs
  ) {
    KafkaConfig cfg = KafkaConfig.of().enabled(enabled)
      .bootstrapServers(bootstrapServers).groupId(groupId).clientId(clientId)
      .autoOffsetReset(autoOffsetReset).pollTimeoutMs(pollTimeoutMs).build();
    ApplicationProperties.ins().kafkaConfig(cfg);
    return cfg;
  }
}
