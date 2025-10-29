package lyf.reg.server;

import lyf.reg.server.data.Message;
import lyf.reg.server.data.Service;
import lyf.reg.server.util.CounterUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Arrays;
import java.util.Base64;
import java.util.Optional;

public class Channel extends Thread {

  private static final Logger logger = LoggerFactory.getLogger(Channel.class);

  private final int id;
  private final Socket socket;
  private final BufferedReader reader;
  private final PrintWriter writer;

  private final RegServer server;

  private Long lastPingTs = System.currentTimeMillis();
  private String namespace;
  private String gid;
  private Service service;

  public Channel(RegServer server, String namespace, String gid, int id, Socket socket) throws IOException {

    this.namespace = namespace;
    this.gid = gid;
    this.id = id;
    this.socket = socket;
    this.server = server;

    this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    this.writer = new PrintWriter(socket.getOutputStream(), true);
  }

  @Override
  public void run() {

    logger.info("[{}] [{}] STARTING a client channel ... ", id, gid);
    CounterUtil counter = CounterUtil.of();
    boolean isOpen = true;
    while (isOpen) {
      try {
        logger.debug("[{}] [{}] WAITING input from client ... ", id, gid);
        String b64Txt = reader.readLine();
        if(b64Txt != null) {
          String raw = new String(Base64.getDecoder().decode(b64Txt));
          if(StringUtils.equals("ping", raw)) {
            lastPingTs = System.currentTimeMillis();
          } else {
            MsgQueue.ins().add(Message.of(namespace, id, raw));
          }
          counter.reset();
          logger.debug("[{}] RECEIVING message from client ... msg = [{}], There are [{}] pending messages.", id, raw, MsgQueue.ins().size());
        } else if (counter.get() > 5) {
          logger.info("[{}] RECEIVING EMPTY message from client ... [{}]", id, counter.get());
          counter.count();
          try { Thread.sleep(10000L); } catch (InterruptedException ignored) {}
        } else {
          logger.info("[{}] RECEIVING EMPTY message from client ... [{}]", id, counter.get());
          counter.count();
        }

        isOpen = socket.isConnected() && !socket.isInputShutdown() && !socket.isOutputShutdown()
          && (System.currentTimeMillis() - lastPingTs < 60 * 1000L);

      } catch (IOException ioe) {
        logger.error("Unexpected Error ...", ioe);
        try { socket.close(); } catch (IOException ioe1) { logger.error("Unexpected Error during close socket.", ioe); }
        break;
      }
    }

    logger.warn("[{}] [{}] [{}] CLOSING a client channel ... ", id, gid, service);
    server.remove(id).flatMap(ConfigurationPool.ins()::unregisterService).ifPresent(
      configs -> Arrays.stream(configs).forEach(cfg -> server.sendConfig("cfg:" + cfg.topic(), cfg.raw(), id))
    );
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this, ToStringStyle.NO_CLASS_NAME_STYLE)
      .append("id", id)
      .append("namespace", namespace)
      .append("gid", gid)
      .toString();
  }

  public String namespace() {
    return namespace;
  }

  void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  void setGroupId(String gid) {
    this.gid = gid;
  }

  void setService(Service service) {
    this.service = service;
  }

  String groupId() {
    return this.gid;
  }

  Long lastPingTs() {
    return lastPingTs;
  }

  int id() {
    return id;
  }

  void sendMessage(String message) {
    String b64Txt = new String(Base64.getEncoder().encode(message.getBytes()));
    writer.println(b64Txt);
    writer.flush();
  }

  public Optional<Service> service() {
    return Optional.ofNullable(service);
  }

  void close() {
    try {
      if(!socket.isClosed()) {
        socket.close();
      }
    } catch (IOException e) {
      logger.error("Unexpected Error during close socket.", e);
    }
  }
}
