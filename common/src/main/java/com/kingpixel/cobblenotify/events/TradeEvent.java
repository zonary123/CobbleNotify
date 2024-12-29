package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.Model.Notification;
import kotlin.Unit;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.List;

/**
 * @author Carlos Varas Alonso - 26/05/2024 2:46
 */
public class TradeEvent {
  public static void registerEvents() {
    CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.NORMAL, (evt) -> {
      try {
        List<ServerPlayerEntity> players = List.of(evt.getTradeParticipant1Pokemon().getOwnerPlayer(), evt.getTradeParticipant2Pokemon().getOwnerPlayer());
        List<Pokemon> pokemons = List.of(evt.getTradeParticipant1Pokemon(), evt.getTradeParticipant2Pokemon());
        Notification.handleEvent(pokemons, players, Notification.EventType.TRADE, null);
      } catch (Exception e) {
        e.printStackTrace();
      }
      return Unit.INSTANCE;
    });
  }

}
