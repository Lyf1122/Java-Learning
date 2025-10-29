package lyf.reg.server;

import lyf.reg.server.data.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;

public enum MsgQueue {

  Q ;

  private static final Logger logger = LoggerFactory.getLogger(MsgQueue.class);

  static MsgQueue ins() {
    return Q;
  }

  private final ConcurrentLinkedQueue<Message> queue = new ConcurrentLinkedQueue<>();

  void add(Message msg) {
    Optional.ofNullable(msg).ifPresent(m -> {
      queue.add(m);
      logger.debug("Added message [{}] to Queue, size=[{}]", m, size());
    });
  }

  Message poll() {
    Message msg = queue.poll();
    logger.debug("Polled message [{}] from Queue, size=[{}]", msg, size());
    return msg;
  }

  boolean isEmpty() {
    return queue.isEmpty();
  }

  boolean notEmpty() {
    return ! isEmpty();
  }

  int size() {
    return queue.size();
  }

  void clear() {
    queue.clear();
  }

}
