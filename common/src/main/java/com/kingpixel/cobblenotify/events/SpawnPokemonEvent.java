package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.Model.InfoSpawn;
import com.kingpixel.cobblenotify.Model.Notification;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import net.minecraft.entity.Entity;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.List;
import java.util.Optional;

public class SpawnPokemonEvent {

  public static void registerEvents() {

    EntityEvent.ADD.register((entity, world) -> {
      if (entity instanceof PokemonEntity pokemonEntity) {
        InfoSpawn info = createInfoSpawn(entity, pokemonEntity, world);
        List<ServerPlayerEntity> players = entity.getWorld().getEntitiesByClass(
          ServerPlayerEntity.class,
          Box.from(entity.getPos()).expand(CobbleNotify.config.getDistance()),
          player -> true
        );

        Notification notification = Notification.handleEvent(List.of(pokemonEntity.getPokemon()), players,
          Notification.EventType.SPAWN, info);
        if (notification != null) {
          notification.getSound().start(entity);
          notification.getParticle().sendParticlesNearPlayers(entity);
        }
      }
      return EventResult.pass();
    });
  }


  private static InfoSpawn createInfoSpawn(Entity entity, PokemonEntity pokemonEntity, World world) {
    InfoSpawn info = new InfoSpawn();
    info.setPokemon(pokemonEntity.getPokemon());

    Optional<RegistryKey<Biome>> optionalBiome = world.getBiome(new BlockPos(entity.getBlockX(), entity.getBlockY(),
      entity.getBlockZ())).getKey();
    String biome = optionalBiome.map(key -> "<lang:biome." + key.getValue().toTranslationKey() + ">").orElse("Unknown");

    info.setBiome(biome);
    info.setWorld("world." + world.getDimensionEntry().getIdAsString().replace(":", "."));
    info.setX(Math.floor(entity.getX()));
    info.setY(Math.floor(entity.getY()));
    info.setZ(Math.floor(entity.getY()));

    return info;
  }

}
