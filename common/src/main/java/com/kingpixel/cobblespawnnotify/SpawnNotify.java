package com.kingpixel.cobblespawnnotify;

import com.kingpixel.cobblespawnnotify.Config.Config;
import com.kingpixel.cobblespawnnotify.Config.Lang;
import com.kingpixel.cobblespawnnotify.Config.SpawnNotifyConfig;
import com.kingpixel.cobblespawnnotify.events.CatchEvent;
import com.kingpixel.cobblespawnnotify.events.DefeatedCatch;
import com.kingpixel.cobblespawnnotify.events.SpawnPokemonEvent;
import com.kingpixel.cobblespawnnotify.permissions.SpawnNotifyPermissions;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:50
 */
public class SpawnNotify {
  public static final String MOD_ID = "cobblespawnnotify";
  public static final Logger LOGGER = LogManager.getLogger();
  public static final String MOD_NAME = "CobbleSpawnNotify";
  public static final String path = "/config/cobblespawnnotify/";
  public static Lang language = new Lang();
  public static MinecraftServer server;
  public static Config config = new Config();
  public static SpawnNotifyConfig dexpermission = new SpawnNotifyConfig();
  public static SpawnNotifyPermissions permissions = new SpawnNotifyPermissions();

  public static void init() {
    SpawnPokemonEvent.registerEvents();
    CatchEvent.registerEvents();
    DefeatedCatch.registerEvents();
  }

  public static void load() {
    language.init();
    config.init();
  }

}
