package lyf.reg.server.data;

import org.apache.commons.lang3.StringUtils;

public record Service(String namespace, String name, String protocol, String url, String[] ids, int size) {

  public static Service of(String raw) {

    String[] tokens = StringUtils.splitPreserveAllTokens(raw, '|');
    if(tokens == null || tokens.length != 5) {
      throw new IllegalStateException("Invalid service message, raw = [" + raw + "]");
    }

    int _size = StringUtils.split(tokens[4], ",").length;
    return new Service(tokens[0], tokens[1], tokens[2], tokens[3], StringUtils.split(tokens[4], ","), _size);
  }

  @Override
  public String toString() {
    return String.format("%s|%s|%s|%s|%s|%s", namespace, name, protocol, url, StringUtils.join(ids, ","), size);
  }

}
