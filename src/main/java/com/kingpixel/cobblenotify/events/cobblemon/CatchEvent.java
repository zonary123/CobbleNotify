package com.kingpixel.cobblenotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.models.Notification;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:39
 */
public class CatchEvent {
  public static void register() {
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOWEST, evt -> {
      try {
        Pokemon pokemon = evt.getPokemon();
        ServerPlayerEntity player = evt.getPlayer();
        if (CobbleNotify.config.getWorldFilter().isBlackListed(player.getServerWorld())) return;
        for (Notification notification : Notifications.NOTIFICATIONS) {
          if (notification.getWorldFilter().isBlackListed(player.getServerWorld())) continue;
          if (notification.computeCaught(pokemon, player)) return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
