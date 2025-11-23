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
    
  }
}
