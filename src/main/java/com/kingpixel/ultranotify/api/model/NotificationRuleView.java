package com.kingpixel.ultranotify.api.model;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;

/**
 * Immutable viewmodel representing a configured notification rule in CobbleNotify.
 */
@Getter
public class NotificationRuleView {

  private final String identifier;
  private final int priority;
  private final boolean useProperties;
  private final String properties;
  private final boolean spawnEnabled;
  private final boolean caughtEnabled;
  private final boolean defeatEnabled;
  private final boolean tradeEnabled;
  private final boolean webhookEnabled;
  private final boolean webhookSpawnEnabled;
  private final boolean webhookCaughtEnabled;
  private final boolean webhookDefeatEnabled;
  private final boolean webhookTradeEnabled;
  private final Set<String> whitelistWorlds;
  private final Set<String> blacklistWorlds;

  public NotificationRuleView(String identifier,
                              int priority,
                              boolean useProperties,
                              String properties,
                              boolean spawnEnabled,
                              boolean caughtEnabled,
                              boolean defeatEnabled,
                              boolean tradeEnabled,
                              boolean webhookEnabled,
                              boolean webhookSpawnEnabled,
                              boolean webhookCaughtEnabled,
                              boolean webhookDefeatEnabled,
                              boolean webhookTradeEnabled,
                              Set<String> whitelistWorlds,
                              Set<String> blacklistWorlds) {
    this.identifier = identifier != null ? identifier : "";
    this.priority = priority;
    this.useProperties = useProperties;
    this.properties = properties != null ? properties : "";
    this.spawnEnabled = spawnEnabled;
    this.caughtEnabled = caughtEnabled;
    this.defeatEnabled = defeatEnabled;
    this.tradeEnabled = tradeEnabled;
    this.webhookEnabled = webhookEnabled;
    this.webhookSpawnEnabled = webhookSpawnEnabled;
    this.webhookCaughtEnabled = webhookCaughtEnabled;
    this.webhookDefeatEnabled = webhookDefeatEnabled;
    this.webhookTradeEnabled = webhookTradeEnabled;
    this.whitelistWorlds = whitelistWorlds != null ? Collections.unmodifiableSet(Set.copyOf(whitelistWorlds)) : Collections.emptySet();
    this.blacklistWorlds = blacklistWorlds != null ? Collections.unmodifiableSet(Set.copyOf(blacklistWorlds)) : Collections.emptySet();
  }
}
