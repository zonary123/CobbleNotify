package com.kingpixel.cobblenotify.Model;

import club.minnced.discord.webhook.send.WebhookMessage;
import com.cobblemon.mod.common.api.pokemon.labels.CobblemonPokemonLabels;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.Model.CobbleUtilsTags;
import com.kingpixel.cobbleutils.Model.Particle;
import com.kingpixel.cobbleutils.Model.Sound;
import com.kingpixel.cobbleutils.Model.discord.WebHookStruct;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.cobbleutils.util.TypeMessage;
import lombok.Getter;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * @author Carlos Varas Alonso - 07/12/2024 22:44
 */
@Getter
public class Notification {
  private boolean WebHook;
  private boolean WebHookSpawn;
  private boolean WebHookCatch;
  private boolean WebHookTrade;
  private boolean WebHookDefeat;
  private boolean notifyNearby;
  private boolean traded;
  private String messageTrade;
  private boolean defeated;
  private String messageDefeat;
  private boolean catched;
  private String messageCatch;
  private boolean spawned;
  private String messageSpawn;
  private List<String> labels;
  private List<String> persistentData;
  private List<String> pokemons;
  private List<String> forms;
  private Sound sound;
  private Particle particle;

  public Notification(List<String> labels, List<String> persistentData) {
    this.WebHook = false;
    this.WebHookSpawn = true;
    this.WebHookCatch = true;
    this.WebHookTrade = true;
    this.WebHookDefeat = true;
    this.notifyNearby = true;
    this.traded = true;
    this.messageTrade = "%prefix% <#d88939>%player1%  <#80cd40>and <#d88939>%player2% <#80cd40>have traded " +
      "<#d88939>%pokemon1% %shiny1% <#80cd40>and " +
      "<#d88939>%pokemon2% %shiny2%!";
    this.defeated = true;
    this.messageDefeat = "%prefix% <#d88939>%player% <#80cd40>has defeated <#e77972>%pokemon% %shiny%!";
    this.catched = true;
    this.messageCatch = "%prefix% <#d88939>%player% <#80cd40>has caught <#e77972>%pokemon% %shiny%!";
    this.spawned = true;
    this.messageSpawn = "%prefix% <#e77972>%pokemon% %shiny% <#80cd40>has spawned in <#e77161>%x% %y% %z% " +
      "<#72e792>%biome%!\n Nearest players: %nearest%";
    this.labels = labels;
    this.persistentData = persistentData;
    this.pokemons = List.of("");
    this.forms = List.of("shiny");
    this.sound = new Sound();
    this.particle = new Particle();
  }

  public static List<Notification> getNotifications() {
    return List.of(
      new Notification(
        List.of(),
        List.of("shiny")
      ),
      new Notification(
        List.of(CobblemonPokemonLabels.LEGENDARY,
          CobblemonPokemonLabels.MYTHICAL,
          CobblemonPokemonLabels.ULTRA_BEAST
        ),
        null
      ),
      new Notification(
        List.of(),
        List.of(CobbleUtilsTags.BOSS_TAG)
      )
    );
  }


  public enum EventType {
    TRADE,
    DEFEAT,
    CATCH,
    SPAWN
  }


  private static boolean PokemonNotify(List<Pokemon> pokemons, Notification notification) {
    for (Pokemon pokemon : pokemons) {
      if (notification.getPokemons() != null) {
        if (notification.getPokemons().contains(pokemon.showdownId()) || notification.getPokemons().contains("*"))
          return true;
      }
      if (notification.getForms() != null) {
        if (notification.getForms().contains(pokemon.getForm().formOnlyShowdownId()) || notification.getForms().contains("*")
          || pokemon.getAspects().stream().anyMatch(aspect -> notification.getForms().contains(aspect)))
          return true;
      }
      if (notification.getPersistentData() != null) {
        for (String data : notification.getPersistentData()) {
          if (pokemon.getPersistentData().contains(data)) return true;
        }
      }
      if (notification.getLabels() != null) {
        return notification.getLabels().contains("*") || pokemon.getForm().getLabels().stream().anyMatch(notification.getLabels()::contains);
      }
    }
    return false;
  }

  private static Notification searchNotification(List<Notification> notifications, List<Pokemon> pokemons,
                                                 EventType eventType) {
    for (Pokemon pokemon : pokemons) {
      if (eventType == EventType.SPAWN || eventType == EventType.DEFEAT) {
        if (pokemon.isPlayerOwned()) return null;
        if (pokemon.isNPCOwned()) return null;
        if (pokemon.getOwnerUUID() != null) return null;
        if (pokemon.getOwnerEntity() != null) return null;
        if (!pokemon.isWild()) return null;
      }

      if (eventType != EventType.CATCH) {
        if (!pokemon.isWild()) return null;
      }
      NbtCompound nbtCompound = pokemon.getPersistentData();
      if (CobbleNotify.config.getBanPersistentData().stream().anyMatch(nbtCompound::contains))
        return null;
      PokemonEntity pokemonEntity = pokemon.getEntity();
      if (pokemonEntity != null) {
        if (pokemonEntity.isPersistent()) return null;
      }
    }
    boolean notify;
    for (Notification notification : notifications) {
      notify = PokemonNotify(pokemons, notification);
      if (notify) return notification;
    }
    return null;
  }

