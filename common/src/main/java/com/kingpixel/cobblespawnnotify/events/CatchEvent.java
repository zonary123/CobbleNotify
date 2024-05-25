package com.kingpixel.cobblespawnnotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblespawnnotify.SpawnNotify;
import com.kingpixel.cobblespawnnotify.utils.Utils;
import kotlin.Unit;
import net.minecraft.world.entity.player.Player;

/**
 * @author Carlos Varas Alonso - 25/05/2024 21:48
 */
public class CatchEvent {
  public static void registerEvents() {
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOW, (evt) -> {
      if (!SpawnNotify.config.isNotifycatch()) return Unit.INSTANCE;
      try {
        Player player = evt.getPlayer();
        Pokemon pokemon = evt.getPokemon();
        String s = "";

        if (pokemon.isLegendary()) {
          s = SpawnNotify.language.getMessagecatchlegendary();
        } else if (pokemon.getShiny()) {
          s = SpawnNotify.language.getMessagecatchshiny();
        } else {
          return Unit.INSTANCE;
        }

        s = s.replace("%player%", player.getName().getString())
          .replace("%pokemon%", pokemon.getSpecies().getName())
          .replace("%form%", SpawnNotify.language.getForms().getOrDefault(pokemon.getForm().getName(), pokemon.getForm().getName()));
        Utils.broadcastMessage(s);
      } catch (Exception e) {
        System.err.println("Se produjo un error al procesar el evento de captura de Pokemon: " + e.getMessage());
        e.printStackTrace();
      }

      return Unit.INSTANCE;
    });
  }
}

