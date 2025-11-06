package com.lyf.registrymonitor.listener;

import com.lyf.lib.util.HttpUtil;
import com.lyf.lib.util.HttpUtilFactory;
import com.lyf.registrymonitor.ApplicationProperties;
import com.lyf.registrymonitor.configs.ClusterConfig;
import com.lyf.registrymonitor.msg.MsgConsumer;
import com.lyf.registrymonitor.proxy.SimpleRegistryProxy;
import com.lyf.registrymonitor.proxy.RegistryProxy;
import com.lyf.registrymonitor.service.MsgQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.lang.NonNull;

import java.net.http.HttpClient;
import java.util.concurrent.Executors;

public class AppContextRefreshedEventListener implements ApplicationListener<ContextRefreshedEvent> {
  private static final Logger logger = LoggerFactory.getLogger(AppContextRefreshedEventListener.class);

  private final HttpUtilFactory httpFactory;

  public AppContextRefreshedEventListener() {
    httpFactory = () -> HttpUtil.of(HttpClient.newBuilder().executor(Executors.newFixedThreadPool(20)).build());
  }

  @Override
  public void onApplicationEvent(@NonNull ContextRefreshedEvent event) {
    logger.info("===== <<ContextRefreshedEvent>> : " + event);
    initRegistryProxy();
  }

  private void initRegistryProxy() {
    logger.info("===== >> initRegistryProxy ::: " );
    ApplicationProperties properties = ApplicationProperties.ins();
    ClusterConfig config = properties.config();
    if(config == null) {
      logger.warn("ClusterConfig not initialized, skip registry proxy init.");
      return;
    }
    RegistryProxy registryProxy = null;
    String mode = config.rsMode();
    if("simple".equalsIgnoreCase(mode)) {
      registryProxy = SimpleRegistryProxy.reset(config);
    } else {
      registryProxy = RegistryProxy.ins(mode);
    }
    logger.info("===== >> >> ClusterConfig ::: {}", config );

    if (registryProxy != null) {
      if (!registryProxy.isUp()) {
        registryProxy.connect();
      }
      registryProxy.addMsgConsumer(new String[]{ "^.*$" }, (MsgConsumer<String>) msg -> logger.info("Receiving unknown message: " + msg));
      registryProxy.addMsgConsumer(new String[]{ "^\\[INFO\\].*$" }, (MsgConsumer<String>) MsgQueue.INFO::add);
    } else {
      logger.warn("RegistryProxy is null for mode={}, initialization skipped.", mode);
    }
  }

}
