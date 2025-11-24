package com.kingpixel.cobblenotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.models.Notification;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:39
 */
public class CatchEvent {
  public static void register() {
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.NORMAL, evt -> {
      Pokemon pokemon = evt.getPokemon();
      ServerPlayerEntity player = evt.getPlayer();
      for (Notification notification : Notifications.NOTIFICATIONS) {
        if (notification.computeCaught(pokemon, player)) return;
      }
    });
  }
}
