package com.kingpixel.cobblenotify.events.cobblemon;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.models.Notification;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:39
 */
public class DefeatEvent {
  public static void register() {
    CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOWEST, evt -> {
      try {
        var battle = evt.getBattle();
        if (!battle.isPvW()) return;
        var killed = evt.getKilled().getOriginalPokemon();
        var killer = evt.getBattle().getPlayers().getFirst();
        if (CobbleNotify.config.getWorldFilter().isBlackListed(killer.getServerWorld())) return;
        for (Notification notification : Notifications.NOTIFICATIONS) {
          if (notification.getWorldFilter().isBlackListed(killer.getServerWorld())) continue;
          if (notification.computeDefeat(killed, killer)) return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
  }
}
