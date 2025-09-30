package org.example.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

class KafkaConfigTest {

  @Test
  public void testProducerProperties() {
    Properties props = KafkaConfig.getProducerProperties();

    assertNotNull(props);
    assertEquals("localhost:9092", props.getProperty("bootstrap.servers"));
    assertEquals("org.apache.kafka.common.serialization.StringSerializer",
      props.getProperty("key.serializer"));
    assertEquals("org.apache.kafka.common.serialization.StringSerializer",
      props.getProperty("value.serializer"));
    assertEquals("all", props.getProperty("acks"));
    assertEquals("3", props.getProperty("retries"));
    assertEquals("true", props.getProperty("enable.idempotence"));
  }

}