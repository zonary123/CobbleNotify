package com.kingpixel.fabric.cobblespawnnotify;

import com.kingpixel.cobblespawnnotify.SpawnNotify;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerWorldEvents;


public class FabricModExample implements ModInitializer {

  @Override
  public void onInitialize() {
    SpawnNotify.init();
    ServerLifecycleEvents.SERVER_STARTED.register(t -> SpawnNotify.load());
    ServerWorldEvents.LOAD.register((t, e) -> SpawnNotify.server = t);
  }
}