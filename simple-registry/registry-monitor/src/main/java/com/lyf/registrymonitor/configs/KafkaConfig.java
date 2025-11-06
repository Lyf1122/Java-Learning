package com.lyf.registrymonitor.configs;

public record KafkaConfig(
  boolean enabled,
  String bootstrapServers,
  String groupId,
  String clientId,
  String autoOffsetReset,
  int pollTimeoutMs
) {
  public static Builder of() { return new Builder(); }
  public static class Builder {
    private boolean enabled;
    private String bootstrapServers;
    private String groupId;
    private String clientId;
    private String autoOffsetReset = "latest";
    private int pollTimeoutMs = 1000;
    public Builder enabled(boolean enabled) { this.enabled = enabled; return this; }
    public Builder bootstrapServers(String bootstrapServers) { this.bootstrapServers = bootstrapServers; return this; }
    public Builder groupId(String groupId) { this.groupId = groupId; return this; }
    public Builder clientId(String clientId) { this.clientId = clientId; return this; }
    public Builder autoOffsetReset(String autoOffsetReset) { this.autoOffsetReset = autoOffsetReset; return this; }
    public Builder pollTimeoutMs(int pollTimeoutMs) { this.pollTimeoutMs = pollTimeoutMs; return this; }
    public KafkaConfig build() { return new KafkaConfig(enabled, bootstrapServers, groupId, clientId, autoOffsetReset, pollTimeoutMs); }
  }
}

