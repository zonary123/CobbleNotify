package com.kingpixel.cobblenotify.models;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.models.enums.Actions;
import com.kingpixel.cobblenotify.models.history.HistorySpawn;
import com.kingpixel.cobblenotify.models.history.HistoryTrade;
import com.kingpixel.cobblenotify.models.notification.NotificationOptions;
import com.kingpixel.cobblenotify.models.webhook.WebHookOptions;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.Model.PokemonBlackList;
import com.kingpixel.cobbleutils.Model.PokemonFormula;
import com.kingpixel.cobbleutils.util.MinecraftUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import lombok.Data;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;

import java.util.List;
import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:40
 */
@Data
public class Notification {
  private String identifier;
  private int priority;
  private ItemModel icon;
  private boolean useFormula;
  private double minValue;
  private PokemonFormula formula;
  private PokemonBlackList filter;
  private NotificationOptions notificationOptions;
  private WebHookOptions webHookOptions;

  public Notification(String identifier) {
    this.identifier = identifier;
    this.priority = 0;
    this.icon = new ItemModel("minecraft:paper", "§e" + identifier);
    this.useFormula = false;
    this.minValue = 2.0;
    this.formula = new PokemonFormula();
    this.filter = new PokemonBlackList();
    this.notificationOptions = new NotificationOptions();
    this.webHookOptions = new WebHookOptions();
  }

  public void check() {
  }

  public boolean isValid(Pokemon pokemon) {
    if (CobbleNotify.config.getGlobalBlackList().isBlackListed(pokemon)) return false;
    if (useFormula) {
      return formula.getPokemonValue(pokemon) >= minValue;
    } else return filter.isBlackListed(pokemon);
  }

  public boolean computeCaught(Pokemon pokemon, ServerPlayerEntity player) {
    if (!isValid(pokemon)) return false;
    boolean notified = false;
    // Send Message notification
    if (notificationOptions.isCaught()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getCatchMessage();
      var content = message.getRawMessage();
      content = PokemonUtils.replace(
        replaceVariables(pokemon.getEntity(), content)
          .replace("%player%", player.getGameProfile().getName()),
        pokemon
      );
      message.sendMessage((UUID) null, content, CobbleNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.CAUGHT, List.of(pokemon));
    if (CobbleNotify.databaseClient == null) return notified;
    if (notified) CobbleNotify.runAsync(() -> {
      var history = CobbleNotify.databaseClient.getSpawnedPokemonById(pokemon.getUuid());
      history.caught(player);
      CobbleNotify.databaseClient.updateHistorySpawn(history);
    });
    return notified;
  }

  public boolean computeDefeat(Pokemon pokemon, ServerPlayerEntity player) {
    if (!pokemon.isWild()) return false;
    if (!isValid(pokemon)) return false;
    boolean notified = false;
    // Send Message notification
    if (notificationOptions.isDefeat()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getDefeatMessage();
      var content = message.getRawMessage();
      content = PokemonUtils.replace(
        replaceVariables(pokemon.getEntity(), content)
          .replace("%player%", player.getGameProfile().getName())
        , pokemon
      );
      message.sendMessage((UUID) null, content, CobbleNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.DEFEAT, List.of(pokemon));
    return notified;
  }

  /**
   * Compute the spawn notification
   *
   * @param pokemonEntity The spawned Pokemon entity
   *
   * @return true if a notification was sent
   */
  public boolean computeSpawn(PokemonEntity pokemonEntity) {
    Pokemon pokemon = pokemonEntity.getPokemon();
    if (!pokemon.isWild() || pokemonEntity.isPersistent()) return false;
    if (!isValid(pokemon)) return false;
    // Send Message notification
    boolean notified = false;
    if (notificationOptions.isSpawn()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getSpawnMessage();
      var content = message.getRawMessage();
      content = PokemonUtils.replace(replaceVariables(pokemonEntity, content), pokemon);
      message.sendMessage((UUID) null, content, CobbleNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.SPAWN, List.of(pokemon));
    // Save to database
    if (CobbleNotify.databaseClient == null) return notified;
    if (notified) {
      Box boundingBox = pokemonEntity.getBoundingBox().expand(64);
      var players = pokemonEntity.getEntityWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), boundingBox,
        p -> true);
      CobbleNotify.runAsync(() -> CobbleNotify.databaseClient.addSpawnedPokemon(new HistorySpawn(pokemonEntity,
        players, this)));

    }
    return notified;
  }

  /**
   * Compute the trade notification
   *
   * @param player1  Player 1 of the trade
   * @param pokemon1 Pokemon 1 of the trade
   * @param player2  Player 2 of the trade
   * @param pokemon2 Pokemon 2 of the trade
   *
   * @return true if a notification was sent
   */
  public boolean computeTrade(Pokemon pokemon1, Pokemon pokemon2, ServerPlayerEntity player1,
                              ServerPlayerEntity player2) {
    if (!isValid(pokemon1) && !isValid(pokemon2)) return false;
    boolean notified = false;
    // Send Message notificationç
    if (notificationOptions.isTrade()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getTradeMessage();
      var content = message.getRawMessage();
      content = PokemonUtils.replace(content, List.of(pokemon1, pokemon2));
      content = content.replace("%player1%", player1.getGameProfile().getName());
      content = content.replace("%player2%", player2.getGameProfile().getName());
      message.sendMessage((UUID) null, content, CobbleNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.TRADE, List.of(pokemon1, pokemon2));
    // Save to database
    if (notified) {
      if (CobbleNotify.databaseClient == null) return notified;
      HistoryTrade historyTrade = new HistoryTrade(pokemon1, pokemon2, player1, player2, this);
      CobbleNotify.runAsync(() -> CobbleNotify.databaseClient.addTradeHistory(historyTrade));
    }
    return notified;
  }

  public static String replaceVariables(PokemonEntity pokemonEntity, String message) {
    if (pokemonEntity == null) return message;
    var x = (int) pokemonEntity.getX();
    var y = (int) pokemonEntity.getY();
    var z = (int) pokemonEntity.getZ();
    var world = pokemonEntity.getEntityWorld();
    message = message.replace("%x%", String.valueOf(x));
    message = message.replace("%y%", String.valueOf(y));
    message = message.replace("%z%", String.valueOf(z));
    message = message.replace("%world%", MinecraftUtils.getWorldTranslate(world));
    message = message.replace("%biome%", MinecraftUtils.getBiomesTranslate(world.getBiome(pokemonEntity.getBlockPos())));
    message = message.replace("%server%", CobbleUtils.config.getServer());
    return message;
  }


}
