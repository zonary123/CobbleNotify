package com.kingpixel.cobblenotify.events;

import com.kingpixel.cobblenotify.events.cobblemon.CatchEvent;
import com.kingpixel.cobblenotify.events.cobblemon.DefeatEvent;
import com.kingpixel.cobblenotify.events.cobblemon.SpawnEvent;
import com.kingpixel.cobblenotify.events.cobblemon.TradeEvent;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:38
 */
public class Events {
  public static void register() {
    TradeEvent.register();
    CatchEvent.register();
    SpawnEvent.register();
    DefeatEvent.register();

    CobblemonEvents.BATTLE_FAINTED.subscribe(Priority.LOW, evt -> {
      sendLog("[BATTLE_FAINTED] Pokémon fainted: " + evt.getKilled().getName().getString(), evt.getBattle());
    });

    CobblemonEvents.BATTLE_FLED.subscribe(Priority.LOW, evt -> {
      sendLog("[BATTLE_FLED] Player fled from battle", evt.getBattle());
    });

    CobblemonEvents.BATTLE_VICTORY.subscribe(Priority.LOW, evt -> {
      sendLog("[BATTLE_VICTORY] Battle won!", evt.getBattle());
    });

    CobblemonEvents.BATTLE_STARTED_PRE.subscribe(Priority.LOW, evt -> {
      sendLog("[BATTLE_STARTED_PRE] Battle started!", evt.getBattle());
    });
  }
}
