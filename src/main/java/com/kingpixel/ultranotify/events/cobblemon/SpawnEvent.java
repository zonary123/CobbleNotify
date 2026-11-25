package com.kingpixel.ultranotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.config.Notifications;
import com.kingpixel.ultranotify.models.Notification;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:39
 */
public class SpawnEvent {
  public static void register() {
    EntityEvent.ADD.register((entity, world) -> {
      try {
        if (entity == null || world == null) return EventResult.pass();
        if (UltraNotify.config != null) {
          if (!UltraNotify.config.isAffectCommand()) return EventResult.pass();
          if (UltraNotify.config.getWorldFilter().isBlackListed(world)) return EventResult.pass();
        }
        if (entity instanceof PokemonEntity pokemonEntity) {
          if (pokemonEntity.getEntityWorld() == null) return EventResult.pass();
          for (Notification notification : Notifications.NOTIFICATIONS) {
            if (notification.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) continue;
            if (notification.computeSpawn(pokemonEntity)) return EventResult.pass();
          }
        }
        return EventResult.pass();
      } catch (Exception e) {
        UltraNotify.LOGGER.error("Error handling EntityEvent.ADD spawn notification: {}", e.getMessage(), e);
        return EventResult.pass();
      }
    });

    CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOWEST, evt -> {
      try {
        if (UltraNotify.config != null && UltraNotify.config.isAffectCommand()) return;
        PokemonEntity pokemonEntity = evt.getEntity();
        if (pokemonEntity == null || pokemonEntity.getEntityWorld() == null) return;
        if (UltraNotify.config != null && UltraNotify.config.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) return;
        for (Notification notification : Notifications.NOTIFICATIONS) {
          if (notification.getWorldFilter().isBlackListed(pokemonEntity.getEntityWorld())) continue;
          if (notification.computeSpawn(pokemonEntity)) return;
        }
      } catch (Exception e) {
        UltraNotify.LOGGER.error("Error handling Cobblemon POKEMON_ENTITY_SPAWN notification: {}", e.getMessage(), e);
      }
    });
  }
}
