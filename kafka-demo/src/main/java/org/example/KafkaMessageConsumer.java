package org.example;

import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.example.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaMessageConsumer {
  private static final Logger logger = LoggerFactory.getLogger(KafkaMessageConsumer.class);

  private final KafkaConsumer<String, String> consumer;
  private final String topic;
  private final AtomicBoolean running = new AtomicBoolean(false);

  public KafkaMessageConsumer(String topic, String groupId) {
    Properties props = KafkaConfig.getConsumerProperties(groupId);
    this.consumer = new KafkaConsumer<>(props);
    this.topic = topic;
  }

  public void start() {
    consumer.subscribe(Collections.singletonList(topic));
    running.set(true);

    // 启动消费线程
    Thread consumerThread = new Thread(this::consumeMessages);
    consumerThread.setName("KafkaConsumer-" + topic);
    consumerThread.start();
  }

  private void consumeMessages() {
    try {
      while (running.get()) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));

        if (records.isEmpty()) {
          continue;
        }

        records.forEach(record -> {
          try {
            // 处理消息
            processMessage(record.key(), record.value(), record.partition(), record.offset());
          } catch (Exception e) {
            logger.error("Failed to process message from topic {}", topic, e);
          }
        });

        // 手动提交偏移量
        consumer.commitSync();
      }
    } finally {
      consumer.close();
    }
  }

  private void processMessage(String key, String value, int partition, long offset) {
    // 这里是实际的消息处理逻辑
    logger.info("Received message [key={}, value={}, partition={}, offset={}]",
      key, value, partition, offset);

    // 根据消息内容执行相应的业务逻辑
    // 例如: 注册服务、订阅主题等
  }

  public void stop() {
    running.set(false);
  }
}
