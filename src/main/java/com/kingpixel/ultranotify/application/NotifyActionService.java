package com.kingpixel.ultranotify.application;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.api.action.NotifyActionResult;
import com.kingpixel.ultranotify.api.model.PlayerNotifyPreferencesView;
import com.kingpixel.ultranotify.config.Notifications;
import com.kingpixel.ultranotify.models.Notification;
import com.kingpixel.ultranotify.models.enums.Actions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Application service providing headless actions for notification triggers, player preferences, and rule management.
 */
public class NotifyActionService {

  private static final NotifyActionService INSTANCE = new NotifyActionService();

  private final Map<UUID, MutablePreferences> playerPreferences = new ConcurrentHashMap<>();

  public static NotifyActionService getInstance() {
    return INSTANCE;
  }

  static class MutablePreferences {
    boolean notificationsEnabled = true;
    boolean spawnAlertsEnabled = true;
    boolean caughtAlertsEnabled = true;
    boolean defeatAlertsEnabled = true;
    boolean tradeAlertsEnabled = true;
    final Set<String> mutedRules = ConcurrentHashMap.newKeySet();

    PlayerNotifyPreferencesView toView(UUID playerUuid) {
      return new PlayerNotifyPreferencesView(
          playerUuid,
          notificationsEnabled,
          spawnAlertsEnabled,
          caughtAlertsEnabled,
          defeatAlertsEnabled,
          tradeAlertsEnabled,
          new HashSet<>(mutedRules)
      );
    }
  }

  PlayerNotifyPreferencesView getPreferencesView(UUID playerUuid) {
    if (playerUuid == null) {
      return new PlayerNotifyPreferencesView(null, true, true, true, true, true, Collections.emptySet());
    }
    return playerPreferences.computeIfAbsent(playerUuid, k -> new MutablePreferences()).toView(playerUuid);
  }

