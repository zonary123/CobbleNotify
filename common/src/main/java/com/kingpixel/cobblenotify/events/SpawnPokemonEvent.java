package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.Model.InfoSpawn;
import com.kingpixel.cobblenotify.utils.NotifyUtils;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.CobbleUtilsTags;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import kotlin.Unit;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

public class SpawnPokemonEvent {

  public static void registerEvents() {
    if (CobbleNotify.config.isNotifyspawn()) {
      EntityEvent.ADD.register((entity, world) -> handleEntityAdd(entity, world));

      CobblemonEvents.POKEMON_ENTITY_SPAWN.subscribe(Priority.NORMAL, (evt) -> {
        if (!CobbleNotify.config.isCommandaffected() && CobbleNotify.config.isNotifyspawn()) {
          PokemonEntity pokemonEntity = evt.getEntity();
          if (isValidWildPokemon(pokemonEntity)) {
            spawned(pokemonEntity, pokemonEntity, ((Entity) pokemonEntity).level());
          }
        }
        return Unit.INSTANCE;
      });

      EntityEvent.ADD.register((entity, world) -> {
        if (CobbleNotify.config.isCommandaffected()) return EventResult.pass();
        if (entity instanceof PokemonEntity pokemon && isValidWildPokemon(pokemon) && CobbleNotify.config.isNotifycatch()) {
          if (!pokemon.getPokemon().getPersistentData().getBoolean(CobbleUtilsTags.BOSS_TAG)) return EventResult.pass();
          spawned(entity, pokemon, world);
        }
        return EventResult.pass();
      });
    }

    EntityEvent.LIVING_DEATH.register((entity, damageSource) -> {
      if (entity instanceof PokemonEntity pokemon) {
        if (!pokemon.getPokemon().isPlayerOwned()) {
          death(pokemon.getPokemon(), damageSource);
        }
      }
      return EventResult.pass();
    });
  }

  private static EventResult handleEntityAdd(Entity entity, Level world) {
    if (!CobbleNotify.config.isCommandaffected() || !(entity instanceof PokemonEntity pokemon))
      return EventResult.pass();
    if (isValidWildPokemon(pokemon)) spawned(entity, pokemon, world);
    return EventResult.pass();
  }

  private static boolean isValidWildPokemon(PokemonEntity pokemonEntity) {
    Pokemon pokemon = pokemonEntity.getPokemon();
    boolean isPersistent;
    try {
      isPersistent = ((Mob) pokemonEntity).isPersistenceRequired();
    } catch (ClassCastException e) {
      isPersistent = false;
    }
    return !isPersistent && pokemon.isWild() && !pokemon.isPlayerOwned();
  }

  private static void death(Pokemon pokemon, DamageSource damageSource) {
    if (pokemon.isPlayerOwned() || (!pokemon.getShiny() && !pokemon.isLegendary())) return;
    String entityName = damageSource.getEntity() == null ? "Unknown" : damageSource.getEntity().getName().getString();
    String message = PokemonUtils.replace(CobbleNotify.language.getMessageDeathPokemon().replace("%entity%", entityName), pokemon);
    NotifyUtils.broadcast(message);
  }

  private static void spawned(Entity entity, PokemonEntity pokemonEntity, Level world) {
    Pokemon pokemon = pokemonEntity.getPokemon();

    if (isBlacklisted(pokemon)) return;

    InfoSpawn info = createInfoSpawn(entity, pokemonEntity, world);
    Player nearestPlayer = world.getNearestPlayer(entity.getX(), entity.getY(), entity.getZ(),
      CobbleNotify.config.getDistanceplayer(), false);
    info.setPlayer(nearestPlayer != null ? nearestPlayer.getDisplayName().getString() : CobbleUtils.language.getNone());

    if (isPokemonSpecialOrMatching(pokemon, nearestPlayer, info)) return;

    if (pokemon.getPersistentData().getBoolean(CobbleUtilsTags.BOSS_TAG)) {
      notifySpawn(nearestPlayer, info, CobbleNotify.language.getMessageNotifySpawnBoss()
        .replace("%rarity%", pokemon.getPersistentData().getString(CobbleUtilsTags.BOSS_RARITY_TAG)), pokemon);
      return;
    }
    if (shouldNotify(pokemon, nearestPlayer, info, CobbleNotify.config.isLegendary(), pokemon.isLegendary())) return;
    shouldNotify(pokemon, nearestPlayer, info, CobbleNotify.config.isShiny(), pokemon.getShiny());
  }

  private static boolean isBlacklisted(Pokemon pokemon) {
    return CobbleNotify.config.getBlacklistedPokemon()
      .stream()
      .anyMatch(id -> id.equalsIgnoreCase(pokemon.showdownId()));
  }

  private static InfoSpawn createInfoSpawn(Entity entity, PokemonEntity pokemonEntity, Level world) {
    InfoSpawn info = new InfoSpawn();
    info.setPokemon(pokemonEntity.getPokemon());

    Optional<ResourceKey<Biome>> optionalBiome = world.getBiome(new BlockPos(entity.getBlockX(), entity.getBlockY(), entity.getBlockZ())).unwrapKey();
    String biome = optionalBiome.map(key -> "<lang:biome." + key.location().toLanguageKey() + ">").orElse("Unknown");

    info.setBiome(biome);
    info.setWorld(world.dimension().location().getPath());
    info.setX(entity.getX());
    info.setY(entity.getY());
    info.setZ(entity.getZ());

    return info;
  }

  private static boolean isPokemonSpecialOrMatching(Pokemon pokemon, Player nearestPlayer, InfoSpawn info) {
    boolean isSpecial = CobbleNotify.config.isSpecialPokemon(pokemon);

    boolean matchesLabelOrForm = CobbleNotify.config.getLabelsandforms()
      .stream()
      .anyMatch(label -> pokemon.getForm().getLabels().contains(label) || pokemon.getForm().getName().equalsIgnoreCase(label));

    if (isSpecial || matchesLabelOrForm) {
      notifySpawn(nearestPlayer, info, CobbleNotify.language.getMessageSpecialPokemon(), pokemon);
      return true;
    }
    return false;
  }

  private static boolean shouldNotify(Pokemon pokemon, Player nearestPlayer, InfoSpawn info, boolean configCondition, boolean pokemonCondition) {
    if (configCondition && pokemonCondition) {
      String messageKey = pokemon.isLegendary() ? CobbleNotify.language.getMessagenotifylegendary() : CobbleNotify.language.getMessagenotifyshiny();
      notifySpawn(nearestPlayer, info, messageKey, pokemon);
      return true;
    }
    return false;
  }

  private static void notifySpawn(Player nearestPlayer, InfoSpawn info, String messageKey, Pokemon pokemon) {
    String message = notifyString(messageKey, info, pokemon);

    if (CobbleNotify.config.isNotifyspawnnearplayer() && nearestPlayer != null) {
      NotifyUtils.adventure((ServerPlayer) nearestPlayer, message);
    } else {
      NotifyUtils.broadcast(message);
    }
  }

  private static String notifyString(String text, InfoSpawn info, Pokemon pokemon) {
    return PokemonUtils.replace(text.replace("%biome%", info.getBiome())
      .replace("%world%", CobbleNotify.language.getWorlds().getOrDefault(info.getWorld(), info.getWorld()))
      .replace("%player%", info.getPlayer())
      .replace("%x%", String.valueOf((int) info.getX()))
      .replace("%y%", String.valueOf((int) info.getY()))
      .replace("%z%", String.valueOf((int) info.getZ())), pokemon);
  }
}
