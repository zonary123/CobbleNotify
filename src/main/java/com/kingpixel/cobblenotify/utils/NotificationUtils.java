package com.kingpixel.cobblenotify.utils;

import me.drex.vanish.api.VanishAPI;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:51
 */
public class NotificationUtils {

  public static boolean isModLoaded(String identifier) {
    return FabricLoader.getInstance().isModLoaded(identifier);
  }

  public static boolean playerIsVanish(ServerPlayerEntity player) {
    try {
      return VanishAPI.isVanished(player);
    } catch (Exception ignored) {
      return false;
    }
  }
}