  public static Notification handleEvent(List<Pokemon> pokemons, List<ServerPlayerEntity> players, EventType eventType,
                                         InfoSpawn info, List<PokemonEntity> pokemonEntitys) {
    try {
      if (pokemons == null || pokemons.isEmpty()) return null;
      for (Pokemon pokemon : pokemons)
        if (CobbleNotify.config.getBlackListPokemons().contains(pokemon.showdownId())) return null;

      Notification notification = searchNotification(CobbleNotify.config.getNotifications(), pokemons, eventType);
      String message;
      WebHookStruct webHookStruct = null;
      if (notification != null) {
        switch (eventType) {
          case TRADE:
            if (!notification.isTraded()) return null;
            message = replacePlayers(players, PokemonUtils.replace(notification.getMessageTrade(), pokemons));
            PlayerUtils.broadcast(
              message,
              CobbleNotify.language.getPrefix()
            );
            if (notification.WebHookTrade) {
              webHookStruct = CobbleNotify.language.getMessageWebHookTrade();
            }
            break;
          case DEFEAT:
            if (!notification.isDefeated()) return null;
            if (players == null || players.isEmpty()) return null;
            int size = players.size();
            if (size == 1) {
              PlayerUtils.sendMessage(
                players.getFirst(),
                replacePlayers(players, PokemonUtils.replace(notification.getMessageDefeat(), pokemons)),
                CobbleNotify.language.getPrefix()
              );
            }
            if (notification.WebHookDefeat) {
              webHookStruct = CobbleNotify.language.getMessageWebHookDefeat();
            }
            break;
          case CATCH:
            if (!notification.isCatched()) return null;
            CobbleUtils.server.getPlayerManager().getPlayerList().forEach(player -> {
              PlayerUtils.sendMessage(
                player,
                replacePlayers(players, PokemonUtils.replace(notification.getMessageCatch(), pokemons)),
                CobbleNotify.language.getPrefix()
              );
            });
            if (notification.WebHookCatch) {
              webHookStruct = CobbleNotify.language.getMessageWebHookCatch();
            }
            break;
          case SPAWN:
            if (!notification.isSpawned()) return null;
            message = PokemonUtils.replace(replacePlayers(players, replaceInfo(info, notification.getMessageSpawn())), pokemons);
            if (notification.isNotifyNearby()) {
              for (ServerPlayerEntity player : players) {
                PlayerUtils.sendMessage(
                  player,
                  message,
                  CobbleNotify.language.getPrefix(),
                  TypeMessage.CHAT
                );
              }
            } else {
              PlayerUtils.broadcast(
                message,
                CobbleNotify.language.getPrefix()
              );
            }
            if (notification.isWebHookSpawn()) {
              webHookStruct = CobbleNotify.language.getMessageWebHookSpawn();
            }
            break;
        }
        if (notification.isWebHook() && CobbleNotify.config.getWebHookData().isENABLED()) {
          if (players == null) return notification;
          if (players.isEmpty()) return notification;
          if (pokemons == null) return notification;
          if (pokemons.isEmpty()) return notification;
          for (PokemonEntity pokemon : pokemonEntitys) {
            if (pokemon == null) return notification;
          }
          //ServerPlayerEntity player = players.getFirst();
          if (webHookStruct != null) {
            WebhookMessage webhookMessage = webHookStruct.getMessageEntity(
              CobbleNotify.config.getWebHookData(),
              players,
              pokemonEntitys
            );
            CobbleNotify.webhookClient.send(webhookMessage);
          }
        }
        return notification;
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  private static String replacePlayers(List<ServerPlayerEntity> players, String message) {
    if (players == null || players.isEmpty()) {
      message = message.replace("%player%", "");
      for (int i = 0; i < 3; i++) {
        message = message.replace("%player" + i + "%", "");
      }
      return message;
    }
    int size = players.size();
    if (size == 1) {
      message = message
        .replace("%player%", players.getFirst().getGameProfile().getName());
    } else {
      for (int i = 0; i < size; i++) {
        message = message
          .replace("%player" + (i + 1) + "%", players.get(i).getGameProfile().getName());
      }
    }
    if (!players.isEmpty()) {
      String[] nearest = new String[players.size()];
      int s = players.size();
      for (int i = 0; i < s; i++) {
        nearest[i] = players.get(i).getGameProfile().getName();
      }
      message = message.replace("%nearest%", String.join(", ", nearest));
    }

    return message;
  }

  private static String replaceInfo(InfoSpawn infoSpawn, String message) {
    if (infoSpawn == null) return message;
    return message
      .replace("%biome%", infoSpawn.getBiome())
      .replace("%world%", infoSpawn.getWorld())
      .replace("%x%", String.valueOf(infoSpawn.getX()))
      .replace("%y%", String.valueOf(infoSpawn.getY()))
      .replace("%z%", String.valueOf(infoSpawn.getZ()));
  }
}
