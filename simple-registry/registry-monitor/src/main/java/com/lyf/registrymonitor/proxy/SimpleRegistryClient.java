package com.lyf.registrymonitor.proxy;

import com.lyf.registrymonitor.configs.ClusterConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Base64;
import java.util.function.Consumer;

public class SimpleRegistryClient extends Thread{
  private static final Logger logger = LoggerFactory.getLogger(SimpleRegistryClient.class);
  private Consumer<String> msgListener;
  private final String server;
  private final int port;
  private final ClusterConfig config;

  private Socket socket;
  private PrintWriter writer;

  private boolean isOpen;
  private long lastPingTs = System.currentTimeMillis();

  public SimpleRegistryClient(String server, int port, ClusterConfig config) {
    this.server = server;
    this.port = port;
    this.config = config;
  }

  @Override
  public void run() {

    if(socket == null) {
      throw new IllegalStateException("There is no available connection.");
    }

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
      while (isOpen) {
        String b64Txt = reader.readLine();
        if(b64Txt != null) {
          String msg = new String(Base64.getDecoder().decode(b64Txt));
          logger.trace("Receive msg [{}] from service registry.", msg);
          if(StringUtils.equals(msg, "ping")) lastPingTs = System.currentTimeMillis();
          else if(StringUtils.isNotBlank(msg)) msgListener.accept(msg);
        }
      }
    } catch (IOException e) {
      logger.error("Unexpected IOException ...", e);
      try {
        socket.close();
        isOpen = false;
      } catch (IOException ioe) { logger.error("Unexpected Error during close socket.", ioe); }
    } catch (Exception e) {
      logger.error("Unexpected Exception ...", e);
    }
  }

  synchronized void reset() {
    try { down(); } catch (Throwable e) { logger.error("Failed to Stop Register Proxy."); }
    try { up(); } catch (Throwable e) { logger.error("Failed to Start Register Proxy."); }
  }

  synchronized void up() {

    try {
      logger.info("server=[{}], port=[{}]", server, port);
      socket = new Socket(server, port);
      writer = new PrintWriter(socket.getOutputStream(), true);
      sendMsgToServer("register=" + String.format("%s|%s|%s|%s|%s", config.namespace(), config.srvName(), "http", config.serviceUrl(), config.appName()));
      lastPingTs = System.currentTimeMillis();
    } catch (IOException e) {
      logger.error("Failed to start up Console Client, {}:{}", server, port);
      throw new IllegalStateException("Failed to start up Register Proxy.");
    }
    isOpen = true;
  }

  synchronized void down() {

    isOpen = false;
    try {
      if(socket != null) socket.close();
    } catch (IOException e) {
      logger.warn("IOException occurred during closing socket for {}:{}. It might be due to socket closed at server end.", server, port);
    }
    if(writer != null) writer.close();
  }

  void sendMsgToServer(String msg) {
    if(writer == null) {
      throw new IllegalStateException("There is no available socket connection.");
    }
    if(StringUtils.isNotBlank(msg)) {
      logger.trace("Send msg [{}] to server.", msg);
      String b64Txt = new String(Base64.getEncoder().encode(msg.getBytes()));
      writer.println(b64Txt);
      writer.flush();
    }
  }

  public void setListener(Consumer<String> msgListener) {
    this.msgListener = msgListener;
  }

  boolean isUp() {
    return socket != null && !socket.isClosed() && System.currentTimeMillis() - lastPingTs < 60 * 1000L;
  }

  boolean isDown() {
    return !isUp();
  }

}
