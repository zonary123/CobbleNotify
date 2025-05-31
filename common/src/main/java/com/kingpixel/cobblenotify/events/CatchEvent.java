package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.kingpixel.cobblenotify.Model.Notification;
import kotlin.Unit;

import java.util.List;

/**
 * @author Carlos Varas Alonso - 25/05/2024 21:48
 */
public class CatchEvent {
  public static void registerEvents() {
    CobblemonEvents.POKEMON_CAPTURED.subscribe(Priority.LOW, (evt) -> {
      Notification.handleEvent(List.of(evt.getPokemon()), List.of(evt.getPlayer()), Notification.EventType.CATCH,
        null, null);
      return Unit.INSTANCE;
    });
  }
}

