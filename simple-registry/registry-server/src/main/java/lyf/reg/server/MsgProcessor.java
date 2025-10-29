package lyf.reg.server;

import lyf.reg.server.data.Config;
import lyf.reg.server.data.Message;
import lyf.reg.server.data.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import static lyf.reg.server.data.Message.*;


public class MsgProcessor implements Runnable {

  private static final Logger logger = LoggerFactory.getLogger(MsgProcessor.class);

  private final RegServer server;
  private boolean on;

  public MsgProcessor(RegServer server) {
    this.on = true;
    this.server = server;
  }

  @Override
  public void run() {
    logger.info("Message Processor startup ...");
    synchronized(MsgQueue.ins()) {
      while (this.on) {
        MsgQueue Q = MsgQueue.ins();
        try {
          if(Q.notEmpty()) process(Q.poll());
        } catch (Exception e) {
          logger.error("Something wrong occurred during processing ... ", e);
        }
      }
    }
  }

  synchronized void on() {
    this.on = true;
  }

  synchronized void off() {
    this.on = false;
  }

  private void process(Message msg) {
    logger.info("PROCESSING message ... [{}]", msg);
    switch (msg.cmd()) {
      case CMD_REGISTER -> {
        Service service = Service.of(msg.msg());
        server.register(msg.cid(), service);
        ConfigurationPool.ins().addRegisterService(service).ifPresent(
          configs -> Arrays.stream(configs).forEach(
            cfg -> server.sendConfig("cfg:" + cfg.topic(), cfg.raw(), msg.cid())
          ));
      }
      case CMD_SUBSCRIBE -> server.subscribe(msg.cid(), msg.msg());
      case CMD_UNSUBSCRIBE -> server.unsubscribe(msg.cid(), msg.msg());
      case CMD_INFO -> server.info(msg.cid());
      case CMD_TASK -> server.send(msg.topic("task"), msg.msg(), msg.cid());
      case CMD_EVENT -> server.send(msg.topic("event"), msg.msg(), msg.cid());
      case CMD_CONF -> ConfigurationPool.ins().addConfig(Config.of(msg.msg())).ifPresent(cfg ->
        server.sendConfig("cfg:" + cfg.topic(), msg.msg(), msg.cid())
      );
      case CMD_EXIT -> server.remove(msg.cid()).flatMap(ConfigurationPool.ins()::unregisterService).ifPresent(
        configs -> Arrays.stream(configs).forEach(cfg -> server.sendConfig("cfg:" + cfg.topic(), cfg.raw(), msg.cid()))
      );
      case CMD_TEST -> logger.info(" TEST ==> {} ", msg.msg());
    }
  }
}
