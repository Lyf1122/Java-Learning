package com.lyf.registrymonitor.proxy;

import com.lyf.registrymonitor.msg.MsgConsumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

public class SimpleRegistryProxy implements RegistryProxy{
  private static final Logger logger = LoggerFactory.getLogger(SimpleRegistryProxy.class);

  private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(100);
  private static final int REMOTE_PORT = 5757;

  static SimpleRegistryProxy ins;

  private boolean up = false;

  private final SimpleRegistryClient client;

  private final Map<String, Set<MsgConsumer<String>>> msgMap = new HashMap<>();

  SimpleRegistryProxy(SimpleRegistryClient client) {
    this.client = client;
    this.client.setListener(msg -> EXECUTOR.submit(() -> {
      try { get(msg); } catch (Throwable ex) { logger.error("failed to process message, msg = {}", msg, ex); }
    }));
  }

  public static SimpleRegistryProxy ins() {
    return ins;
  }

  @Override
  public void get(String msg) {
    logger.debug("Get Message from Service Registry ==> {}", msg);
    consume(msg);
  }

  private void consume(String msg) {
    msgMap.keySet().stream()
      .filter(key -> {
        Pattern p = Pattern.compile(key, Pattern.DOTALL);
        return p.matcher(msg).matches();
      }).map(msgMap::get).forEach(cns -> cns.forEach(cn -> cn.process(msg)));
  }

  @Override
  public void push(String cmd, String msg) {
    client.sendMsgToServer(cmd + "=" + msg);
  }

  @Override
  public void addMsgConsumer(String[] codes, MsgConsumer<String> consumer) {
    Optional.ofNullable(codes).ifPresent(
      cds -> Arrays.stream(cds)
        .map(cd -> { msgMap.putIfAbsent(cd, new LinkedHashSet<>()); return msgMap.get(cd);})
        .forEach(set -> set.add(consumer))
    );
  }

  @Override
  public void connect() {

    logger.info("Connecting to registry server ... ");

    client.up();
    EXECUTOR.submit(client);

    this.up = true;

    EXECUTOR.submit(() -> {
      while (up) {
        client.sendMsgToServer("ping");
        try { Thread.sleep(5000L); } catch (InterruptedException ignored) {
          logger.error("Unexpected Interrupted.");
        }
      }
    });

    EXECUTOR.submit(new Daemon(client));
  }

  private class Daemon implements Runnable {

    private final SimpleRegistryClient client;

    public Daemon(SimpleRegistryClient client) {
      this.client = client;
    }

    @Override
    public void run() {
      while (up) {
        boolean isDown = client.isDown();
        logger.trace("Checking client status = [{}]", !isDown);
        if(client.isDown()) {
          logger.warn("RegistryClient connection is DOWN, reconnecting ...");
          client.reset(); EXECUTOR.submit(client);
        }
        try {
          Thread.sleep(10000L);
        } catch (InterruptedException ignored) {
          logger.error("Unexpected Interrupted.");
        }
      }
    }
  }

  @Override
  public boolean isUp() {
    return client.isUp();
  }

  @Override
  public void disconnect() {

    logger.info("Disconnecting to registry server ... ");

    this.up = false;

    client.sendMsgToServer("exit");
    client.down();
  }

  @Override
  public void subscribe(String namespace, String msgType) {
    String topic = namespace + ":" + msgType;
    client.sendMsgToServer("sub=" + topic);
  }

  @Override
  public void subscribe(String topic) {
    client.sendMsgToServer("sub=" + topic);
  }

  @Override
  public void unsubscribe(String namespace, String msgType) {
    String topic = namespace + ":" + msgType;
    client.sendMsgToServer("unsub=" + topic);
  }

  @Override
  public void unsubscribe(String topic) {
    client.sendMsgToServer("unsub=" + topic);
  }

}
