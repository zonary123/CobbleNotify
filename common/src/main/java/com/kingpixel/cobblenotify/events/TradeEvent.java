package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.Model.Notification;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * @author Carlos Varas Alonso - 26/05/2024 2:46
 */
public class TradeEvent {
  public static void registerEvents() {
    CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.LOW, (evt) -> {
      try {
        var player1 = CobbleNotify.server.getPlayerManager().getPlayer(evt.getTradeParticipant1().getUuid());
        var player2 = CobbleNotify.server.getPlayerManager().getPlayer(evt.getTradeParticipant2().getUuid());
        if (player1 == null || player2 == null) {
          CobbleNotify.LOGGER.error("Player not found in the TradeEvent");
          return Unit.INSTANCE;
        }
        List<ServerPlayerEntity> players = List.of(player1, player2);
        List<Pokemon> pokemons = List.of(evt.getTradeParticipant1Pokemon(), evt.getTradeParticipant2Pokemon());
        Notification.handleEvent(pokemons, players, Notification.EventType.TRADE, null, null);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return Unit.INSTANCE;
    });
  }

}
