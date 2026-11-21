package com.kingpixel.ultranotify.application;

import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.api.model.NotificationRuleView;
import com.kingpixel.ultranotify.api.model.PlayerNotifyContext;
import com.kingpixel.ultranotify.api.model.PlayerNotifyPreferencesView;
import com.kingpixel.ultranotify.api.model.SpawnHistoryView;
import com.kingpixel.ultranotify.api.model.TradeHistoryView;
import com.kingpixel.ultranotify.config.Notifications;
import com.kingpixel.ultranotify.models.Notification;
import com.kingpixel.ultranotify.models.WorldFilter;
import com.kingpixel.ultranotify.models.history.HistorySpawn;
import com.kingpixel.ultranotify.models.history.HistoryTrade;
import com.kingpixel.ultranotify.models.notification.NotificationOptions;
import com.kingpixel.ultranotify.models.webhook.WebHookOptions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Application service providing read-only query operations and immutable viewmodels for CobbleNotify.
 */
public class NotifyContextService {

  private static final NotifyContextService INSTANCE = new NotifyContextService();

  public static NotifyContextService getInstance() {
    return INSTANCE;
  }

  /**
   * Retrieves the full notification context snapshot for an online player.
   *
   * @param player the player
   * @return future with the player context, or null if player is null
   */
  public CompletableFuture<PlayerNotifyContext> getPlayerContext(ServerPlayerEntity player) {
    if (player == null) {
      return CompletableFuture.completedFuture(null);
    }

    return CompletableFuture.supplyAsync(() -> {
      String worldId = player.getServerWorld() != null && player.getServerWorld().getRegistryKey() != null
          ? player.getServerWorld().getRegistryKey().getValue().toString()
          : "";
      boolean worldBlacklisted = UltraNotify.config != null
          && UltraNotify.config.getWorldFilter() != null
          && !worldId.isEmpty()
          && UltraNotify.config.getWorldFilter().isBlackListed(worldId);

      PlayerNotifyPreferencesView prefs = NotifyActionService.getInstance().getPreferencesView(player.getUuid());
      List<NotificationRuleView> rules = toRuleViews(Notifications.NOTIFICATIONS);

      return new PlayerNotifyContext(
          player.getUuid(),
          player.getGameProfile() != null ? player.getGameProfile().getName() : "",
          worldId,
          worldBlacklisted,
          prefs,
          rules
      );
    });
  }

  /**
   * Retrieves a player's notification preferences by UUID.
   *
   * @param playerUuid the player UUID
   * @return future with the player preferences view, or null if UUID is null
   */
  public CompletableFuture<PlayerNotifyPreferencesView> getPlayerPreferences(UUID playerUuid) {
    if (playerUuid == null) {
      return CompletableFuture.completedFuture(null);
    }
    return CompletableFuture.completedFuture(NotifyActionService.getInstance().getPreferencesView(playerUuid));
  }

  /**
   * Retrieves a specific notification rule view by identifier.
   *
   * @param ruleId the rule identifier
   * @return future with the optional rule view
   */
  public CompletableFuture<Optional<NotificationRuleView>> getRule(String ruleId) {
    if (ruleId == null || ruleId.isBlank()) {
      return CompletableFuture.completedFuture(Optional.empty());
    }
    for (Notification notification : Notifications.NOTIFICATIONS) {
      if (notification != null && notification.getIdentifier() != null
          && notification.getIdentifier().equalsIgnoreCase(ruleId)) {
        return CompletableFuture.completedFuture(Optional.of(toRuleView(notification)));
      }
    }
    return CompletableFuture.completedFuture(Optional.empty());
  }

  /**
   * Retrieves all configured notification rules as immutable views.
   *
   * @return future with the list of rule views
   */
  public CompletableFuture<List<NotificationRuleView>> getAllRules() {
    return CompletableFuture.completedFuture(toRuleViews(Notifications.NOTIFICATIONS));
  }

