package com.kingpixel.forge.cobblenotify;

import com.kingpixel.cobblenotify.CobbleNotify;
import net.minecraftforge.fml.common.Mod;

@Mod(CobbleNotify.MOD_ID)
public class ForgeCobbleNotify {

  public ForgeCobbleNotify() {
    CobbleNotify.init();
  }
}
