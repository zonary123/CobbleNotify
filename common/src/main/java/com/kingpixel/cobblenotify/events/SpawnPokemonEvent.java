package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.EntityBackedBattleActor;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.Model.InfoSpawn;
import com.kingpixel.cobblenotify.utils.NotifyUtils;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public class SpawnPokemonEvent {
  //private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
  //public static final List<PokemonEntity> pokemonsLiving = new ArrayList<>();

  public static void registerEvents() {
    if (CobbleNotify.config.isNotifyspawn()) {
      EntityEvent.ADD.register((entity, world) -> {
        if (!CobbleNotify.config.isCommandaffected()) return EventResult.pass();
        if (!CobbleNotify.config.isNotifyspawn()) return EventResult.pass();
        if (entity instanceof EntityBackedBattleActor<?>) return EventResult.pass();
        if (entity instanceof PokemonEntity pokemon) {
          if (((PokemonEntity) entity).isPersistenceRequired()) return EventResult.pass();
          if (pokemon.getPokemon().isPlayerOwned()) return EventResult.pass();
          if (!pokemon.getPokemon().isWild()) return EventResult.pass();
          spawned(entity, pokemon, world);
        }
        return EventResult.pass();
      });

      CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, (evt) -> {
        if (CobbleNotify.config.isCommandaffected()) return Unit.INSTANCE;
        if (!CobbleNotify.config.isNotifyspawn()) return Unit.INSTANCE;
        if (evt.getEntity().isPersistenceRequired()) return Unit.INSTANCE;
        PokemonEntity pokemonEntity = evt.getEntity();
        if (pokemonEntity.getPokemon().isPlayerOwned()) return Unit.INSTANCE;
        if (!pokemonEntity.getPokemon().isWild()) return Unit.INSTANCE;
        spawned(pokemonEntity, pokemonEntity, ((Entity) pokemonEntity).level());
        return Unit.INSTANCE;
      });
    }


    EntityEvent.LIVING_DEATH.register((entity, damageSource) -> {
      if (entity instanceof EntityBackedBattleActor<?>) return EventResult.pass();
      if (entity instanceof PokemonEntity pokemon) {
        if (pokemon.getPokemon().isPlayerOwned()) return EventResult.pass();
        death(pokemon.getPokemon(), damageSource);
      }
      return EventResult.pass();
    });
  }

  private static void despawned(Pokemon pokemon) {
    if (pokemon.isPlayerOwned()) return;

    String message = PokemonUtils.replace(CobbleNotify.language.getMessageDespawnPokemon(), pokemon);
    NotifyUtils.broadcast(message);
  }

  private static void death(Pokemon pokemon, DamageSource damageSource) {
    if (pokemon.isPlayerOwned() || (!pokemon.getShiny() && !pokemon.isLegendary())) {
      return;
    }
    String message = PokemonUtils.replace(CobbleNotify.language.getMessageDeathPokemon().replace("%entity%",
        damageSource.getEntity() == null ? "Unknown" : damageSource.getEntity().getName().getString()),
      pokemon);
    NotifyUtils.broadcast(message);
  }

  private static void spawned(Entity entity, PokemonEntity pokemon, Level world) {
    InfoSpawn info = new InfoSpawn();
    info.setPokemon(pokemon.getPokemon().getSpecies().getName());
    info.setForm(pokemon.getPokemon().getForm().getName());

    Optional<ResourceKey<Biome>> optional = world.getBiome(new BlockPos(entity.getBlockX(), entity.getBlockY(),
        entity.getBlockZ()))
      .unwrapKey();

    String biome = optional
      .map(ResourceKey::location)
      .map(resourceLocation -> "<lang:biome." + resourceLocation.getNamespace() + "." + resourceLocation.getPath() +
        ">")
      .orElse("Unknown");

    info.setBiome(biome);
    world.dimension();
    info.setWorld(world.dimension().location().getPath());
    info.setX(entity.getX());
    info.setY(entity.getY());
    info.setZ(entity.getZ());

    Player nearestPlayer = world.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(), CobbleNotify.config.getDistanceplayer(), false);
    info.setPlayer(nearestPlayer != null ? nearestPlayer.getDisplayName().getString() : CobbleUtils.language.getNone());

    if (CobbleNotify.config.isSpecialPokemon(pokemon.getPokemon())) {
      if (CobbleNotify.config.isNotifyspawnnearplayer()) {
        if (nearestPlayer != null) {
          NotifyUtils.adventure((ServerPlayer) nearestPlayer, notifyString(CobbleNotify.language.getMessageSpecialPokemon(), info,
            pokemon.getPokemon()));
        } else {
          NotifyUtils.broadcast(notifyString(CobbleNotify.language.getMessageSpecialPokemon(), info, pokemon.getPokemon()));
        }
      } else {
        NotifyUtils.broadcast(notifyString(CobbleNotify.language.getMessageSpecialPokemon(), info, pokemon.getPokemon()));
      }
      return;
    }

    if (CobbleNotify.config.isLegendary() && pokemon.getPokemon().isLegendary()) {
      if (CobbleNotify.config.isNotifyspawnnearplayer()) {
        if (nearestPlayer != null) {
          NotifyUtils.adventure((ServerPlayer) nearestPlayer, notifyString(CobbleNotify.language.getMessagenotifylegendary(), info,
            pokemon.getPokemon()));
        } else {
          NotifyUtils.broadcast(notifyString(CobbleNotify.language.getMessagenotifylegendary(), info, pokemon.getPokemon()));
        }
      } else {
        NotifyUtils.broadcast(notifyString(CobbleNotify.language.getMessagenotifylegendary(), info, pokemon.getPokemon()));
      }
    } else if (CobbleNotify.config.isShiny() && pokemon.getPokemon().getShiny()) {
      if (CobbleNotify.config.isNotifyspawnnearplayer()) {
        if (nearestPlayer != null) {
          NotifyUtils.adventure((ServerPlayer) nearestPlayer, notifyString(CobbleNotify.language.getMessagenotifyshiny(), info,
            pokemon.getPokemon()));
        } else {
          NotifyUtils.broadcast(notifyString(CobbleNotify.language.getMessagenotifyshiny(), info, pokemon.getPokemon()));
        }
      } else {
        NotifyUtils.broadcast(notifyString(CobbleNotify.language.getMessagenotifyshiny(), info, pokemon.getPokemon()));
      }
    }
  }

  private static String notifyString(String text, InfoSpawn info, Pokemon pokemon) {
    if (text.contains("%")) {
      return PokemonUtils.replace(text.replace("%biome%",
          info.getBiome())
        .replace("%world%", CobbleNotify.language.getWorlds().getOrDefault(info.getWorld(), info.getWorld()))
        .replace("%player%", info.getPlayer())
        .replace("%x%", String.valueOf((int) info.getX()))
        .replace("%y%", String.valueOf((int) info.getY()))
        .replace("%z%", String.valueOf((int) info.getZ())), pokemon);
    } else {
      return text;
    }
  }
}
