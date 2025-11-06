package com.lyf.registrymonitor.proxy;

import com.lyf.registrymonitor.ApplicationProperties;
import com.lyf.registrymonitor.configs.ClusterConfig;
import com.lyf.registrymonitor.configs.KafkaConfig;
import com.lyf.registrymonitor.msg.MsgConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

/**
 * Kafka-based implementation of RegistryProxy.
 * Topic conventions (default):
 *   register  -> registry. Register
 *   info      -> registry.info (optional)
 *   heartbeat -> registry. Heartbeat
 *   config    -> registry.config
 *   status    -> registry. Status
 * Generic push(cmd,msg) will send to topic: registry.<cmd>
 */
public class KafkaRegistryProxy implements RegistryProxy {
  private static final Logger logger = LoggerFactory.getLogger(KafkaRegistryProxy.class);

  private static KafkaRegistryProxy INS;

  private final KafkaRegistryClient client;
  private final KafkaMessageDispatcher dispatcher = new KafkaMessageDispatcher();
  private final KafkaConfig kafkaConfig;
  private final ClusterConfig clusterConfig;

  private KafkaRegistryProxy(KafkaRegistryClient client, KafkaConfig kafkaConfig, ClusterConfig clusterConfig) {
    this.client = client;
    this.kafkaConfig = kafkaConfig;
    this.clusterConfig = clusterConfig;
    this.client.setListener(dispatcher::dispatch);
  }

  public static synchronized KafkaRegistryProxy ins() {
    if(INS == null) {
      KafkaConfig kcfg = ApplicationProperties.ins().kafkaConfig();
      ClusterConfig ccfg = ApplicationProperties.ins().config();
      if(kcfg == null || ccfg == null) {
        logger.warn("KafkaRegistryProxy initialization skipped due to missing configuration.");
        return null;
      }
      INS = new KafkaRegistryProxy(new KafkaRegistryClient(kcfg), kcfg, ccfg);
    }
    return INS;
  }

  @Override
  public void get(String msg) {
    // For Kafka mode, 'get' simply dispatches an incoming message already handled by dispatcher.
    dispatcher.dispatch(msg);
  }

  @Override
  public boolean isUp() {
    return kafkaConfig.enabled() && client.isUp();
  }

  @Override
  public void push(String cmd, String msg) {
    if(!kafkaConfig.enabled()) { logger.debug("Kafka disabled, skip push cmd={} msg={} ", cmd, msg); return; }
    String topic = "registry." + cmd;
    String key = clusterConfig.appName();
    client.send(topic, key, msg == null ? "" : msg);
  }

  @Override
  public void addMsgConsumer(String[] codes, MsgConsumer<String> consumer) {
    dispatcher.register(codes, consumer);
  }

  @Override
  public void connect() {
    if(!kafkaConfig.enabled()) { logger.info("Kafka disabled, skip connect."); return; }
    client.start();
    // Auto subscribe essential topics
    client.subscribe("registry.register");
    client.subscribe("registry.info");
    client.subscribe("registry.config");
    client.subscribe("registry.status");
    client.subscribe("registry.heartbeat");
  }

  @Override
  public void disconnect() {
    if(!kafkaConfig.enabled()) return;
    client.stop();
  }

  @Override
  public void subscribe(String namespace, String msgType) {
    if(!kafkaConfig.enabled()) return;
    // Map namespace+msgType to topic: namespace.msgType
    String topic = namespace + "." + msgType;
    client.subscribe(topic);
  }

  @Override
  public void subscribe(String topic) {
    if(!kafkaConfig.enabled()) return;
    client.subscribe(topic);
  }

  @Override
  public void unsubscribe(String namespace, String msgType) {
    if(!kafkaConfig.enabled()) return;
    String topic = namespace + "." + msgType;
    client.unsubscribe(topic);
  }

  @Override
  public void unsubscribe(String topic) {
    if(!kafkaConfig.enabled()) return;
    client.unsubscribe(topic);
  }
}

