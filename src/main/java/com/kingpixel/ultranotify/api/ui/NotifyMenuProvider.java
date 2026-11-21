package com.kingpixel.ultranotify.api.ui;

import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Service Provider Interface (SPI) for CobbleNotify (UltraNotify) user interfaces.
 * Allows companion mods (e.g. Rotom Phone, custom graphical screens, web dashboards)
 * to intercept and replace default chest GUI menus with custom screens.
 */
public interface NotifyMenuProvider {

  /**
   * Opens the main notification history hub menu for a player.
   *
   * @param player the player
   */
  void openMainMenu(ServerPlayerEntity player);

  /**
   * Opens the spawn history menu for a player.
   *
   * @param player the player
   * @param page   the page number (1-indexed)
   */
  void openSpawnHistoryMenu(ServerPlayerEntity player, int page);

  /**
   * Opens the caught history menu for a player.
   *
   * @param player the player
   * @param page   the page number (1-indexed)
   */
  void openCaughtHistoryMenu(ServerPlayerEntity player, int page);

  /**
   * Opens the trade history menu for a player.
   *
   * @param player the player
   * @param page   the page number (1-indexed)
   */
  void openTradeHistoryMenu(ServerPlayerEntity player, int page);

  /**
   * Opens the player notification preferences menu.
   * Defaults to opening the main notification menu unless overridden by a custom UI mod.
   *
   * @param player the player
   */
  default void openPreferencesMenu(ServerPlayerEntity player) {
    openMainMenu(player);
  }
}
