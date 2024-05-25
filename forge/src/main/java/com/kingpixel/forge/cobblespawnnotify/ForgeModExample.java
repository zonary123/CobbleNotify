package com.kingpixel.forge.cobblespawnnotify;

import com.kingpixel.cobblespawnnotify.SpawnNotify;
import com.kingpixel.cobblespawnnotify.command.CommandTree;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod(SpawnNotify.MOD_ID)
public class ForgeModExample {

  public ForgeModExample() {
    SpawnNotify.init();
    MinecraftForge.EVENT_BUS.register(this);
  }

  @SubscribeEvent
  public void registerCommands(RegisterCommandsEvent event) {
    CommandTree.register(event.getDispatcher());
  }

  @SubscribeEvent
  public void serverStartedEvent(ServerStartedEvent event) {
    SpawnNotify.load();
  }

  @SubscribeEvent
  public void worldLoadEvent(LevelEvent.Load event) {
    SpawnNotify.server = event.getLevel().getServer();
  }
}
