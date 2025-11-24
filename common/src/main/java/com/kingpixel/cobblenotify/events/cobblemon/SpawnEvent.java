package com.kingpixel.cobblenotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.models.Notification;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:39
 */
public class SpawnEvent {
  public static void register() {
    EntityEvent.ADD.register((entity, world) -> {
      if (!CobbleNotify.config.isAffectCommand()) return EventResult.pass();
      if (entity instanceof PokemonEntity pokemonEntity) {
        for (Notification notification : Notifications.NOTIFICATIONS) {
          if (notification.computeSpawn(pokemonEntity)) return EventResult.pass();
        }
      }
      return EventResult.pass();
    });

    CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, evt -> {
      if (CobbleNotify.config.isAffectCommand()) return;
      PokemonEntity pokemonEntity = evt.getEntity();
      for (Notification notification : Notifications.NOTIFICATIONS) {
        if (notification.computeSpawn(pokemonEntity)) return;
      }
    });
  }
}
