package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.utils.NotifyUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import kotlin.Unit;
import net.minecraft.world.entity.player.Player;

/**
 * @author Carlos Varas Alonso - 25/05/2024 21:48
 */
public class CatchEvent {
  public static void registerEvents() {
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOW, (evt) -> {
      if (!CobbleNotify.config.isNotifycatch()) return Unit.INSTANCE;
      try {
        Player player = evt.getPlayer();
        Pokemon pokemon = evt.getPokemon();

        if (!pokemon.getShiny() && !pokemon.isLegendary()) return Unit.INSTANCE;
        String message;
        /*synchronized (SpawnPokemonEvent.pokemonsLiving) {
          SpawnPokemonEvent.pokemonsLiving.remove(pokemon);
        }*/
        if (pokemon.isLegendary()) {
          message = CobbleNotify.language.getMessagecatchlegendary();
        } else if (pokemon.getShiny()) {
          message = CobbleNotify.language.getMessagecatchshiny();
        } else {
          return Unit.INSTANCE;
        }

        NotifyUtils.broadcast(PokemonUtils.replace(message.replace("%player%", player.getName().getString()), pokemon));
      } catch (Exception e) {
        System.err.println("Se produjo un error al procesar el evento de captura de Pokemon: " + e.getMessage());
        e.printStackTrace();
      }

      return Unit.INSTANCE;
    });
  }
}

