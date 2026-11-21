package com.kingpixel.ultranotify.api.ui;

import com.kingpixel.ultranotify.UltraNotify;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * Default GooeyLibs-based implementation of {@link NotifyMenuProvider}.
 */
public class DefaultGooeyNotifyMenuProvider implements NotifyMenuProvider {

  @Override
  public void openMainMenu(ServerPlayerEntity player) {
    if (player == null || UltraNotify.lang == null || UltraNotify.lang.getGlobalGui() == null) return;
    UltraNotify.lang.getGlobalGui().open(player);
  }

  @Override
  public void openSpawnHistoryMenu(ServerPlayerEntity player, int page) {
    if (player == null || UltraNotify.lang == null || UltraNotify.lang.getSpawnGui() == null) return;
    UltraNotify.lang.getSpawnGui().open(player, Math.max(1, page));
  }

  @Override
  public void openCaughtHistoryMenu(ServerPlayerEntity player, int page) {
    if (player == null || UltraNotify.lang == null || UltraNotify.lang.getCaughtGui() == null) return;
    UltraNotify.lang.getCaughtGui().open(player, Math.max(1, page));
  }

  @Override
  public void openTradeHistoryMenu(ServerPlayerEntity player, int page) {
    if (player == null || UltraNotify.lang == null || UltraNotify.lang.getTradeGui() == null) return;
    UltraNotify.lang.getTradeGui().open(player, Math.max(1, page));
  }

  @Override
  public void openPreferencesMenu(ServerPlayerEntity player) {
    openMainMenu(player);
  }
}
