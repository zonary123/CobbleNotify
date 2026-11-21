package com.kingpixel.ultranotify.api.model;

import lombok.Getter;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

/**
 * Immutable viewmodel representing a player's notification preferences in CobbleNotify.
 */
@Getter
public class PlayerNotifyPreferencesView {

  private final UUID playerUuid;
  private final boolean notificationsEnabled;
  private final boolean spawnAlertsEnabled;
  private final boolean caughtAlertsEnabled;
  private final boolean defeatAlertsEnabled;
  private final boolean tradeAlertsEnabled;
  private final Set<String> mutedRuleIds;

  public PlayerNotifyPreferencesView(UUID playerUuid,
                                     boolean notificationsEnabled,
                                     boolean spawnAlertsEnabled,
                                     boolean caughtAlertsEnabled,
                                     boolean defeatAlertsEnabled,
                                     boolean tradeAlertsEnabled,
                                     Set<String> mutedRuleIds) {
    this.playerUuid = playerUuid;
    this.notificationsEnabled = notificationsEnabled;
    this.spawnAlertsEnabled = spawnAlertsEnabled;
    this.caughtAlertsEnabled = caughtAlertsEnabled;
    this.defeatAlertsEnabled = defeatAlertsEnabled;
    this.tradeAlertsEnabled = tradeAlertsEnabled;
    this.mutedRuleIds = mutedRuleIds != null ? Collections.unmodifiableSet(Set.copyOf(mutedRuleIds)) : Collections.emptySet();
  }

  public boolean isRuleMuted(String ruleId) {
    if (ruleId == null || ruleId.isBlank()) return false;
    return mutedRuleIds.contains(ruleId.toLowerCase());
  }
}
