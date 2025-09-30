package org.example;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.example.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class KafkaMsgProducer {
  private static final Logger logger = LoggerFactory.getLogger(KafkaMsgProducer.class);

  private final KafkaProducer<String, String> producer;
  private final String topic;

  public KafkaMsgProducer(String topic) {
    Properties props = KafkaConfig.getProducerProperties();
    this.producer = new KafkaProducer<>(props);
    this.topic = topic;
  }

  public void sendMessage(String key, String message) {
    ProducerRecord<String, String> record = new ProducerRecord<>(topic, key, message);

    try {
      Future<RecordMetadata> future = producer.send(record);
      RecordMetadata metadata = future.get(); // 同步等待发送结果
      logger.info("Message sent successfully to topic {} partition {} at offset {}",
        metadata.topic(), metadata.partition(), metadata.offset());
    } catch (InterruptedException | ExecutionException e) {
      logger.error("Failed to send message to Kafka", e);
    }
  }

  public void close() {
    producer.close();
  }
}
