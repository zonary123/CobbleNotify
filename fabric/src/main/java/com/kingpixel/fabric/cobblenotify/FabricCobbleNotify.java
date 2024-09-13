package com.kingpixel.fabric.cobblenotify;

import com.kingpixel.cobblenotify.CobbleNotify;
import net.fabricmc.api.ModInitializer;


public class FabricCobbleNotify implements ModInitializer {

  @Override
  public void onInitialize() {
    CobbleNotify.init();
  }
}