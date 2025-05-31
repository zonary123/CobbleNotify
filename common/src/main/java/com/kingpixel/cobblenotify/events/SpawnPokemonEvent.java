package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.Model.InfoSpawn;
import com.kingpixel.cobblenotify.Model.Notification;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import kotlin.Unit;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Box;
import net.minecraft.world.biome.Biome;

import java.util.List;

public class SpawnPokemonEvent {

  public static void registerEvents() {

    EntityEvent.ADD.register((entity, world) -> {
      if (!CobbleNotify.config.isAffectCommands()) return EventResult.pass();
      if (entity instanceof PokemonEntity pokemonEntity) {
        handleNotification(pokemonEntity);
      }
      return EventResult.pass();
    });

    CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.LOW, evt -> {
      if (CobbleNotify.config.isAffectCommands()) return Unit.INSTANCE;
      handleNotification(evt.getEntity());
      return Unit.INSTANCE;
    });
  }

  private static void handleNotification(PokemonEntity pokemonEntity) {
    try {
      InfoSpawn info = createInfoSpawn(pokemonEntity);
      List<ServerPlayerEntity> players = pokemonEntity.getWorld().getEntitiesByClass(
        ServerPlayerEntity.class,
        Box.from(pokemonEntity.getPos()).expand(CobbleNotify.config.getDistance()),
        player -> true
      );
      var pokemonEntitys = List.of(pokemonEntity);

      Notification notification = Notification.handleEvent(List.of(pokemonEntity.getPokemon()), players,
        Notification.EventType.SPAWN, info, pokemonEntitys);
      if (notification != null) {
        notification.getSound().start(pokemonEntity);
        notification.getParticle().sendParticlesNearPlayers(pokemonEntity);
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }


  private static InfoSpawn createInfoSpawn(PokemonEntity pokemonEntity) {
    InfoSpawn info = new InfoSpawn();
    info.setPokemon(pokemonEntity.getPokemon());
    String biome;
    String world;


    try {
      RegistryEntry<Biome> biomeRegistry = pokemonEntity.getWorld().getBiome(pokemonEntity.getBlockPos());
      biome = "<lang:biome." + biomeRegistry.getIdAsString()
        .replace(":", ".") + ">";
    } catch (Exception ignored) {
      biome = "Unknown";
    }

    try {
      world = pokemonEntity.getEntityWorld().getRegistryKey().getValue() + "";
    } catch (Exception ignored) {
      world = "Unknown";
    }

    info.setBiome(biome);
    info.setWorld(world);
    info.setX(Math.floor(pokemonEntity.getX()));
    info.setY(Math.floor(pokemonEntity.getY()));
    info.setZ(Math.floor(pokemonEntity.getZ()));

    return info;
  }

}
