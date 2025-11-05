package com.lyf.registrymonitor;

import com.lyf.registrymonitor.configs.ClusterConfig;
import com.lyf.registrymonitor.proxy.RegistryProxy;
import com.lyf.registrymonitor.proxy.RegistryProxyIns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource({"classpath:application-spring.properties"})
public class ApplicationConfiguration {

  @Bean
  public ClusterConfig clusterConfig(
    @Value("${cluster.namespace:evan-loc}") String ns,
    @Value("${cluster.service:model}") String service, @Value("${cluster.srv-port:8080}") Integer srvPort,
    @Value("${cluster.srv-url:localhost:8080}") String srvUrl, @Value("${cluster.ins-name:ins-model}") String insName,
    @Value("${cluster.rs-mode:none}") String rsMode, @Value("${cluster.mq-server:localhost:5757}") String mqServer,
    @Value("${cluster.cf-server:localhost:5757}") String cfServer
  ) {
    return ClusterConfig.of().namespace(ns).appName(insName).service(service)
      .srvName("model").srvPort(srvPort).srvServer(srvUrl).rsMode(rsMode).mqServer(mqServer).cfServer(cfServer)
      .build();
  }

  @Bean
  public RegistryProxyIns regProxyIns(@Autowired ClusterConfig config) {
    return () -> RegistryProxy.ins(config.rsMode());
  }
}
