package com.kingpixel.ultranotify.api;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.ultranotify.api.action.NotifyActionResult;
import com.kingpixel.ultranotify.api.model.NotificationRuleView;
import com.kingpixel.ultranotify.api.model.PlayerNotifyContext;
import com.kingpixel.ultranotify.api.model.PlayerNotifyPreferencesView;
import com.kingpixel.ultranotify.api.model.SpawnHistoryView;
import com.kingpixel.ultranotify.api.model.TradeHistoryView;
import com.kingpixel.ultranotify.api.ui.NotifyMenuProvider;
import com.kingpixel.ultranotify.api.ui.NotifyMenuRegistry;
import com.kingpixel.ultranotify.application.NotifyActionService;
import com.kingpixel.ultranotify.application.NotifyContextService;
import com.kingpixel.ultranotify.models.enums.Actions;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Unified public API facade for CobbleNotify (UltraNotify).
 */
public class CobbleNotifyAPI {

  // --- UI Menu Provider & Registry API ---

  public static void openMainMenu(ServerPlayerEntity player) {
    NotifyMenuRegistry.openMainMenu(player);
  }

  public static void openSpawnHistoryMenu(ServerPlayerEntity player, int page) {
    NotifyMenuRegistry.openSpawnHistoryMenu(player, page);
  }

  public static void openCaughtHistoryMenu(ServerPlayerEntity player, int page) {
    NotifyMenuRegistry.openCaughtHistoryMenu(player, page);
  }

  public static void openTradeHistoryMenu(ServerPlayerEntity player, int page) {
    NotifyMenuRegistry.openTradeHistoryMenu(player, page);
  }

  public static void openPreferencesMenu(ServerPlayerEntity player) {
    NotifyMenuRegistry.openPreferencesMenu(player);
  }

  public static void registerMenuProvider(NotifyMenuProvider provider) {
    NotifyMenuRegistry.register(provider);
  }

  public static void unregisterMenuProvider() {
    NotifyMenuRegistry.unregister();
  }

  public static boolean hasCustomMenuProvider() {
    return NotifyMenuRegistry.hasCustomProvider();
  }

  public static NotifyMenuProvider getMenuProvider() {
    return NotifyMenuRegistry.getProvider();
  }

  // --- Headless Action API ---

  public static CompletableFuture<NotifyActionResult> reloadConfigs() {
    return NotifyActionService.getInstance().reloadConfigs();
  }

  public static CompletableFuture<NotifyActionResult> triggerCaughtNotification(ServerPlayerEntity player, Pokemon pokemon) {
    return NotifyActionService.getInstance().triggerCaughtNotification(player, pokemon);
  }

  public static CompletableFuture<NotifyActionResult> triggerDefeatNotification(ServerPlayerEntity player,
                                                                                Pokemon pokemon,
                                                                                PokemonEntity entity) {
    return NotifyActionService.getInstance().triggerDefeatNotification(player, pokemon, entity);
  }

  public static CompletableFuture<NotifyActionResult> triggerSpawnNotification(PokemonEntity pokemonEntity) {
    return NotifyActionService.getInstance().triggerSpawnNotification(pokemonEntity);
  }

  public static CompletableFuture<NotifyActionResult> triggerTradeNotification(ServerPlayerEntity player1,
                                                                               Pokemon pokemon1,
                                                                               ServerPlayerEntity player2,
                                                                               Pokemon pokemon2) {
    return NotifyActionService.getInstance().triggerTradeNotification(player1, pokemon1, player2, pokemon2);
  }

  public static CompletableFuture<NotifyActionResult> togglePlayerNotifications(ServerPlayerEntity player) {
    return NotifyActionService.getInstance().togglePlayerNotifications(player);
  }

  public static CompletableFuture<NotifyActionResult> togglePlayerNotifications(UUID playerUuid) {
    return NotifyActionService.getInstance().togglePlayerNotifications(playerUuid);
  }

  public static CompletableFuture<NotifyActionResult> toggleActionPreference(ServerPlayerEntity player, Actions action) {
    return NotifyActionService.getInstance().toggleActionPreference(player, action);
  }

  public static CompletableFuture<NotifyActionResult> toggleActionPreference(UUID playerUuid, Actions action) {
    return NotifyActionService.getInstance().toggleActionPreference(playerUuid, action);
  }

  public static CompletableFuture<NotifyActionResult> toggleRuleMute(UUID playerUuid, String ruleId) {
    return NotifyActionService.getInstance().toggleRuleMute(playerUuid, ruleId);
  }

  // --- Context & Query API ---

  public static CompletableFuture<PlayerNotifyContext> getPlayerContext(ServerPlayerEntity player) {
    return NotifyContextService.getInstance().getPlayerContext(player);
  }

  public static CompletableFuture<PlayerNotifyPreferencesView> getPlayerPreferences(UUID playerUuid) {
    return NotifyContextService.getInstance().getPlayerPreferences(playerUuid);
  }

  public static CompletableFuture<Optional<NotificationRuleView>> getRule(String ruleId) {
    return NotifyContextService.getInstance().getRule(ruleId);
  }

  public static CompletableFuture<List<NotificationRuleView>> getAllRules() {
    return NotifyContextService.getInstance().getAllRules();
  }

  public static CompletableFuture<List<SpawnHistoryView>> getSpawnHistory(int page) {
    return NotifyContextService.getInstance().getSpawnHistory(page);
  }

  public static CompletableFuture<List<SpawnHistoryView>> getCaughtHistory(int page) {
    return NotifyContextService.getInstance().getCaughtHistory(page);
  }

  public static CompletableFuture<List<TradeHistoryView>> getTradeHistory(int page) {
    return NotifyContextService.getInstance().getTradeHistory(page);
  }
}
