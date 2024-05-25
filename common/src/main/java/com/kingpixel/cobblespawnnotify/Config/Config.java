package com.kingpixel.cobblespawnnotify.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblespawnnotify.SpawnNotify;
import com.kingpixel.cobblespawnnotify.utils.Utils;

import java.util.concurrent.CompletableFuture;


/**
 * @author Carlos Varas Alonso - 29/04/2024 0:14
 */
public class Config {
  private String lang;
  private boolean shiny;
  private boolean legendary;
  private boolean notifyspawn;
  private boolean notifycatch;
  private boolean notifydefeat;
  private int distanceplayer;

  public Config() {
    lang = "en";
    shiny = true;
    legendary = true;
    notifyspawn = true;
    notifycatch = true;
    notifydefeat = true;
    distanceplayer = 100;
  }

  public boolean isNotifyspawn() {
    return notifyspawn;
  }

  public boolean isNotifycatch() {
    return notifycatch;
  }

  public boolean isNotifydefeat() {
    return notifydefeat;
  }

  public String getLang() {
    return lang;
  }

  public boolean isShiny() {
    return shiny;
  }

  public boolean isLegendary() {
    return legendary;
  }

  public int getDistanceplayer() {
    return distanceplayer;
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(SpawnNotify.path, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        Config config = gson.fromJson(el, Config.class);
        lang = config.getLang();
        shiny = config.isShiny();
        legendary = config.isLegendary();
        notifyspawn = config.isNotifyspawn();
        notifycatch = config.isNotifycatch();
        notifydefeat = config.isNotifydefeat();
        distanceplayer = config.getDistanceplayer();
        String data = gson.toJson(this);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(SpawnNotify.path, "config.json",
          data);
        if (!futureWrite.join()) {
          SpawnNotify.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
        }
      });

    if (!futureRead.join()) {
      SpawnNotify.LOGGER.info("No config.json file found for" + SpawnNotify.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(SpawnNotify.path, "config.json",
        data);

      if (!futureWrite.join()) {
        SpawnNotify.LOGGER.fatal("Could not write config.json file for CobbleHunt.");
      }
    }

  }
}
