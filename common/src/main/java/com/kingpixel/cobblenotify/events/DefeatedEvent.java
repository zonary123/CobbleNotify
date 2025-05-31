package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.Model.Notification;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 25/05/2024 21:48
 */
public class DefeatedEvent {
  public static void registerEvents() {
    CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOW, (evt) -> {
      try {
        List<ServerPlayerEntity> players = new ArrayList<>();
        List<Pokemon> pokemons = new ArrayList<>();

        for (BattleActor winner : evt.getWinners()) {
          if (winner instanceof PlayerBattleActor playerBattleActor) {
            players.add(playerBattleActor.getEntity());
          }
        }

        for (BattleActor loser : evt.getLosers()) {
          if (loser instanceof PokemonBattleActor pokemonBattleActor) {
            pokemons.add(pokemonBattleActor.getPokemon().getOriginalPokemon());
          }
        }

        Notification.handleEvent(pokemons, players, Notification.EventType.DEFEAT, null, null);
        return Unit.INSTANCE;
      } catch (Exception e) {
        e.printStackTrace();

      }
      return Unit.INSTANCE;
    });
  }
}
