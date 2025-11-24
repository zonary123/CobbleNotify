package com.kingpixel.cobblenotify;

import com.kingpixel.cobblenotify.command.CommandTree;
import com.kingpixel.cobblenotify.config.Config;
import com.kingpixel.cobblenotify.config.Lang;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.events.Events;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.minecraft.server.MinecraftServer;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:50
 */
public class CobbleNotify {
  public static final String MOD_ID = "cobblenotify";
  public static final String MOD_NAME = "CobbleNotify";
  public static final String PATH = "/config/cobblenotify/";
  public static MinecraftServer server;
  public static Lang lang = new Lang();
  public static Config config = new Config();

  public static void init() {
    events();
  }

  public static void load() {
    files();
  }

  private static void files() {
    config.init();
    lang.init();
    Notifications.init();
  }


  private static void events() {
    files();

    CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> CommandTree.register(dispatcher));

    LifecycleEvent.SERVER_STARTED.register(server -> load());

    LifecycleEvent.SERVER_LEVEL_LOAD.register(level -> server = level.getServer());

    Events.register();
  }

}
