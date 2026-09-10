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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.TypeFilter;
import net.minecraft.util.math.Box;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.Comparator;
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
    if (identifier == null || identifier.isBlank())
      identifier = "notification";
    if (properties == null || properties.isBlank())
      properties = "shiny=true";
    if (icon == null)
      icon = new ItemModel("minecraft:paper", "§e" + identifier);
    if (worldFilter == null)
      worldFilter = new WorldFilter();
    if (filter == null)
      filter = PokemonBlackList.createBlackList();
    if (notificationOptions == null)
      notificationOptions = new NotificationOptions();
    if (notificationOptions.getNotificationMessages() == null)
      notificationOptions.setNotificationMessages(new NotificationMessages());
    if (webHookOptions == null)
      webHookOptions = new WebHookOptions();
    if (webHookOptions.getWebHookMessages() == null)
      webHookOptions.setWebHookMessages(new WebHookMessages());
  }

  private transient PokemonProperties parsedProperties;

  public void setProperties(String properties) {
    this.properties = properties;
    this.parsedProperties = null;
  }

  public boolean isValid(Pokemon pokemon, @Nullable PokemonEntity entity) {
    if (entity != null && (entity.isAiDisabled() || entity.isUncatchable()))
      return false;
    if (UltraNotify.config.getGlobalBlackList().isBlackListed(pokemon))
      return false;
    if (useProperties) {
      if (parsedProperties == null) {
        parsedProperties = PokemonProperties.Companion.parse(properties);
      }
      return parsedProperties.matches(pokemon);
    } else
      return filter.isBlackListed(pokemon);
  }

  public boolean computeCaught(Pokemon pokemon, ServerPlayerEntity player) {
    if (!isValid(pokemon, null))
      return false;
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
              pokemon));
      message.sendMessage((UUID) null, content, UltraNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.CAUGHT, List.of(pokemon), player, null);
    if (UltraNotify.databaseClient == null)
      return notified;
    if (notified)
      UltraNotify.runAsync(() -> {
        var history = UltraNotify.databaseClient.getSpawnedPokemonById(pokemon.getUuid());
        if (history == null)
          return;
        history.caught(player);
        UltraNotify.databaseClient.updateHistorySpawn(history);
      });
    return notified;
  }

  public boolean computeDefeat(Pokemon pokemon, ServerPlayerEntity player, PokemonEntity entity) {
    if (!pokemon.isWild())
      return false;
    if (!isValid(pokemon, entity))
      return false;
    boolean notified = false;
    // Send Message notification
    if (notificationOptions.isDefeat()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getDefeatMessage();
      var content = message.getRawMessage();
      PokemonEntity entityToUse = entity != null ? entity : pokemon.getEntity();
      content = replaceVariables(entityToUse, pokemon, PokemonUtils.replace(content, pokemon))
          .replace("%player%", player.getGameProfile().getName());
      message.sendMessage((UUID) null, content, UltraNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.DEFEAT, List.of(pokemon), player, entity != null ? entity : pokemon.getEntity());
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
    if (!pokemon.isWild() || pokemonEntity.isPersistent())
      return false;
    if (!isValid(pokemon, pokemonEntity))
      return false;

    List<ServerPlayerEntity> visiblePlayers = resolveNearbyPlayers(pokemonEntity);
    ServerPlayerEntity closestPlayer = visiblePlayers.isEmpty() ? null : visiblePlayers.getFirst();
    String playerName = closestPlayer != null ? closestPlayer.getGameProfile().getName() : "Unknown";

    boolean notified = false;
    if (notificationOptions.isSpawn()) {
      var messages = notificationOptions.getNotificationMessages();
      var message = messages.getSpawnMessage();
      var content = message.getRawMessage();
      content = replaceVariables(pokemonEntity, pokemon, PokemonUtils.replace(content, pokemon));
      String nearestPlayers = String.join(", ", visiblePlayers.stream()
          .map(PlayerEntity::getGameProfile)
          .map(GameProfile::getName)
          .toList());
      String nearest = nearestPlayers.isBlank() ? "none" : nearestPlayers;
      content = content
          .replace("%nearest%", nearest)
          .replace("%player%", playerName);
      UUID playerUUID = closestPlayer != null ? closestPlayer.getGameProfile().getId() : null;
      message.sendMessage(playerUUID, content, UltraNotify.lang.getPrefix(), false);
      notified = true;
    }
    // Send WebHook notification
    notified |= webHookOptions.sendMessage(Actions.SPAWN, List.of(pokemon), closestPlayer, pokemonEntity);
    // Save to database
    if (UltraNotify.databaseClient == null)
      return notified;
    if (notified) {
      if (pokemon.getLevel() > Cobblemon.INSTANCE.getConfig().getMaxPokemonLevel())
        return notified;
      List<PlayerEntity> playersForHistory = new ArrayList<>(visiblePlayers);
      UltraNotify.runAsync(() -> UltraNotify.databaseClient.addSpawnedPokemon(new HistorySpawn(pokemonEntity,
          playersForHistory, this)));
    }
    return notified;
  }

  private List<ServerPlayerEntity> resolveNearbyPlayers(PokemonEntity pokemonEntity) {
    List<ServerPlayerEntity> visiblePlayers = new ArrayList<>();
    if (pokemonEntity.getEntityWorld() instanceof ServerWorld serverWorld) {
      addValidPlayers(serverWorld.getPlayers(), pokemonEntity, 64.0 * 64.0, visiblePlayers);
      if (visiblePlayers.isEmpty()) {
        addValidPlayers(serverWorld.getPlayers(), pokemonEntity, 128.0 * 128.0, visiblePlayers);
      }
    }

    if (visiblePlayers.isEmpty()) {
      Box boundingBox = pokemonEntity.getBoundingBox().expand(64);
      var boxPlayers = pokemonEntity.getEntityWorld().getEntitiesByType(TypeFilter.instanceOf(PlayerEntity.class),
          boundingBox, p -> true);
      for (PlayerEntity p : boxPlayers) {
        if (isValidPlayer(p)) {
          visiblePlayers.add((ServerPlayerEntity) p);
        }
      }
    }
    visiblePlayers.sort(Comparator.comparingDouble(p -> p.squaredDistanceTo(pokemonEntity)));
    return visiblePlayers;
  }

  private static void addValidPlayers(List<ServerPlayerEntity> source, PokemonEntity pokemonEntity, double maxDistSq, List<ServerPlayerEntity> target) {
    for (ServerPlayerEntity p : source) {
      if (isValidPlayer(p) && p.squaredDistanceTo(pokemonEntity) <= maxDistSq) {
        target.add(p);
      }
    }
  }

  private static boolean isValidPlayer(PlayerEntity p) {
    return p instanceof ServerPlayerEntity sp && !sp.isSpectator() && !NotificationUtils.playerIsVanish(sp);
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
    if (!isValid(pokemon1, null) && !isValid(pokemon2, null))
      return false;
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
      if (UltraNotify.databaseClient == null)
        return notified;
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
