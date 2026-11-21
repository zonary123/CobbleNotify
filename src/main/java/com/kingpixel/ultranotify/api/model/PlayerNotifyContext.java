package com.kingpixel.ultranotify.api.model;

import lombok.Getter;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Snapshot context viewmodel representing a player's notification status, preferences, and active rules.
 */
@Getter
public class PlayerNotifyContext {

  private final UUID playerUuid;
  private final String playerName;
  private final String currentWorld;
  private final boolean worldBlacklisted;
  private final PlayerNotifyPreferencesView preferences;
  private final List<NotificationRuleView> activeRules;

  public PlayerNotifyContext(UUID playerUuid,
                             String playerName,
                             String currentWorld,
                             boolean worldBlacklisted,
                             PlayerNotifyPreferencesView preferences,
                             List<NotificationRuleView> activeRules) {
    this.playerUuid = playerUuid;
    this.playerName = playerName != null ? playerName : "";
    this.currentWorld = currentWorld != null ? currentWorld : "";
    this.worldBlacklisted = worldBlacklisted;
    this.preferences = preferences;
    this.activeRules = activeRules != null ? Collections.unmodifiableList(List.copyOf(activeRules)) : Collections.emptyList();
  }
}
