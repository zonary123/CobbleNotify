package com.kingpixel.cobblespawnnotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.spawning.context.SpawningContext;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.kingpixel.cobblespawnnotify.Model.InfoSpawn;
import com.kingpixel.cobblespawnnotify.SpawnNotify;
import com.kingpixel.cobblespawnnotify.utils.Utils;
import kotlin.Unit;
import net.minecraft.world.entity.player.Player;

/**
 * @author Carlos Varas Alonso - 25/05/2024 18:55
 */
public class SpawnPokemonEvent {
  public static void registerEvents() {
    CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, (evt) -> {
      if (!SpawnNotify.config.isNotifyspawn()) return Unit.INSTANCE;
      try {
        PokemonEntity pokemon = evt.getEntity();
        SpawningContext context = evt.getCtx();
        logic(pokemon, context);
      } catch (Exception e) {
        // Aquí puedes manejar el error como prefieras. Por ejemplo, puedes imprimir el error en la consola:
        System.err.println("Se produjo un error al procesar el evento de spawn de Pokemon: " + e.getMessage());
        e.printStackTrace();
      }
      return Unit.INSTANCE;
    });

  }

  public static void logic(PokemonEntity pokemon, SpawningContext context) {
    InfoSpawn info = new InfoSpawn();

    info.setPokemon(pokemon.getPokemon().getSpecies().getName());
    info.setForm(pokemon.getPokemon().getForm().getName());
    info.setBiome(context.getBiomeName().getPath());
    info.setWorld(context.getWorld().dimension().location().getPath());
    info.setX(context.getPosition().getX());
    info.setY(context.getPosition().getY());
    info.setZ(context.getPosition().getZ());

    Player nearestPlayer = context.getWorld().getNearestPlayer(info.getX(), info.getY(), info.getZ(),
      SpawnNotify.config.getDistanceplayer(), false);
    info.setPlayer(nearestPlayer != null && nearestPlayer.getGameProfile().getName() != null ? nearestPlayer.getGameProfile().getName() : "");

    if (SpawnNotify.config.isLegendary() && pokemon.getPokemon().isLegendary()) {
      Utils.broadcastMessage(notifyString(SpawnNotify.language.getMessagenotifylegendary(), info));
    } else if (SpawnNotify.config.isShiny() && pokemon.getPokemon().getShiny()) {
      Utils.broadcastMessage(notifyString(SpawnNotify.language.getMessagenotifyshiny(), info));
    }
  }

  private static String notifyString(String texto, InfoSpawn info) {
    String s;
    if (texto.contains("%")) {
      s = texto.replace("%pokemon%", info.getPokemon())
        .replace("%biome%", SpawnNotify.language.getBiomes().getOrDefault(info.getBiome(), info.getBiome()))
        .replace("%form%", SpawnNotify.language.getForms().getOrDefault(info.getForm(), info.getForm()))
        .replace("%world%", SpawnNotify.language.getWorlds().getOrDefault(info.getWorld(), info.getWorld()))
        .replace("%player%", info.getPlayer())
        .replace("%x%", String.valueOf(info.getX()))
        .replace("%y%", String.valueOf(info.getY()))
        .replace("%z%", String.valueOf(info.getZ()))
        .replace("%prefix%", SpawnNotify.language.getPrefix());
    } else {
      s = texto;
    }

    return s;
  }
}
