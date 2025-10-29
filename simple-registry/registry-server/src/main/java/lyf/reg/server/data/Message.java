package lyf.reg.server.data;

import org.apache.commons.lang3.StringUtils;

public record Message(String namespace, int cid, String cmd, String msg) {

  public static final String CMD_REGISTER = "register";
  public static final String CMD_SUBSCRIBE = "sub";
  public static final String CMD_UNSUBSCRIBE = "unsub";
  public static final String CMD_INFO = "info";
  public static final String CMD_EVENT = "evn";
  public static final String CMD_TASK = "tsk";
  public static final String CMD_CONF = "conf";
  public static final String CMD_EXIT = "exit";
  public static final String CMD_TEST = "test";

  public static Message of(String namespace, int cid, String raw) {
    String _cmd = StringUtils.substringBefore(raw, "=");
    String _msg = StringUtils.substringAfter(raw, "=");
    return new Message(namespace, cid, _cmd, _msg);
  }

  public String  topic(String type) {
    return namespace + ":" + type;
  }

}