  /**
   * Retrieves paginated spawn history records as immutable views.
   *
   * @param page the 1-indexed page number
   * @return future with the list of spawn history views
   */
  public CompletableFuture<List<SpawnHistoryView>> getSpawnHistory(int page) {
    if (page < 1 || UltraNotify.databaseClient == null) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }
    return CompletableFuture.supplyAsync(() -> {
      List<HistorySpawn> spawns = UltraNotify.databaseClient.getSpawnedPokemons(page);
      if (spawns == null) return Collections.<SpawnHistoryView>emptyList();
      return spawns.stream().map(this::toSpawnHistoryView).toList();
    }).exceptionally(ex -> Collections.<SpawnHistoryView>emptyList());
  }

  /**
   * Retrieves paginated caught history records as immutable views.
   *
   * @param page the 1-indexed page number
   * @return future with the list of caught history views
   */
  public CompletableFuture<List<SpawnHistoryView>> getCaughtHistory(int page) {
    if (page < 1 || UltraNotify.databaseClient == null) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }
    return CompletableFuture.supplyAsync(() -> {
      List<HistorySpawn> caught = UltraNotify.databaseClient.getCaughtPokemons(page);
      if (caught == null) return Collections.<SpawnHistoryView>emptyList();
      return caught.stream().map(this::toSpawnHistoryView).toList();
    }).exceptionally(ex -> Collections.<SpawnHistoryView>emptyList());
  }

  /**
   * Retrieves paginated trade history records as immutable views.
   *
   * @param page the 1-indexed page number
   * @return future with the list of trade history views
   */
  public CompletableFuture<List<TradeHistoryView>> getTradeHistory(int page) {
    if (page < 1 || UltraNotify.databaseClient == null) {
      return CompletableFuture.completedFuture(Collections.emptyList());
    }
    return CompletableFuture.supplyAsync(() -> {
      List<HistoryTrade> trades = UltraNotify.databaseClient.getTradesHistory(page);
      if (trades == null) return Collections.<TradeHistoryView>emptyList();
      return trades.stream().map(this::toTradeHistoryView).toList();
    }).exceptionally(ex -> Collections.<TradeHistoryView>emptyList());
  }

  /**
   * Transforms a {@link Notification} model into an immutable {@link NotificationRuleView}.
   *
   * @param notification the notification rule model
   * @return the immutable rule view
   */
  public NotificationRuleView toRuleView(Notification notification) {
    if (notification == null) return null;
    NotificationOptions nOpts = notification.getNotificationOptions();
    WebHookOptions wOpts = notification.getWebHookOptions();
    WorldFilter wf = notification.getWorldFilter();

    return new NotificationRuleView(
        notification.getIdentifier(),
        notification.getPriority(),
        notification.isUseProperties(),
        notification.getProperties(),
        nOpts != null && nOpts.isSpawn(),
        nOpts != null && nOpts.isCaught(),
        nOpts != null && nOpts.isDefeat(),
        nOpts != null && nOpts.isTrade(),
        wOpts != null && wOpts.isEnabled(),
        wOpts != null && wOpts.isSpawn(),
        wOpts != null && wOpts.isCaught(),
        wOpts != null && wOpts.isDefeat(),
        wOpts != null && wOpts.isTrade(),
        wf != null && wf.getWhitelistWorlds() != null ? wf.getWhitelistWorlds() : Set.of(),
        wf != null && wf.getBlacklistWorlds() != null ? wf.getBlacklistWorlds() : Set.of()
    );
  }

  /**
   * Transforms a {@link HistorySpawn} model into an immutable {@link SpawnHistoryView}.
   *
   * @param spawn the history spawn model
   * @return the immutable spawn history view
   */
  public SpawnHistoryView toSpawnHistoryView(HistorySpawn spawn) {
    if (spawn == null) return null;
    String pokemonName = spawn.getPokemon() != null ? spawn.getPokemon().showdownId() : "";
    return new SpawnHistoryView(
        spawn.getIdentifier(),
        spawn.getNotificationId(),
        spawn.getPlayerId(),
        pokemonName,
        spawn.getSpawnTime(),
        spawn.getWorld(),
        spawn.getLocation(),
        spawn.getNearbyPlayers(),
        spawn.isCaught(),
        spawn.getCaughtBy(),
        spawn.getCaughtDate()
    );
  }

  /**
   * Transforms a {@link HistoryTrade} model into an immutable {@link TradeHistoryView}.
   *
   * @param trade the history trade model
   * @return the immutable trade history view
   */
  public TradeHistoryView toTradeHistoryView(HistoryTrade trade) {
    if (trade == null) return null;
    String p1Name = trade.getPokemon1() != null ? trade.getPokemon1().showdownId() : "";
    String p2Name = trade.getPokemon2() != null ? trade.getPokemon2().showdownId() : "";
    return new TradeHistoryView(
        trade.getIdentifier(),
        trade.getNotificationId(),
        trade.getTrader1Id(),
        trade.getTrader1(),
        p1Name,
        trade.getTrader2Id(),
        trade.getTrader2(),
        p2Name,
        trade.getTradeTime()
    );
  }

  private List<NotificationRuleView> toRuleViews(List<Notification> list) {
    if (list == null || list.isEmpty()) return Collections.emptyList();
    List<NotificationRuleView> result = new ArrayList<>(list.size());
    for (Notification n : list) {
      if (n != null) {
        result.add(toRuleView(n));
      }
    }
    return Collections.unmodifiableList(result);
  }
}
