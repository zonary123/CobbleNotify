package com.kingpixel.cobblenotify.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.Model.Notification;
import com.kingpixel.cobbleutils.Model.WebHookData;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author Carlos Varas Alonso - 29/04/2024 0:14
 */
@Getter
@ToString
public class Config {
  private boolean debug;
  private boolean affectCommands;
  private String lang;
  private WebHookData webHookData;
  private int distance;
  private List<String> blackListPokemons;
  private List<String> banPersistentData;
  private List<Notification> notifications;

  public Config() {
    debug = false;
    affectCommands = true;
    lang = "en";
    webHookData = new WebHookData("", "", "");
    distance = 100;
    blackListPokemons = List.of("phione");
    banPersistentData = List.of("plushieowner", "fakeRaid");
    notifications = Notification.getNotifications();
  }


  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleNotify.PATH, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        CobbleNotify.config = gson.fromJson(el, Config.class);
        String data = gson.toJson(CobbleNotify.config);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH, "config.json",
          data);
        if (!futureWrite.join()) {
          CobbleNotify.LOGGER.fatal("Could not write config.json file for CobbleNotify.");
        }
      });

    if (!futureRead.join()) {
      CobbleNotify.LOGGER.info("No config.json file found for" + CobbleNotify.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH, "config.json",
        data);

      if (!futureWrite.join()) {
        CobbleNotify.LOGGER.fatal("Could not write config.json file for CobbleNotify.");
      }
    }

  }
}
