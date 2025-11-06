package com.lyf.registrymonitor.proxy;

import com.lyf.registrymonitor.configs.KafkaConfig;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

/**
 * Low level Kafka client wrapper. Manages producer/consumer lifecycle and polling.
 */
class KafkaRegistryClient {
  private static final Logger logger = LoggerFactory.getLogger(KafkaRegistryClient.class);

  private final KafkaConfig config;
  private KafkaProducer<String, String> producer;
  private KafkaConsumer<String, String> consumer;
  private final AtomicBoolean running = new AtomicBoolean(false);
  private final ExecutorService pollExecutor = Executors.newSingleThreadExecutor(r -> {
    Thread t = new Thread(r, "kafka-registry-poll-thread");
    t.setDaemon(true); return t;
  });

  private Consumer<String> msgListener;
  private final Set<String> subscribedTopics = new HashSet<>();

  KafkaRegistryClient(KafkaConfig config) {
    this.config = config;
  }

  synchronized void start() {
    if(!config.enabled()) {
      logger.warn("Kafka disabled, skip start.");
      return;
    }
    if(running.get()) return;
    Properties props = new Properties();
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    props.put(ProducerConfig.CLIENT_ID_CONFIG, config.clientId());
    props.put(ProducerConfig.ACKS_CONFIG, "1");
    producer = new KafkaProducer<>(props);

    Properties cprops = new Properties();
    cprops.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, config.bootstrapServers());
    cprops.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    cprops.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    cprops.put(ConsumerConfig.GROUP_ID_CONFIG, config.groupId());
    cprops.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, config.autoOffsetReset());
    cprops.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "true");
    consumer = new KafkaConsumer<>(cprops);
    if(!subscribedTopics.isEmpty()) {
      consumer.subscribe(new ArrayList<>(subscribedTopics));
      logger.info("Subscribed topics at start: {}", subscribedTopics);
    }
    running.set(true);
    pollExecutor.submit(this::pollLoop);
  }

  private void pollLoop() {
    logger.info("Kafka poll loop started.");
    while (running.get()) {
      try {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(config.pollTimeoutMs()));
        for (ConsumerRecord<String, String> record : records) {
          if(msgListener != null) {
            try { msgListener.accept(record.value()); } catch (Throwable ex) { logger.error("Listener error", ex); }
          }
        }
      } catch (Exception e) {
        logger.error("Poll loop exception", e);
        try { Thread.sleep(1000L); } catch (InterruptedException ignored) {}
      }
    }
    logger.info("Kafka poll loop terminated.");
  }

  synchronized void stop() {
    if(!running.get()) return;
    running.set(false);
    try { consumer.wakeup(); } catch (Exception ignored) {}
    pollExecutor.shutdownNow();
    try { producer.close(Duration.ofSeconds(2)); } catch (Exception ignored) {}
    try { consumer.close(Duration.ofSeconds(2)); } catch (Exception ignored) {}
  }

  void send(String topic, String key, String value) {
    if(!config.enabled()) { logger.debug("Kafka disabled, skip send."); return; }
    if(producer == null) { logger.warn("Producer null, message skipped topic={}, key={}", topic, key); return; }
    ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, value);
    producer.send(record, (md, ex) -> {
      if(ex != null) logger.error("Failed to send to topic={}, key={}, ex={}", topic, key, ex.getMessage());
      else logger.trace("Sent to topic={}, partition={}, offset={}", md.topic(), md.partition(), md.offset());
    });
  }

  void subscribe(String topic) {
    if(!config.enabled()) { logger.debug("Kafka disabled, skip subscribe."); return; }
    subscribedTopics.add(topic);
    if(consumer != null) {
      consumer.subscribe(new ArrayList<>(subscribedTopics));
      logger.info("Update subscription topics={}", subscribedTopics);
    }
  }

  void unsubscribe(String topic) {
    if(!config.enabled()) { logger.debug("Kafka disabled, skip unsubscribe."); return; }
    subscribedTopics.remove(topic);
    if(consumer != null) {
      consumer.subscribe(new ArrayList<>(subscribedTopics));
      logger.info("Update subscription topics={}", subscribedTopics);
    }
  }

  void setListener(Consumer<String> listener) { this.msgListener = listener; }

  boolean isUp() { return running.get(); }
}

