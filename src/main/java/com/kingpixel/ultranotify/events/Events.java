package com.kingpixel.ultranotify.events;

import com.kingpixel.ultranotify.events.cobblemon.CatchEvent;
import com.kingpixel.ultranotify.events.cobblemon.DefeatEvent;
import com.kingpixel.ultranotify.events.cobblemon.SpawnEvent;
import com.kingpixel.ultranotify.events.cobblemon.TradeEvent;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:38
 */
public class Events {
  public static void register() {
    TradeEvent.register();
    CatchEvent.register();
    SpawnEvent.register();
    DefeatEvent.register();

  }
}
