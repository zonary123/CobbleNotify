package com.kingpixel.cobblenotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.models.Notification;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

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
        ServerPlayerEntity player1 = getPlayerByUUID(evt.getTradeParticipant1().getUuid());
        ServerPlayerEntity player2 = getPlayerByUUID(evt.getTradeParticipant2().getUuid());
        ServerWorld world = player1.getServerWorld();
        if (CobbleNotify.config.getWorldFilter().isBlackListed(world)) return;
        for (Notification notification : Notifications.NOTIFICATIONS) {
          if (notification.getWorldFilter().isBlackListed(world)) continue;
          if (notification.computeTrade(pokemon1, pokemon2, player1, player2)) return;
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
