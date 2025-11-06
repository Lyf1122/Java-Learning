package com.lyf.registrymonitor.proxy;

import com.lyf.registrymonitor.msg.MsgConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

/**
 * Dispatcher for Kafka consumed messages. It routes messages to registered consumers
 * whose regex pattern matches the message content.
 */
class KafkaMessageDispatcher {
  private static final Logger logger = LoggerFactory.getLogger(KafkaMessageDispatcher.class);
  // patternString -> consumers set
  private final Map<String, Set<MsgConsumer<String>>> patternConsumers = new ConcurrentHashMap<>();

  void register(String[] patterns, MsgConsumer<String> consumer) {
    if (patterns == null || consumer == null) return;
    for (String p : patterns) {
      patternConsumers.computeIfAbsent(p, k -> Collections.synchronizedSet(new LinkedHashSet<>()));
      patternConsumers.get(p).add(consumer);
    }
  }

  void dispatch(String message) {
    if (message == null) return;
    patternConsumers.forEach((patternStr, consumers) -> {
      try {
        Pattern pattern = Pattern.compile(patternStr, Pattern.DOTALL);
        if (pattern.matcher(message).matches()) {
          for (MsgConsumer<String> c : consumers) {
            try { c.process(message); } catch (Throwable ex) { logger.error("Consumer failed, pattern={}, msg={}", patternStr, message, ex); }
          }
        }
      } catch (Exception e) {
        logger.error("Invalid pattern: {}", patternStr, e);
      }
    });
  }
}

