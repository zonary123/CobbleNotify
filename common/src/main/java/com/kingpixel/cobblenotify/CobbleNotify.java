package com.kingpixel.cobblenotify;

import com.kingpixel.cobblenotify.Config.Config;
import com.kingpixel.cobblenotify.Config.Lang;
import com.kingpixel.cobblenotify.Config.SpawnNotifyConfig;
import com.kingpixel.cobblenotify.command.CommandTree;
import com.kingpixel.cobblenotify.events.CatchEvent;
import com.kingpixel.cobblenotify.events.DefeatedEvent;
import com.kingpixel.cobblenotify.events.SpawnPokemonEvent;
import com.kingpixel.cobblenotify.events.TradeEvent;
import com.kingpixel.cobblenotify.permissions.SpawnNotifyPermissions;
import com.kingpixel.cobblenotify.utils.UtilsLogger;
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
  public static final UtilsLogger LOGGER = new UtilsLogger();
  public static MinecraftServer server;
  public static Lang language = new Lang();
  public static Config config = new Config();
  public static SpawnNotifyConfig dexpermission = new SpawnNotifyConfig();
  public static SpawnNotifyPermissions permissions = new SpawnNotifyPermissions();

  public static void init() {
    events();
  }

  public static void load() {
    files();
    print();
  }

  private static void files() {
    language.init();
    config.init();
  }

  private static void print() {
    LOGGER.info("CobbleNotify loaded");
    LOGGER.info("+--------------------------------+");
    LOGGER.info("|  " + MOD_NAME + " v1.0.3 loaded  |");
    LOGGER.info("| Author: zonary123");
    LOGGER.info("| Discord: zonary123");
    LOGGER.info("| Discord Server: https://discord.gg/ZK2g6uw7SD");
    LOGGER.info("| Github: zonary123");
    LOGGER.info("| Ko-fi: https://ko-fi.com/zonary123");
    LOGGER.info("+--------------------------------+");
  }

  private static void events() {
    files();

    CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> CommandTree.register(dispatcher));

    LifecycleEvent.SERVER_STARTED.register(server -> load());

    LifecycleEvent.SERVER_LEVEL_LOAD.register(level -> server = level.getLevel().getServer());


    SpawnPokemonEvent.registerEvents();
    CatchEvent.registerEvents();
    TradeEvent.registerEvents();
    DefeatedEvent.registerEvents();
  }

}
