package lyf.reg.server.data;

import org.apache.commons.lang3.StringUtils;

import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public record Config(String key, int type, String val) {

  public final static Pattern TOPIC_REGEX_PATTERN = Pattern.compile("^([a-z-$]+\\.[0-9a-z-]+)\\.(.*)$");

  public final static String ROOT_BIZ_REGEX = "^\\$\\.biz\\.core[1-5]$";
  public final static String ROOT_CFG_REGEX = "^\\$\\.namespace\\.(secure|share|esp|core[1-5])$";
  public final static String CLUSTER_CFG_REGEX = "^[a-z-]+\\.[a-z-]+\\.(name|protocol|url|instances|size)$";
  public final static String SERVICE_CFG_REGEX = "^[a-z-]+\\.[a-z]+\\.hive\\.[0-9a-z-]+$";
  public final static String BIZ_CFG_REGEX = "^biz\\.[0-9]+\\.([A-Z0-9]{6}\\.)?[0-9a-z-]+$";
  public final static String EXP_CFG_REGEX = "^esp\\.[0-9a-z-]+\\.(hive\\.)?[0-9a-z-]+$";
  public final static String EXD_CFG_REGEX = "^esd\\.[a-z-]+\\.(region\\.[A-Z]{2}|[0-9a-z-]+)$";

  public final static String CLUSTER_CFG_REGEX_PATTERN = "^%s\\.%s\\.(name|protocol|url|instances|size)$";
  public final static String SERVICE_CFG_REGEX_PATTERN = "^%s\\.%s\\.hive\\.[0-9a-z-]+$";
  public final static String BIZ_CFG_REGEX_PATTERN = "^biz\\.%s\\.([A-Z0-9]{6}\\.)?[0-9a-z-]+$";
  public final static String EXP_CFG_REGEX_PATTERN = "^esp\\.%s\\.(hive\\.)?[0-9a-z-]+$";
  public final static String EXD_CFG_REGEX_PATTERN = "^esd\\.%s\\.(region\\.[A-Z]{2}|[0-9a-z-]+)$";

  public Config(String key, String val) {
    this(key, 1, val);
  }

  public static Config of(String raw) {

    String[] ts = StringUtils.splitPreserveAllTokens(raw, ":");
    if(ts == null || ts.length != 3 || !StringUtils.isNumeric(ts[1])) {
      throw new IllegalStateException("Invalid configuration data, raw=[" + raw + "].");
    }

    String _key = ts[0]; int _type = Integer.parseInt(ts[1]);
    String _val = decodeValue(_type, ts[2]);
    return new Config(_key, _type, _val);
  }

  public String topic() {
    Matcher m = TOPIC_REGEX_PATTERN.matcher(key);
    if(m.find()) return m.group(1);
    else throw new IllegalStateException("Cannot parse topic from key, [" + key + "].");
  }

  public String raw() {
    String _val = encodeValue(type, val);
    return key + ":" + type + ":" + _val;
  }

  public boolean isRootConfig() {
    return Pattern.matches(ROOT_CFG_REGEX, key);
  }

  public boolean isRootBizConfig() {
    return Pattern.matches(ROOT_BIZ_REGEX, key);
  }

  public boolean isClusterConfig() {
    return Pattern.matches(CLUSTER_CFG_REGEX, key);
  }

  public boolean isServiceConfig() {
    return Pattern.matches(String.format(SERVICE_CFG_REGEX), key);
  }

  public boolean isBusinessConfig() {
    return Pattern.matches(BIZ_CFG_REGEX, key);
  }

  public boolean isExternalServiceProviderConfig() {
    return Pattern.matches(EXP_CFG_REGEX, key);
  }

  public boolean isExternalServiceDomainConfig() {
    return Pattern.matches(EXD_CFG_REGEX, key);
  }

  private static String decodeValue(int type, String value) {
    String v = value;
    switch (type) {
      case 1 -> v = new String(Base64.getDecoder().decode(value.getBytes()));  // TEXT
      case 4 -> throw new UnsupportedOperationException();  // PASSWORD
    }
    return v;
  }

  private static String encodeValue(int type, String value) {
    String v = value;
    switch (type) {
      case 1 -> v = new String(Base64.getEncoder().withoutPadding().encode(value.getBytes()));
      case 4 -> throw new UnsupportedOperationException();
    }
    return v;
  }

}
