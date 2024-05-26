package com.kingpixel.cobblespawnnotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblespawnnotify.SpawnNotify;
import com.kingpixel.cobblespawnnotify.utils.Utils;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.Objects;

/**
 * @author Carlos Varas Alonso - 25/05/2024 21:48
 */
public class DefeatedCatch {
  public static void registerEvents() {

    CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOW, (evt) -> {
      if (!SpawnNotify.config.isNotifydefeat()) return Unit.INSTANCE;
      try {
        List<ServerPlayer> playerWinners =
          evt.getWinners().stream().filter(actor -> actor instanceof PlayerBattleActor)
            .map(actor -> ((PlayerBattleActor) actor).getEntity())
            .filter(Objects::nonNull)
            .toList();

        Pokemon pokemon = new Pokemon();
        if (playerWinners.isEmpty()) return Unit.INSTANCE;
        Player player = playerWinners.get(0);
        for (BattleActor winner : evt.getWinners()) {
          if (winner instanceof PlayerBattleActor playerBattleActor) {
            player = playerBattleActor.getEntity();
            PokemonBattleActor pokemonActor = (PokemonBattleActor) winner.getSide().getOppositeSide().getActors()[0];
            pokemon = pokemonActor.getPokemon().getOriginalPokemon();
            break;
          }
        }


        String s = "";
        if (pokemon.isLegendary()) {
          s = SpawnNotify.language.getMessagedefeatlegendary()
            .replace("%pokemon%", pokemon.getSpecies().getName())
            .replace("%form%", SpawnNotify.language.getForms().getOrDefault(pokemon.getForm().getName(), pokemon.getForm().getName()))
            .replace("%player%", player.getGameProfile().getName());
        } else if (pokemon.getShiny()) {
          s = SpawnNotify.language.getMessagedefeatshiny()
            .replace("%pokemon%", pokemon.getSpecies().getName())
            .replace("%form%", SpawnNotify.language.getForms().getOrDefault(pokemon.getForm().getName(), pokemon.getForm().getName()))
            .replace("%player%", player.getGameProfile().getName());
        } else {
          return Unit.INSTANCE;
        }

        Utils.broadcastMessage(s);

      } catch (Exception e) {
        System.err.println("Se produjo un error al procesar el evento de captura de Pokemon: " + e.getMessage());
        e.printStackTrace();
      }

      return Unit.INSTANCE;
    });
  }
}

