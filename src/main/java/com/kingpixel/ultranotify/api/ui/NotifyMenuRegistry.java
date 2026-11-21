package com.kingpixel.ultranotify.api.ui;

import net.minecraft.server.network.ServerPlayerEntity;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Thread-safe registry for CobbleNotify UI menu providers.
 * Allows companion mods (e.g. Rotom Phone, custom GUIs) to register custom menu providers.
 * If no custom provider is registered, it automatically falls back to {@link DefaultGooeyNotifyMenuProvider}.
 */
public final class NotifyMenuRegistry {

  private static final NotifyMenuProvider DEFAULT_PROVIDER = new DefaultGooeyNotifyMenuProvider();
  private static final AtomicReference<NotifyMenuProvider> CUSTOM_PROVIDER = new AtomicReference<>(null);

  private NotifyMenuRegistry() {}

  /**
   * Registers a custom menu provider. Passing null unregisters any custom provider.
   *
   * @param provider the custom menu provider, or null to revert to default
   */
  public static void register(NotifyMenuProvider provider) {
    CUSTOM_PROVIDER.set(provider);
  }

  /**
   * Unregisters the custom menu provider, reverting back to the default GooeyLibs implementation.
   */
  public static void unregister() {
    CUSTOM_PROVIDER.set(null);
  }

  /**
   * Checks whether a custom menu provider is currently registered.
   *
   * @return true if a custom provider is active
   */
  public static boolean hasCustomProvider() {
    return CUSTOM_PROVIDER.get() != null;
  }

  /**
   * Returns the active menu provider (custom if present, otherwise default).
   *
   * @return the active menu provider
   */
  public static NotifyMenuProvider getProvider() {
    NotifyMenuProvider custom = CUSTOM_PROVIDER.get();
    return custom != null ? custom : DEFAULT_PROVIDER;
  }

  /**
   * Opens the main notification hub menu using the active provider.
   *
   * @param player the player
   */
  public static void openMainMenu(ServerPlayerEntity player) {
    if (player == null) return;
    getProvider().openMainMenu(player);
  }

  /**
   * Opens the spawn history menu using the active provider.
   *
   * @param player the player
   * @param page   the page number (1-indexed)
   */
  public static void openSpawnHistoryMenu(ServerPlayerEntity player, int page) {
    if (player == null) return;
    getProvider().openSpawnHistoryMenu(player, Math.max(1, page));
  }

  /**
   * Opens the caught history menu using the active provider.
   *
   * @param player the player
   * @param page   the page number (1-indexed)
   */
  public static void openCaughtHistoryMenu(ServerPlayerEntity player, int page) {
    if (player == null) return;
    getProvider().openCaughtHistoryMenu(player, Math.max(1, page));
  }

  /**
   * Opens the trade history menu using the active provider.
   *
   * @param player the player
   * @param page   the page number (1-indexed)
   */
  public static void openTradeHistoryMenu(ServerPlayerEntity player, int page) {
    if (player == null) return;
    getProvider().openTradeHistoryMenu(player, Math.max(1, page));
  }

  /**
   * Opens the player notification preferences menu using the active provider.
   *
   * @param player the player
   */
  public static void openPreferencesMenu(ServerPlayerEntity player) {
    if (player == null) return;
    getProvider().openPreferencesMenu(player);
  }
}
