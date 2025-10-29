package lyf.reg.server;

import lyf.reg.server.data.Group;
import lyf.reg.server.data.Service;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class RegServer {

  private static final Logger logger = LoggerFactory.getLogger(RegServer.class);

  final int port;

  private ServerSocket server;

  private final Map<Integer, Channel> channels = new ConcurrentHashMap<>();
  private final Map<String, List<Channel>> groups = new ConcurrentHashMap<>();
  private final Map<String, Set<String>> groupsWithTopics = new ConcurrentHashMap<>();

  public RegServer(int port) {
    this.port = port;
    this.groups.put("NEW", new ArrayList<>());
  }

  public void up() {

    try {
      server = new ServerSocket(port);
      logger.info("SERVER started on port {}.", port);
      while (true) {
        Socket socket = server.accept();
        buildChannel(socket).start();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  Channel buildChannel(Socket socket) throws IOException {
    int id = genId();
    logger.info("A CLIENT (id={}) connected.", id);
    Channel ch = new Channel(this, null, "NEW", id, socket);
    channels.put(id, ch);
    groups.get("NEW").add(ch);
    return ch;
  }

  int genId() {
    int id;
    do {
      id = RandomUtils.nextInt(100000, 999999);
    } while (channels.containsKey(id));
    return id;
  }

  void down() {
    try {
      if(server != null && !server.isClosed()) server.close();
    } catch (IOException e) {
      throw new IllegalStateException("Failed to close Socket Server.", e);
    }
  }

  public int size() {
    return channels.size();
  }

  public void info(final int oriCid) {
    Channel ch = channels.get(oriCid);
    logger.info("SEND register server info to {}", ch);
    try {
      Thread.sleep(3000L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
    StringBuilder sb = new StringBuilder("[INFO]\n");
    if(ch != null) {
      channels.values().stream().map(this::formatChannel).forEach(s -> sb.append(s).append("\n"));
      groups.forEach((gid, chs) -> sb.append(formatGroups(gid, chs)).append("\n"));
      groupsWithTopics.forEach((gid, topics) -> sb.append(formatSubscribe(gid, topics)).append("\n"));
      send(sb.toString(), Collections.singletonList(ch));
    }
  }

  private String formatChannel(Channel ch) {
    return String.format("C|%s|%s|%s", ch.id(), ch.groupId(), ch.service().map(Service::toString).orElse("NA"));
  }

  private String formatGroups(String gid, List<Channel> chs) {
    return String.format("G|%s|%s", gid, StringUtils.join(chs.stream().map(Channel::id).toArray(Integer[]::new), ","));
  }

  private String formatSubscribe(String gid, Set<String> topics) {
    return String.format("S|%s|%s", gid, StringUtils.join(topics, ","));
  }

  void cleanup() {
    groups.get("NEW").stream()
      .filter(ch -> ch.lastPingTs() < System.currentTimeMillis() - 5 * 60 * 1000L)
      .map(Channel::id).forEach(this::remove);
  }

  public Optional<Service> remove(int id) {
    logger.info("[{}] CLOSING a client channel ... ", id);
    Channel ch = channels.get(id);
    Service svc = null;
    if(ch != null) {
      svc = ch.service().orElse(null);
      ch.close();
      channels.remove(id);
      groups.get(ch.groupId()).remove(ch);
    }
    logger.info("[{}] CLOSING Service svc ... [{}]", id, svc);
    return Optional.ofNullable(svc);
  }

  public void sendConfig(String topic, String message, final int oriCid) {
    logger.info("SEND config to others ... [{}] [{}] [{}]", oriCid, topic, message);
    groups.keySet().stream()
      .filter(gid -> !StringUtils.equals("NEW", gid)).filter(gid -> groupsWithTopics.get(gid).contains(topic))
      .forEach(gid -> sendAllExcept(message, groups.get(gid), oriCid));
  }

  public void send(String topic, String message, final int oriCid) {
    Channel ch = channels.get(oriCid);
    if(ch != null) {
      String oriGroup = ch.groupId();
      logger.info("SEND message to others ... [{}] [{}] [{}] [{}]", oriGroup, oriCid, topic, message);
      groups.keySet().stream()
        .filter(gid -> !StringUtils.equals("NEW", gid)).filter(gid -> !StringUtils.equals(oriGroup, gid))
        .filter(gid -> groupsWithTopics.get(gid).contains(topic))
        .forEach(gid -> send(message, groups.get(gid)));
    }
  }

  private void send(String message, List<Channel> chs) {
    logger.debug("SEND message to others ... [{}] {}", message, StringUtils.join(chs, "; "));
    if(!chs.isEmpty()) {
      int idx = RandomUtils.nextInt(0, chs.size());
      chs.get(idx).sendMessage(message);
    }
  }

  private void sendAllExcept(String message, List<Channel> chs, final int oriCid) {
    logger.debug("SEND message to others ... [{}] {}", message, StringUtils.join(chs, "; "));
    chs.stream().filter(ch -> ch.id() != oriCid).forEach(ch -> ch.sendMessage(message));
  }

  public void register(int cid, Service service) {
    Channel ch = channels.get(cid);
    String gid = service.namespace() + ":" + service.name();
    logger.info("Register channel {} to Group [{}].", ch, gid);
    if(ch != null) {
      groups.get(ch.groupId()).remove(ch);
      Group gp = Group.of(gid);
      ch.setNamespace(gp.namespace());
      ch.setGroupId(gid);
      ch.setService(service);
      groups.putIfAbsent(gid, new ArrayList<>());
      groups.get(gid).add(ch);
      groupsWithTopics.putIfAbsent(ch.groupId(), new HashSet<>());
    }
  }

  public void subscribe(int cid, String topic) {
    Channel ch = channels.get(cid);
    logger.info("Subscribe topic {} for Channel {}.", topic, ch);
    if(ch != null) {
      groupsWithTopics.get(ch.groupId()).add(topic);
    }
  }

  public void unsubscribe(int cid, String topic) {
    Channel ch = channels.get(cid);
    logger.info("Unsubscribe topic [{}] for Channel {}.", topic, ch);
    if(ch != null && groupsWithTopics.get(ch.groupId()) != null) {
      groupsWithTopics.get(ch.groupId()).remove(topic);
    }
  }

  void pingAll() {
    Map<Integer, Channel> temp = Map.copyOf(channels);
    temp.values().forEach(ch -> ch.sendMessage("ping"));
  }

}
