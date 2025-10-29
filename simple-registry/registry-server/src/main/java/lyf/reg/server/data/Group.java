package lyf.reg.server.data;

import org.apache.commons.lang3.StringUtils;

public record Group(String namespace, String service) {

  public static Group of(String raw) {
    String _ns = StringUtils.substringBefore(raw, ":");
    String _srv = StringUtils.substringAfter(raw, ":");
    return new Group(_ns, _srv);
  }

}
