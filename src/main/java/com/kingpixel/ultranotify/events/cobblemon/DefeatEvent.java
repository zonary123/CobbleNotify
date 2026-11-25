package com.kingpixel.ultranotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.battles.model.actor.ActorType;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.api.events.battles.BattleFaintedEvent;
import com.cobblemon.mod.common.battles.actor.PlayerBattleActor;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.config.Notifications;
import com.kingpixel.ultranotify.models.Notification;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:39
 */
public class DefeatEvent {
  private DefeatEvent() {}

  public static void register() {
    CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, DefeatEvent::handleBattleFainted);
  }

  private static void handleBattleFainted(BattleFaintedEvent evt) {
    try {
      var battle = evt.getBattle();
      if (battle == null || !battle.isPvW()) return;

      var killedBattlePokemon = evt.getKilled();
      if (killedBattlePokemon == null) return;

      var killedActor = killedBattlePokemon.getActor();
      if (killedActor == null || killedActor.getType() != ActorType.WILD) return;

      ServerPlayerEntity killer = getKiller(evt);
      if (killer == null) return;
      if (UltraNotify.config.getWorldFilter().isBlackListed(killer.getServerWorld())) return;

      var killed = killedBattlePokemon.getOriginalPokemon();
      if (killed == null) return;
      var entity = killedBattlePokemon.getEntity();

      for (Notification notification : Notifications.NOTIFICATIONS) {
        if (notification.getWorldFilter().isBlackListed(killer.getServerWorld())) continue;
        if (notification.computeDefeat(killed, killer, entity)) return;
      }
    } catch (Exception e) {
      UltraNotify.LOGGER.error("Error handling battle fainted event: {}", e.getMessage(), e);
    }
  }

  private static ServerPlayerEntity getKiller(BattleFaintedEvent evt) {
    var context = evt.getContext();
    if (context != null && context.getOrigin() != null) {
      var originActor = context.getOrigin().getActor();
      if (originActor instanceof PlayerBattleActor playerActor) {
        return playerActor.getEntity();
      }
    }
    var battle = evt.getBattle();
    if (battle == null) return null;
    var players = battle.getPlayers();
    return (players == null || players.isEmpty()) ? null : players.getFirst();
  }
}

