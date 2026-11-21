package com.kingpixel.ultranotify;

import com.kingpixel.ultranotify.api.CobbleNotifyAPI;
import com.kingpixel.ultranotify.api.UltraNotifyAPI;
import com.kingpixel.ultranotify.api.action.NotifyActionResult;
import com.kingpixel.ultranotify.api.model.NotificationRuleView;
import com.kingpixel.ultranotify.api.model.PlayerNotifyContext;
import com.kingpixel.ultranotify.api.model.PlayerNotifyPreferencesView;
import com.kingpixel.ultranotify.api.model.SpawnHistoryView;
import com.kingpixel.ultranotify.api.model.TradeHistoryView;
import com.kingpixel.ultranotify.api.ui.DefaultGooeyNotifyMenuProvider;
import com.kingpixel.ultranotify.api.ui.NotifyMenuProvider;
import com.kingpixel.ultranotify.api.ui.NotifyMenuRegistry;
import com.kingpixel.ultranotify.application.NotifyActionService;
import com.kingpixel.ultranotify.application.NotifyContextService;
import com.kingpixel.ultranotify.models.enums.Actions;
import net.minecraft.server.network.ServerPlayerEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TestCobbleNotifyApi {

  @BeforeEach
  @AfterEach
  void resetRegistry() {
    NotifyMenuRegistry.unregister();
  }

  @Test
  void testDefaultMenuProvider() {
    assertFalse(NotifyMenuRegistry.hasCustomProvider());
    assertNotNull(NotifyMenuRegistry.getProvider());
    assertTrue(NotifyMenuRegistry.getProvider() instanceof DefaultGooeyNotifyMenuProvider);
  }

  @Test
  void testCustomMenuProviderRegistrationAndOverride() {
    AtomicBoolean mainOpened = new AtomicBoolean(false);
    AtomicInteger spawnPage = new AtomicInteger(0);
    AtomicInteger caughtPage = new AtomicInteger(0);
    AtomicInteger tradePage = new AtomicInteger(0);
    AtomicBoolean prefsOpened = new AtomicBoolean(false);

    NotifyMenuProvider custom = new NotifyMenuProvider() {
      @Override
      public void openMainMenu(ServerPlayerEntity player) {
        mainOpened.set(true);
      }

      @Override
      public void openSpawnHistoryMenu(ServerPlayerEntity player, int page) {
        spawnPage.set(page);
      }

      @Override
      public void openCaughtHistoryMenu(ServerPlayerEntity player, int page) {
        caughtPage.set(page);
      }

      @Override
      public void openTradeHistoryMenu(ServerPlayerEntity player, int page) {
        tradePage.set(page);
      }

      @Override
      public void openPreferencesMenu(ServerPlayerEntity player) {
        prefsOpened.set(true);
      }
    };

    CobbleNotifyAPI.registerMenuProvider(custom);
    assertTrue(CobbleNotifyAPI.hasCustomMenuProvider());
    assertSame(custom, CobbleNotifyAPI.getMenuProvider());

    // Null player guard should not invoke provider
    CobbleNotifyAPI.openMainMenu(null);
    CobbleNotifyAPI.openSpawnHistoryMenu(null, 2);
    CobbleNotifyAPI.openCaughtHistoryMenu(null, 3);
    CobbleNotifyAPI.openTradeHistoryMenu(null, 4);
    CobbleNotifyAPI.openPreferencesMenu(null);
    assertFalse(mainOpened.get());
    assertEquals(0, spawnPage.get());
    assertEquals(0, caughtPage.get());
    assertEquals(0, tradePage.get());
    assertFalse(prefsOpened.get());

    // Direct provider invocation confirms registered custom provider receives calls
    CobbleNotifyAPI.getMenuProvider().openMainMenu(null);
    assertTrue(mainOpened.get());

    CobbleNotifyAPI.getMenuProvider().openSpawnHistoryMenu(null, 2);
    assertEquals(2, spawnPage.get());

    CobbleNotifyAPI.getMenuProvider().openCaughtHistoryMenu(null, 3);
    assertEquals(3, caughtPage.get());

    CobbleNotifyAPI.getMenuProvider().openTradeHistoryMenu(null, 4);
    assertEquals(4, tradePage.get());

    CobbleNotifyAPI.getMenuProvider().openPreferencesMenu(null);
    assertTrue(prefsOpened.get());

    CobbleNotifyAPI.unregisterMenuProvider();
    assertFalse(CobbleNotifyAPI.hasCustomMenuProvider());
    assertTrue(CobbleNotifyAPI.getMenuProvider() instanceof DefaultGooeyNotifyMenuProvider);

    // Verify UltraNotifyAPI facade alias
    UltraNotifyAPI.registerMenuProvider(custom);
    assertTrue(UltraNotifyAPI.hasCustomMenuProvider());
    assertSame(custom, UltraNotifyAPI.getMenuProvider());
    UltraNotifyAPI.unregisterMenuProvider();
    assertFalse(UltraNotifyAPI.hasCustomMenuProvider());
  }

  @Test
  void testNotifyActionResultStatuses() {
    NotifyActionResult success = NotifyActionResult.success("Dispatched!", "legendary");
    assertTrue(success.isSuccess());
    assertEquals(NotifyActionResult.Status.SUCCESS, success.getStatus());
    assertEquals("Dispatched!", success.getMessage());
    assertEquals("legendary", success.getRuleId());

    NotifyActionResult pref = NotifyActionResult.preferenceUpdated("Toggled", "shiny");
    assertTrue(pref.isSuccess());
    assertEquals(NotifyActionResult.Status.PREFERENCE_UPDATED, pref.getStatus());
    assertEquals("shiny", pref.getRuleId());

    NotifyActionResult notFound = NotifyActionResult.ruleNotFound("mythical");
    assertFalse(notFound.isSuccess());
    assertEquals(NotifyActionResult.Status.RULE_NOT_FOUND, notFound.getStatus());
    assertEquals("mythical", notFound.getRuleId());

    NotifyActionResult offline = NotifyActionResult.offline("Player is offline");
    assertFalse(offline.isSuccess());
    assertEquals(NotifyActionResult.Status.PLAYER_OFFLINE, offline.getStatus());

    NotifyActionResult noPerm = NotifyActionResult.noPermission("No permission");
    assertFalse(noPerm.isSuccess());
    assertEquals(NotifyActionResult.Status.NO_PERMISSION, noPerm.getStatus());

    NotifyActionResult invalid = NotifyActionResult.invalidArgument("Invalid arg");
    assertFalse(invalid.isSuccess());
    assertEquals(NotifyActionResult.Status.INVALID_ARGUMENT, invalid.getStatus());
  }

  @Test
  void testImmutableViews() {
    NotificationRuleView ruleView = new NotificationRuleView(
        "legendary",
        10,
        false,
        "shiny=true",
        true,
        true,
        true,
        false,
        true,
        true,
        true,
        false,
        false,
        Set.of("*"),
        Set.of("minecraft:the_end")
    );

    assertEquals("legendary", ruleView.getIdentifier());
    assertEquals(10, ruleView.getPriority());
    assertFalse(ruleView.isUseProperties());
    assertTrue(ruleView.isSpawnEnabled());
    assertTrue(ruleView.isCaughtEnabled());
    assertTrue(ruleView.isDefeatEnabled());
    assertFalse(ruleView.isTradeEnabled());
    assertTrue(ruleView.isWebhookEnabled());
    assertThrows(UnsupportedOperationException.class, () -> ruleView.getWhitelistWorlds().add("test"));
    assertThrows(UnsupportedOperationException.class, () -> ruleView.getBlacklistWorlds().add("test"));

    UUID playerUuid = UUID.randomUUID();
    PlayerNotifyPreferencesView prefsView = new PlayerNotifyPreferencesView(
        playerUuid,
        true,
        true,
        true,
        false,
        true,
        Set.of("boss")
    );

    assertEquals(playerUuid, prefsView.getPlayerUuid());
    assertTrue(prefsView.isNotificationsEnabled());
    assertTrue(prefsView.isSpawnAlertsEnabled());
    assertFalse(prefsView.isDefeatAlertsEnabled());
    assertTrue(prefsView.isRuleMuted("BOSS"));
    assertFalse(prefsView.isRuleMuted("legendary"));
    assertThrows(UnsupportedOperationException.class, () -> prefsView.getMutedRuleIds().add("shiny"));

    PlayerNotifyContext context = new PlayerNotifyContext(
        playerUuid,
        "Ash",
        "minecraft:overworld",
        false,
        prefsView,
        List.of(ruleView)
    );

    assertEquals(playerUuid, context.getPlayerUuid());
    assertEquals("Ash", context.getPlayerName());
    assertEquals("minecraft:overworld", context.getCurrentWorld());
    assertFalse(context.isWorldBlacklisted());
    assertSame(prefsView, context.getPreferences());
    assertEquals(1, context.getActiveRules().size());
    assertThrows(UnsupportedOperationException.class, () -> context.getActiveRules().clear());

    Instant now = Instant.now();
    UUID spawnId = UUID.randomUUID();
    SpawnHistoryView spawnView = new SpawnHistoryView(
        spawnId,
        "legendary",
        playerUuid,
        "rayquaza",
        now,
        "minecraft:overworld",
        "X: 100 Y: 70 Z: -200",
        List.of("Ash", "Misty"),
        true,
        "Ash",
        now
    );

    assertEquals(spawnId, spawnView.getIdentifier());
    assertEquals("legendary", spawnView.getNotificationId());
    assertEquals("rayquaza", spawnView.getPokemonName());
    assertTrue(spawnView.isCaught());
    assertEquals("Ash", spawnView.getCaughtBy());
    assertEquals(2, spawnView.getNearbyPlayers().size());
    assertThrows(UnsupportedOperationException.class, () -> spawnView.getNearbyPlayers().add("Brock"));

    UUID tradeId = UUID.randomUUID();
    UUID trader2 = UUID.randomUUID();
    TradeHistoryView tradeView = new TradeHistoryView(
        tradeId,
        "shiny",
        playerUuid,
        "Ash",
        "pikachu",
        trader2,
        "Gary",
        "eevee",
        now
    );

    assertEquals(tradeId, tradeView.getIdentifier());
    assertEquals("shiny", tradeView.getNotificationId());
    assertEquals("Ash", tradeView.getTrader1Name());
    assertEquals("pikachu", tradeView.getPokemon1Name());
    assertEquals("Gary", tradeView.getTrader2Name());
    assertEquals("eevee", tradeView.getPokemon2Name());
  }

  @Test
  void testPlayerPreferencesActionsAndContext() {
    UUID playerUuid = UUID.randomUUID();

    PlayerNotifyPreferencesView initial = CobbleNotifyAPI.getPlayerPreferences(playerUuid).join();
    assertNotNull(initial);
    assertTrue(initial.isNotificationsEnabled());
    assertTrue(initial.isSpawnAlertsEnabled());

    NotifyActionResult toggleGlobal = CobbleNotifyAPI.togglePlayerNotifications(playerUuid).join();
    assertTrue(toggleGlobal.isSuccess());
    assertEquals(NotifyActionResult.Status.PREFERENCE_UPDATED, toggleGlobal.getStatus());

    PlayerNotifyPreferencesView afterGlobal = CobbleNotifyAPI.getPlayerPreferences(playerUuid).join();
    assertFalse(afterGlobal.isNotificationsEnabled());

    NotifyActionResult toggleSpawn = CobbleNotifyAPI.toggleActionPreference(playerUuid, Actions.SPAWN).join();
    assertTrue(toggleSpawn.isSuccess());
    assertFalse(CobbleNotifyAPI.getPlayerPreferences(playerUuid).join().isSpawnAlertsEnabled());

    NotifyActionResult toggleMute = CobbleNotifyAPI.toggleRuleMute(playerUuid, "Legendary").join();
    assertTrue(toggleMute.isSuccess());
    assertTrue(CobbleNotifyAPI.getPlayerPreferences(playerUuid).join().isRuleMuted("legendary"));

    NotifyActionResult unmute = CobbleNotifyAPI.toggleRuleMute(playerUuid, "Legendary").join();
    assertTrue(unmute.isSuccess());
    assertFalse(CobbleNotifyAPI.getPlayerPreferences(playerUuid).join().isRuleMuted("legendary"));
  }

  @Test
  void testOfflineAndNullGuardsInServices() {
    NotifyActionService actionService = NotifyActionService.getInstance();
    NotifyContextService contextService = NotifyContextService.getInstance();

    NotifyActionResult r1 = actionService.triggerCaughtNotification(null, null).join();
    assertFalse(r1.isSuccess());
    assertEquals(NotifyActionResult.Status.PLAYER_OFFLINE, r1.getStatus());

    NotifyActionResult r2 = actionService.triggerDefeatNotification(null, null, null).join();
    assertFalse(r2.isSuccess());
    assertEquals(NotifyActionResult.Status.PLAYER_OFFLINE, r2.getStatus());

    NotifyActionResult r3 = actionService.triggerSpawnNotification(null).join();
    assertFalse(r3.isSuccess());
    assertEquals(NotifyActionResult.Status.INVALID_ARGUMENT, r3.getStatus());

    NotifyActionResult r4 = actionService.triggerTradeNotification(null, null, null, null).join();
    assertFalse(r4.isSuccess());
    assertEquals(NotifyActionResult.Status.PLAYER_OFFLINE, r4.getStatus());

    NotifyActionResult r5 = actionService.togglePlayerNotifications((ServerPlayerEntity) null).join();
    assertFalse(r5.isSuccess());
    assertEquals(NotifyActionResult.Status.PLAYER_OFFLINE, r5.getStatus());

    NotifyActionResult r6 = actionService.toggleActionPreference((UUID) null, Actions.CAUGHT).join();
    assertFalse(r6.isSuccess());
    assertEquals(NotifyActionResult.Status.PLAYER_OFFLINE, r6.getStatus());

    NotifyActionResult r7 = actionService.toggleRuleMute(UUID.randomUUID(), " ").join();
    assertFalse(r7.isSuccess());
    assertEquals(NotifyActionResult.Status.INVALID_ARGUMENT, r7.getStatus());

    assertNull(contextService.getPlayerContext(null).join());
    assertNull(contextService.getPlayerPreferences(null).join());
    assertFalse(contextService.getRule(null).join().isPresent());
    assertFalse(contextService.getRule("").join().isPresent());
    assertTrue(contextService.getSpawnHistory(0).join().isEmpty());
    assertTrue(contextService.getCaughtHistory(0).join().isEmpty());
    assertTrue(contextService.getTradeHistory(0).join().isEmpty());
  }
}
