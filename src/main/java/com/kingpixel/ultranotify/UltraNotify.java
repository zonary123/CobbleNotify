package com.kingpixel.ultranotify;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.kingpixel.ultranotify.command.CommandTree;
import com.kingpixel.ultranotify.config.Config;
import com.kingpixel.ultranotify.config.Lang;
import com.kingpixel.ultranotify.config.Notifications;
import com.kingpixel.ultranotify.database.DataBaseClient;
import com.kingpixel.ultranotify.database.DataBaseFactory;
import com.kingpixel.ultranotify.events.Events;
import com.kingpixel.cobbleutils.util.async.AsyncContext;
import java.util.concurrent.TimeUnit;
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
public class UltraNotify implements ModInitializer {
  public static final String MOD_ID = "ultranotify";
  public static final String MOD_NAME = "UltraNotify";
  public static final String PATH = "/config/ultranotify/";
  public static MinecraftServer server;
  public static Lang lang = new Lang();
  public static Config config = new Config();
  public static DataBaseClient databaseClient;
  private static final AsyncContext asyncContext = new AsyncContext(MOD_NAME, 2, 6, 500, 30, TimeUnit.SECONDS);

  public static AsyncContext getAsyncContext() {
    return asyncContext;
  }

  private static final ExecutorService EXECUTOR_SERVICE = asyncContext.getExecutor();

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
