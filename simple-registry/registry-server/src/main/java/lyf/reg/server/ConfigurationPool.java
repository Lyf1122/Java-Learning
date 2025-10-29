package lyf.reg.server;

import lyf.reg.server.data.Config;
import lyf.reg.server.data.Service;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Pattern;

import static lyf.reg.server.data.Config.*;

public class ConfigurationPool {

  private static final Logger logger = LoggerFactory.getLogger(ConfigurationPool.class);
  private static final ConfigurationPool INS = new ConfigurationPool();

  private final Map<String, String> configs = Collections.synchronizedSortedMap(new TreeMap<>());

  public static ConfigurationPool ins() {
    return INS;
  }

  public Optional<Config[]> addRegisterService(Service service) {

    String prefix = service.namespace() + "." + service.name();
    List<Config> list = new LinkedList<>();

    configs.put(prefix + ".name", service.name()); list.add(new Config(prefix + ".name", service.name()));
    configs.put(prefix + ".protocol", service.protocol()); list.add(new Config(prefix + ".protocol", service.protocol()));
    configs.put(prefix + ".url", service.url()); list.add(new Config(prefix + ".url", service.url()));

    return addInstance(prefix, service.ids(), list) ? Optional.of(list.toArray(new Config[0])) : Optional.empty();
  }

  private boolean addInstance(String prefix, String[] ids, List<Config> configList) {

    String instances = configs.get(prefix + ".instances");
    List<String> oriIds = StringUtils.isBlank(instances)
      ? new LinkedList<>()
      : new LinkedList<>(List.of(StringUtils.split(instances, ",")));

    boolean isUpdated = false;
    if(ids != null) {
      String[] newIds = Arrays.stream(ids).filter(id -> ! oriIds.contains(id)).toArray(String[]::new);
      if(newIds.length > 0) {
        oriIds.addAll(List.of(newIds));
        collectServiceInstancesAndSizeConfig(prefix, oriIds, configList);
        isUpdated = true;
      }
    }
    return isUpdated;
  }

  public Optional<Config[]> unregisterService(Service service) {

    logger.info("==> Unregister Service, service = [{}]", service);

    String prefix = service.namespace() + "." + service.name();
    List<Config> list = new LinkedList<>();
    return removeInstance(prefix, service.ids(), list) ? Optional.of(list.toArray(new Config[0])) : Optional.empty();
  }

  private boolean removeInstance(String prefix, String[] ids, List<Config> configList) {

    logger.info("==> [START] removeInstance, [{}] [{}] [{}]", prefix, StringUtils.join(ids, ","), configList.size());
    String instances = configs.get(prefix + ".instances");
    List<String> oriIds = StringUtils.isBlank(instances)
      ? new LinkedList<>()
      : new LinkedList<>(List.of(StringUtils.split(instances, ",")));

    boolean isUpdated = false;
    if(ids != null) {
      String[] idsToBeRemoved = Arrays.stream(ids).filter(oriIds::contains).toArray(String[]::new);
      if(idsToBeRemoved.length > 0) {
        Arrays.stream(idsToBeRemoved).forEach(oriIds::remove);
        collectServiceInstancesAndSizeConfig(prefix, oriIds, configList);
        isUpdated = true;
      }
    }
    return isUpdated;
  }

  private void collectServiceInstancesAndSizeConfig(String prefix, List<String> list, List<Config> configList) {
    String updatedInstances = StringUtils.join(list, ",");
    configs.put(prefix + ".instances", updatedInstances);
    configs.put(prefix + ".size", list.size() + "");
    configList.add(new Config(prefix + ".instances", updatedInstances));
    configList.add(new Config(prefix + ".size", list.size() + ""));
  }

  public Optional<Config> addConfig(Config config) {

    if(config == null || isNotChanged(config)) return Optional.empty();

    if(config.isRootConfig()) addRootConfig(config);
    else if(config.isRootBizConfig()) addRootBizConfig(config);
    else if(config.isClusterConfig()) addClusterConfig(config);
    else if(config.isServiceConfig()) addServiceConfig(config);
    else if(config.isBusinessConfig()) addBusinessConfig(config);
    else if(config.isExternalServiceProviderConfig()) addExternalServiceProviderConfig(config);
    else if(config.isExternalServiceDomainConfig()) addExternalServiceDomainConfig(config);
    else {
      throw new IllegalStateException("Unexpected config data, key=[" + config.key() + "].");
    }

    return Optional.of(config);
  }

  private boolean isNotChanged(Config config) {
    return configs.containsKey(config.key()) && StringUtils.equals(configs.get(config.key()), config.val());
  }

  public void addRootConfig(Config config) {
    addConfig("Root", ROOT_CFG_REGEX, config);
  }

  public void addRootBizConfig(Config config) {
    addConfig("RootBiz", ROOT_BIZ_REGEX, config);
  }

  public void addClusterConfig(Config config) {

    addConfig("Cluster", CLUSTER_CFG_REGEX, config);
  }

  public void addServiceConfig(Config config) {

    addConfig("Service", SERVICE_CFG_REGEX, config);
  }

  public void addBusinessConfig(Config config) {

    addConfig("Business", BIZ_CFG_REGEX, config);
  }

  public void addExternalServiceProviderConfig(Config config) {

    addConfig("ESP", EXP_CFG_REGEX, config);
  }

  public void addExternalServiceDomainConfig(Config config) {

    addConfig("ESD", EXD_CFG_REGEX, config);
  }

  private void addConfig(String type, String regex, Config config) {
    String key = config.key();
    String val = config.val();
    if(StringUtils.isNotBlank(key) && Pattern.matches(regex, key)) {
      configs.put(key, val);
    } else {
      throw new IllegalStateException("Invalid " + type + " config item, key=[" + key + "].");
    }
  }

  Config[] getRootConfigs() {
    return getConfigurations(ROOT_CFG_REGEX);
  }

  Config[] getClusterConfigs(String namespace, String service) {
    return getConfigurations(String.format(CLUSTER_CFG_REGEX_PATTERN, namespace, service));
  }

  Config[] getServiceConfigs(String namespace, String service) {
    return getConfigurations(String.format(SERVICE_CFG_REGEX_PATTERN, namespace, service));
  }

  Config[] getBusinessConfigs(String bizId) {
    return getConfigurations(String.format(BIZ_CFG_REGEX_PATTERN, bizId));
  }

  Config[] getExternalServiceProviderConfigs(String espNameAccount) {
    return getConfigurations(String.format(EXP_CFG_REGEX_PATTERN, espNameAccount));
  }

  Config[] getExternalServiceDomainConfigs(String domain) {
    return getConfigurations(String.format(EXD_CFG_REGEX_PATTERN, domain));
  }

  Service getService(String namespace, String service) {
    String name = getValue(String.format("%s.%s.name", namespace, service)).orElse(null);
    String protocol = getValue(String.format("%s.%s.protocol", namespace, service)).orElse(null);
    String url = getValue(String.format("%s.%s.url", namespace, service)).orElse(null);
    String[] ids = getValue(String.format("%s.%s.instances", namespace, service)).map(v -> StringUtils.split(v, ",")).orElse(new String[0]);
    int size = getValue(String.format("%s.%s.size", namespace, service)).map(Integer::valueOf).orElse(0);
    return new Service(namespace, name, protocol, url, ids, size);
  }

  Optional<String> getValue(String key) {
    return Optional.ofNullable(configs.get(key));
  }

  Config[] getConfigurations(String regex) {
    return configs.keySet().stream().filter(key -> Pattern.matches(regex, key))
      .map(key -> new Config(key, configs.get(key))).toArray(Config[]::new);
  }

}
