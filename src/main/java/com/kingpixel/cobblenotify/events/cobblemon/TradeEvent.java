package com.kingpixel.cobblenotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.models.Notification;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:38
 */
public class TradeEvent {
  public static void register() {
    CobblemonEvents.TRADE_EVENT_POST.subscribe(Priority.NORMAL, evt -> {
      try {
        Pokemon pokemon1 = evt.getTradeParticipant1Pokemon();
        Pokemon pokemon2 = evt.getTradeParticipant2Pokemon();
        UUID player1 = evt.getTradeParticipant1().getUuid();
        UUID player2 = evt.getTradeParticipant2().getUuid();
        for (Notification notification : Notifications.NOTIFICATIONS) {
          if (notification.computeTrade(pokemon1, pokemon2, getPlayerByUUID(player1), getPlayerByUUID(player2))) return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }

  private static ServerPlayerEntity getPlayerByUUID(UUID uuid) {
    return CobbleNotify.server.getPlayerManager().getPlayer(uuid);
  }
}
