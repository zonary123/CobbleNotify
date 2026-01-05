package com.kingpixel.cobblenotify;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kingpixel.cobblenotify.command.CommandTree;
import com.kingpixel.cobblenotify.config.Config;
import com.kingpixel.cobblenotify.config.Lang;
import com.kingpixel.cobblenotify.config.Notifications;
import com.kingpixel.cobblenotify.database.DataBaseClient;
import com.kingpixel.cobblenotify.database.DataBaseFactory;
import com.kingpixel.cobblenotify.events.Events;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import net.fabricmc.api.ModInitializer;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:50
 */
public class CobbleNotify implements ModInitializer {
  public static final String MOD_ID = "cobblenotify";
  public static final String MOD_NAME = "CobbleNotify";
  public static final String PATH = "/config/cobblenotify/";
  public static MinecraftServer server;
  public static Lang lang = new Lang();
  public static Config config = new Config();
  public static DataBaseClient databaseClient;
  private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(2, new ThreadFactoryBuilder()
    .setDaemon(true)
    .setNameFormat("CobbleNotify-Executor-%d")
    .build()
  );

  @Override public void onInitialize() {
    events();
  }

  public static void load() {
    files();
  }

  private static void files() {
    config.init();
    lang.init();
    Notifications.init();
    databaseClient = DataBaseFactory.createDataBaseClient(config.getDatabase());
  }


  private static void events() {
    files();

    CommandRegistrationEvent.EVENT.register((dispatcher, registry, selection) -> CommandTree.register(dispatcher));

    LifecycleEvent.SERVER_STARTED.register(server -> load());

    LifecycleEvent.SERVER_LEVEL_LOAD.register(level -> server = level.getServer());

    Events.register();
  }

  public static void runAsync(Runnable task) {
    if (EXECUTOR_SERVICE.isShutdown() || EXECUTOR_SERVICE.isTerminated()) {
      task.run();
      return;
    }
    CompletableFuture.runAsync(task, EXECUTOR_SERVICE)
      .orTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
      .exceptionally(ex -> {
        ex.printStackTrace();
        return null;
      });
  }

}
