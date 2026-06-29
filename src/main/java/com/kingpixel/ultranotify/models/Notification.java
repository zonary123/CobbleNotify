package com.kingpixel.ultranotify.models;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.moves.Move;
import com.cobblemon.mod.common.api.pokemon.PokemonProperties;
import com.cobblemon.mod.common.api.types.ElementalType;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Nature;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.Model.PokemonBlackList;
import com.kingpixel.cobbleutils.util.MinecraftUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.models.enums.Actions;
import com.kingpixel.ultranotify.models.history.HistorySpawn;
import com.kingpixel.ultranotify.models.history.HistoryTrade;
import com.kingpixel.ultranotify.models.notification.NotificationMessages;
import com.kingpixel.ultranotify.models.notification.NotificationOptions;
import com.kingpixel.ultranotify.models.webhook.WebHookMessages;
import com.kingpixel.ultranotify.models.webhook.WebHookOptions;
import com.kingpixel.ultranotify.utils.NotificationUtils;
import com.mojang.authlib.GameProfile;
import lombok.Data;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import org.jspecify.annotations.Nullable;

import java.util.Iterator;
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
  private WorldFilter worldFilter;
  private boolean useProperties;
  private String properties;
  private PokemonBlackList filter;
  private NotificationOptions notificationOptions;
  private WebHookOptions webHookOptions;

  public Notification(String identifier) {
    this.identifier = identifier;
    this.priority = 0;
    this.icon = new ItemModel("minecraft:paper", "§e" + identifier);
    this.worldFilter = new WorldFilter();
    this.useProperties = false;
    this.properties = "shiny=true";
    this.filter = PokemonBlackList.createBlackList();
    this.notificationOptions = new NotificationOptions();
    this.webHookOptions = new WebHookOptions();
  }

  public void check() {
    if (identifier == null || identifier.isBlank()) identifier = "notification";
    if (properties == null || properties.isBlank()) properties = "shiny=true";
    if (icon == null) icon = new ItemModel("minecraft:paper", "§e" + identifier);
    if (worldFilter == null) worldFilter = new WorldFilter();
    if (filter == null) filter = PokemonBlackList.createBlackList();
    if (notificationOptions == null) notificationOptions = new NotificationOptions();
    if (notificationOptions.getNotificationMessages() == null)
      notificationOptions.setNotificationMessages(new NotificationMessages());
    if (webHookOptions == null) webHookOptions = new WebHookOptions();
    if (webHookOptions.getWebHookMessages() == null)
      webHookOptions.setWebHookMessages(new WebHookMessages());
  }

  public boolean isValid(Pokemon pokemon, @Nullable PokemonEntity entity) {
    if (entity != null) if (entity.isAiDisabled() || entity.isUncatchable()) return false;
    if (UltraNotify.config.getGlobalBlackList().isBlackListed(pokemon)) return false;
    if (useProperties) {
      return PokemonProperties.Companion.parse(properties).matches(pokemon);
    } else return filter.isBlackListed(pokemon);
  }

  public boolean computeCaught(Pokemon pokemon, ServerPlayerEntity player) {
    if (!isValid(pokemon, null)) return false;
    boolean notified = false;
    // Send Message notification
    if (notificationOptions.isCaught() && !NotificationUtils.playerIsVanish(player)) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getCatchMessage();
      var content = message.getRawMessage();
      content = replaceVariables(
        pokemon.getEntity(), pokemon, PokemonUtils.replace(
          content
            .replace("%player%", player.getGameProfile().getName()),
          pokemon
        )
      );
      message.sendMessage((UUID) null, content, UltraNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.CAUGHT, List.of(pokemon), player, null);
    if (UltraNotify.databaseClient == null) return notified;
    if (notified) UltraNotify.runAsync(() -> {
      var history = UltraNotify.databaseClient.getSpawnedPokemonById(pokemon.getUuid());
      if (history == null) return;
      history.caught(player);
      UltraNotify.databaseClient.updateHistorySpawn(history);
    });
    return notified;
  }

  public boolean computeDefeat(Pokemon pokemon, ServerPlayerEntity player, PokemonEntity entity) {
    if (!pokemon.isWild()) return false;
    if (!isValid(pokemon, entity)) return false;
    boolean notified = false;
    // Send Message notification
    if (notificationOptions.isDefeat()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getDefeatMessage();
      var content = message.getRawMessage();
      content = replaceVariables(pokemon.getEntity(), pokemon, PokemonUtils.replace(content, pokemon))
        .replace("%player%", player.getGameProfile().getName());
      message.sendMessage((UUID) null, content, UltraNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.DEFEAT, List.of(pokemon), player, null);
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
    if (!isValid(pokemon, pokemonEntity)) return false;
    // Send Message notification
    Box boundingBox = pokemonEntity.getBoundingBox().expand(64);
    var players = pokemonEntity.getEntityWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class), boundingBox, p -> true);
    var visiblePlayers = players.stream()
      .filter(p -> !p.isSpectator() && !NotificationUtils.playerIsVanish((ServerPlayerEntity) p))
      .toList();
    boolean notified = false;
    if (notificationOptions.isSpawn() && (players.isEmpty() || !visiblePlayers.isEmpty())) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getSpawnMessage();
      var content = message.getRawMessage();
      content = replaceVariables(pokemonEntity, pokemon, PokemonUtils.replace(content, pokemon));
      String nearestPlayers = String.join(", ", visiblePlayers.stream()
        .map(PlayerEntity::getGameProfile)
        .map(GameProfile::getName)
        .toList());
      String nearest = nearestPlayers.isBlank() ? "none" : nearestPlayers;
      String playerName = visiblePlayers.isEmpty() ? "Unknown" : visiblePlayers.getFirst().getGameProfile().getName();
      content = content
        .replace("%nearest%", nearest)
        .replace("%player%", playerName);
      UUID playerUUID = visiblePlayers.isEmpty() ? null : visiblePlayers.getFirst().getGameProfile().getId();
      message.sendMessage(playerUUID, content, UltraNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.SPAWN, List.of(pokemon), null, pokemonEntity);
    // Save to database
    if (UltraNotify.databaseClient == null) return notified;
    if (notified) {
      if (pokemon.getLevel() > Cobblemon.INSTANCE.getConfig().getMaxPokemonLevel()) return notified;
      UltraNotify.runAsync(() -> UltraNotify.databaseClient.addSpawnedPokemon(new HistorySpawn(pokemonEntity,
        players, this))
      );
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
    if (!isValid(pokemon1, null) && !isValid(pokemon2, null)) return false;
    boolean notified = false;
    // Send Message notificationç
    if (notificationOptions.isTrade()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getTradeMessage();
      var content = message.getRawMessage();
      content = PokemonUtils.replace(content, List.of(pokemon1, pokemon2));
      content = content.replace("%player1%", player1.getGameProfile().getName());
      content = content.replace("%player2%", player2.getGameProfile().getName());
      message.sendMessage((UUID) null, content, UltraNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.TRADE, List.of(pokemon1, pokemon2), null, null);
    // Save to database
    if (notified) {
      if (UltraNotify.databaseClient == null) return notified;
      HistoryTrade historyTrade = new HistoryTrade(pokemon1, pokemon2, player1, player2, this);
      UltraNotify.runAsync(() -> UltraNotify.databaseClient.addTradeHistory(historyTrade));
    }
    return notified;
  }

  public static String replaceVariables(PokemonEntity pokemonEntity, Pokemon pokemon, String message) {
    if (pokemonEntity != null) {
      var x = (int) pokemonEntity.getX();
      var y = (int) pokemonEntity.getY();
      var z = (int) pokemonEntity.getZ();
      var world = pokemonEntity.getEntityWorld();
      message = message.replace("%x%", String.valueOf(x));
      message = message.replace("%y%", String.valueOf(y));
      message = message.replace("%z%", String.valueOf(z));
      message = message.replace("%world%", MinecraftUtils.getWorldTranslate(world));

      var biome = world.getBiome(pokemonEntity.getBlockPos());
      String biomeId = biome.getIdAsString();
      message = message.replace("%biome%", CobbleUtils.language.getBiomes().getOrDefault(biomeId, biomeId));
    }

    message = message.replace("%server%", CobbleUtils.config.getServer());


    // Pokemon variables
    Nature nature = pokemon.getNature();
    message = message
      .replace("%pokemon%", pokemon.showdownId())
      .replace("%types%", getTypes(pokemon))
      .replace("%ability%", pokemon.getAbility().getName())
      .replace("%up%", nature.getIncreasedStat() == null ? "" : nature.getIncreasedStat().getShowdownId())
      .replace("%down%", nature.getDecreasedStat() == null ? "" : nature.getDecreasedStat().getShowdownId())
      .replace("%nature%", pokemon.getNature().getDisplayName())
      .replace("%move1%", getMove(0, pokemon))
      .replace("%move2%", getMove(1, pokemon))
      .replace("%move3%", getMove(2, pokemon))
      .replace("%move4%", getMove(3, pokemon));
    return message;
  }

  private static String getTypes(Pokemon pokemon) {
    Iterable<ElementalType> types = pokemon.getForm().getTypes();
    Iterator<ElementalType> iterator = types.iterator();
    StringBuilder typesString = new StringBuilder();
    while (iterator.hasNext()) {
      typesString.append(iterator.next().getName());
      if (iterator.hasNext()) {
        typesString.append(" / ");
      }
    }
    return typesString.toString();
  }

  private static String getMove(int index, Pokemon pokemon) {
    try {
      Move move = pokemon.getMoveSet().get(index);
      return move != null ? move.getName() : "None";
    } catch (IndexOutOfBoundsException ignored) {
      return "None";
    }
  }
}
