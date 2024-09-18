package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.BattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.cobblemon.mod.common.battles.actor.PokemonBattleActor;
import com.cobblemon.mod.common.pokemon.OriginalTrainerType;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.utils.NotifyUtils;
import com.kingpixel.cobbleutils.Model.CobbleUtilsTags;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;

/**
 * @author Carlos Varas Alonso - 25/05/2024 21:48
 */
public class DefeatedEvent {
  public static void registerEvents() {
    CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOW, (evt) -> {
      if (!CobbleNotify.config.isNotifydefeat()) return Unit.INSTANCE;

      ServerPlayer player = null;
      Pokemon pokemon = null;

      try {
        for (BattleActor winner : evt.getWinners()) {
          if (winner instanceof PlayerBattleActor playerBattleActor) {
            player = playerBattleActor.getEntity();
          }
        }

        for (BattleActor loser : evt.getLosers()) {
          if (loser instanceof PokemonBattleActor pokemonBattleActor) {
            pokemon = pokemonBattleActor.getPokemon().getOriginalPokemon();
          }
        }

        if (player == null) return Unit.INSTANCE;
        if (pokemon == null || pokemon.isPlayerOwned() || pokemon.getOriginalTrainerType() == OriginalTrainerType.PLAYER || pokemon.getOriginalTrainerType() == OriginalTrainerType.NPC || !pokemon.isWild())
          return Unit.INSTANCE;

        if (pokemon.getPersistentData().getBoolean(CobbleUtilsTags.BOSS_TAG)) return Unit.INSTANCE;

        String message;
        if (pokemon.isLegendary()) {
          message = PokemonUtils.replace(
            CobbleNotify.language.getMessagedefeatlegendary()
              .replace("%player%", player.getGameProfile().getName()),
            pokemon
          );
        } else if (pokemon.getShiny()) {
          message = PokemonUtils.replace(
            CobbleNotify.language.getMessagedefeatshiny()
              .replace("%player%", player.getGameProfile().getName()),
            pokemon
          );
        } else {
          return Unit.INSTANCE;
        }

        // Enviar la notificación
        NotifyUtils.broadcast(message);
      } catch (Exception e) {
        CobbleNotify.LOGGER.error("Error procesando el evento de derrota de Pokémon: " + e.getMessage());
      }

      return Unit.INSTANCE;
    });
  }
}
