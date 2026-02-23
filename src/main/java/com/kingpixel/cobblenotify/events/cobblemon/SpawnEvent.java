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
      try {
        if (!CobbleNotify.config.isAffectCommand()) return EventResult.pass();
        if (CobbleNotify.config.getWorldFilter().isBlackListed(world)) return EventResult.pass();
        if (entity instanceof PokemonEntity pokemonEntity) {
          for (Notification notification : Notifications.NOTIFICATIONS) {
            if (notification.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) continue;
            if (notification.computeSpawn(pokemonEntity)) return EventResult.pass();
          }
        }
        return EventResult.pass();
      } catch (Exception e) {
        e.printStackTrace();
        return EventResult.pass();
      }
    });

    CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, evt -> {
      try {
        if (CobbleNotify.config.isAffectCommand()) return;
        PokemonEntity pokemonEntity = evt.getEntity();
        if (CobbleNotify.config.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) return;
        for (Notification notification : Notifications.NOTIFICATIONS) {
          if (notification.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) continue;
          if (notification.computeSpawn(pokemonEntity)) return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