  /**
   * Reloads all CobbleNotify configurations, language files, and notification rules headlessly.
   *
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> reloadConfigs() {
    return CompletableFuture.supplyAsync(() -> {
      UltraNotify.load();
      return NotifyActionResult.success("CobbleNotify configuration reloaded.");
    }).exceptionally(ex -> NotifyActionResult.failure(NotifyActionResult.Status.ERROR, ex.getMessage()));
  }

  /**
   * Triggers a caught notification across matching rules or a specific rule.
   *
   * @param player  the player who caught the Pokemon
   * @param pokemon the caught Pokemon
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> triggerCaughtNotification(ServerPlayerEntity player, Pokemon pokemon) {
    if (player == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Player is offline."));
    }
    if (pokemon == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.invalidArgument("Pokemon cannot be null."));
    }

    return CompletableFuture.supplyAsync(() -> {
      if (UltraNotify.config != null && UltraNotify.config.getWorldFilter().isBlackListed(player.getServerWorld())) {
        return NotifyActionResult.failure(NotifyActionResult.Status.WORLD_BLACKLISTED, "Player world is blacklisted.");
      }
      for (Notification notification : Notifications.NOTIFICATIONS) {
        if (notification.getWorldFilter().isBlackListed(player.getServerWorld())) continue;
        if (notification.computeCaught(pokemon, player)) {
          return NotifyActionResult.success("Caught notification dispatched.", notification.getIdentifier());
        }
      }
      return NotifyActionResult.failure(NotifyActionResult.Status.RULE_NOT_FOUND, "No matching notification rule triggered.");
    }).exceptionally(ex -> NotifyActionResult.failure(NotifyActionResult.Status.ERROR, ex.getMessage()));
  }

  /**
   * Triggers a defeat notification across matching rules.
   *
   * @param player  the player who defeated the Pokemon
   * @param pokemon the defeated Pokemon
   * @param entity  the optional Pokemon entity
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> triggerDefeatNotification(ServerPlayerEntity player,
                                                                         Pokemon pokemon,
                                                                         PokemonEntity entity) {
    if (player == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Player is offline."));
    }
    if (pokemon == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.invalidArgument("Pokemon cannot be null."));
    }

    return CompletableFuture.supplyAsync(() -> {
      if (UltraNotify.config != null && UltraNotify.config.getWorldFilter().isBlackListed(player.getServerWorld())) {
        return NotifyActionResult.failure(NotifyActionResult.Status.WORLD_BLACKLISTED, "Player world is blacklisted.");
      }
      for (Notification notification : Notifications.NOTIFICATIONS) {
        if (notification.getWorldFilter().isBlackListed(player.getServerWorld())) continue;
        if (notification.computeDefeat(pokemon, player, entity)) {
          return NotifyActionResult.success("Defeat notification dispatched.", notification.getIdentifier());
        }
      }
      return NotifyActionResult.failure(NotifyActionResult.Status.RULE_NOT_FOUND, "No matching notification rule triggered.");
    }).exceptionally(ex -> NotifyActionResult.failure(NotifyActionResult.Status.ERROR, ex.getMessage()));
  }

  /**
   * Triggers a spawn notification across matching rules.
   *
   * @param pokemonEntity the spawned Pokemon entity
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> triggerSpawnNotification(PokemonEntity pokemonEntity) {
    if (pokemonEntity == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.invalidArgument("PokemonEntity cannot be null."));
    }

    return CompletableFuture.supplyAsync(() -> {
      if (UltraNotify.config != null && UltraNotify.config.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) {
        return NotifyActionResult.failure(NotifyActionResult.Status.WORLD_BLACKLISTED, "Entity world is blacklisted.");
      }
      for (Notification notification : Notifications.NOTIFICATIONS) {
        if (notification.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) continue;
        if (notification.computeSpawn(pokemonEntity)) {
          return NotifyActionResult.success("Spawn notification dispatched.", notification.getIdentifier());
        }
      }
      return NotifyActionResult.failure(NotifyActionResult.Status.RULE_NOT_FOUND, "No matching notification rule triggered.");
    }).exceptionally(ex -> NotifyActionResult.failure(NotifyActionResult.Status.ERROR, ex.getMessage()));
  }

  /**
   * Triggers a trade notification across matching rules.
   *
   * @param player1  the first trade participant
   * @param pokemon1 the first Pokemon
   * @param player2  the second trade participant
   * @param pokemon2 the second Pokemon
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> triggerTradeNotification(ServerPlayerEntity player1,
                                                                        Pokemon pokemon1,
                                                                        ServerPlayerEntity player2,
                                                                        Pokemon pokemon2) {
    if (player1 == null || player2 == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Trade participant is offline."));
    }
    if (pokemon1 == null || pokemon2 == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.invalidArgument("Traded Pokemon cannot be null."));
    }

    return CompletableFuture.supplyAsync(() -> {
      if (UltraNotify.config != null && UltraNotify.config.getWorldFilter().isBlackListed(player1.getServerWorld())) {
        return NotifyActionResult.failure(NotifyActionResult.Status.WORLD_BLACKLISTED, "Player world is blacklisted.");
      }
      for (Notification notification : Notifications.NOTIFICATIONS) {
        if (notification.getWorldFilter().isBlackListed(player1.getServerWorld())) continue;
        if (notification.computeTrade(pokemon1, pokemon2, player1, player2)) {
          return NotifyActionResult.success("Trade notification dispatched.", notification.getIdentifier());
        }
      }
      return NotifyActionResult.failure(NotifyActionResult.Status.RULE_NOT_FOUND, "No matching notification rule triggered.");
    }).exceptionally(ex -> NotifyActionResult.failure(NotifyActionResult.Status.ERROR, ex.getMessage()));
  }

  /**
   * Toggles global notification preference for a player.
   *
   * @param player the player
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> togglePlayerNotifications(ServerPlayerEntity player) {
    if (player == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Player is offline."));
    }
    return togglePlayerNotifications(player.getUuid());
  }

  /**
   * Toggles global notification preference for a player UUID.
   *
   * @param playerUuid the player UUID
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> togglePlayerNotifications(UUID playerUuid) {
    if (playerUuid == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Player UUID is null."));
    }
    MutablePreferences prefs = playerPreferences.computeIfAbsent(playerUuid, k -> new MutablePreferences());
    prefs.notificationsEnabled = !prefs.notificationsEnabled;
    boolean state = prefs.notificationsEnabled;
    return CompletableFuture.completedFuture(
        NotifyActionResult.preferenceUpdated("Notifications " + (state ? "enabled" : "disabled") + ".", "global")
    );
  }

  /**
   * Toggles a specific action alert preference (SPAWN, CAUGHT, DEFEAT, TRADE) for a player.
   *
   * @param player the player
   * @param action the notification action type
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> toggleActionPreference(ServerPlayerEntity player, Actions action) {
    if (player == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Player is offline."));
    }
    return toggleActionPreference(player.getUuid(), action);
  }

  /**
   * Toggles a specific action alert preference (SPAWN, CAUGHT, DEFEAT, TRADE) for a player UUID.
   *
   * @param playerUuid the player UUID
   * @param action     the notification action type
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> toggleActionPreference(UUID playerUuid, Actions action) {
    if (playerUuid == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Player UUID is null."));
    }
    if (action == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.invalidArgument("Action cannot be null."));
    }
    MutablePreferences prefs = playerPreferences.computeIfAbsent(playerUuid, k -> new MutablePreferences());
    boolean newState;
    switch (action) {
      case SPAWN -> {
        prefs.spawnAlertsEnabled = !prefs.spawnAlertsEnabled;
        newState = prefs.spawnAlertsEnabled;
      }
      case CAUGHT -> {
        prefs.caughtAlertsEnabled = !prefs.caughtAlertsEnabled;
        newState = prefs.caughtAlertsEnabled;
      }
      case DEFEAT -> {
        prefs.defeatAlertsEnabled = !prefs.defeatAlertsEnabled;
        newState = prefs.defeatAlertsEnabled;
      }
      case TRADE -> {
        prefs.tradeAlertsEnabled = !prefs.tradeAlertsEnabled;
        newState = prefs.tradeAlertsEnabled;
      }
      default -> {
        return CompletableFuture.completedFuture(NotifyActionResult.invalidArgument("Unknown action."));
      }
    }
    return CompletableFuture.completedFuture(
        NotifyActionResult.preferenceUpdated(action.name() + " alerts " + (newState ? "enabled" : "disabled") + ".", action.name())
    );
  }

  /**
   * Toggles mute state for a specific notification rule identifier for a player.
   *
   * @param playerUuid the player UUID
   * @param ruleId     the notification rule identifier
   * @return future with the action result
   */
  public CompletableFuture<NotifyActionResult> toggleRuleMute(UUID playerUuid, String ruleId) {
    if (playerUuid == null) {
      return CompletableFuture.completedFuture(NotifyActionResult.offline("Player UUID is null."));
    }
    if (ruleId == null || ruleId.isBlank()) {
      return CompletableFuture.completedFuture(NotifyActionResult.invalidArgument("Rule ID cannot be empty."));
    }
    MutablePreferences prefs = playerPreferences.computeIfAbsent(playerUuid, k -> new MutablePreferences());
    String key = ruleId.toLowerCase();
    boolean muted;
    if (prefs.mutedRules.contains(key)) {
      prefs.mutedRules.remove(key);
      muted = false;
    } else {
      prefs.mutedRules.add(key);
      muted = true;
    }
    return CompletableFuture.completedFuture(
        NotifyActionResult.preferenceUpdated("Rule '" + ruleId + "' " + (muted ? "muted" : "unmuted") + ".", ruleId)
    );
  }
}
