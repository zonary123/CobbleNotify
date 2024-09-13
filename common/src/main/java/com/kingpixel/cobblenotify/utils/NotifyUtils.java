package com.kingpixel.cobblenotify.utils;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author Carlos Varas Alonso - 18/07/2024 4:07
 */
public class NotifyUtils {
  public static void adventure(ServerPlayer player, String message) {
    player.sendSystemMessage(AdventureTranslator.toNativeWithOutPrefix(message
      .replace("%prefix%", CobbleNotify.language.getPrefix())));
  }

  public static void broadcast(String message) {
    CobbleNotify.server.getPlayerList().getPlayers().forEach(player -> adventure(player, message));
  }
}
