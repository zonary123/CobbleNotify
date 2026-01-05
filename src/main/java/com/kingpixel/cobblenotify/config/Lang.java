package com.kingpixel.cobblenotify.config;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.gui.CaughtGui;
import com.kingpixel.cobblenotify.gui.GlobalGui;
import com.kingpixel.cobblenotify.gui.SpawnGui;
import com.kingpixel.cobblenotify.gui.TradeGui;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;

import java.io.File;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:47
 */
@Data
public class Lang {
  private String prefix = "&6[&bCobbleNotify&6]&r";
  private List<String> spawnLore = List.of(
    "%lorepokemon%",
    "&7Spawn Time: %spawnTime%",
    "&7Location: %location%",
    "&7World: %world%",
    "&7Nearby Players: %nearbyPlayers%",
    "&7Caught: %caught%",
    "&7Caught By: %caughtBy%",
    "&7Caught Time: %caughtDate%"
  );
  private List<String> tradeLore = List.of(
    "&7Offered By: %trader%",
    "&7Pokemon Offered: %offeredPokemon%",
    "&7Received By: %receiver%",
    "&7Pokemon Received: %receivedPokemon%",
    "%lorepokemon%"
  );
  private GlobalGui globalGui = new GlobalGui();
  private SpawnGui spawnGui = new SpawnGui();
  private CaughtGui caughtGui = new CaughtGui();
  private TradeGui tradeGui = new TradeGui();

  public void init() {
    File folder = Utils.getAbsolutePath(CobbleNotify.PATH + "/lang/");
    if (!folder.exists()) {
      folder.mkdirs();
    }

    var futureRead = Utils.readFileAsync(CobbleNotify.PATH + "/lang/", CobbleNotify.config.getLang() + ".json",
      data -> {
        try {
          var lang = Utils.newGson().fromJson(data, Lang.class);
          if (lang != null) {
            CobbleNotify.lang = lang;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

    futureRead.join();
    Utils.writeFileAsync(CobbleNotify.PATH + "/lang/", CobbleNotify.config.getLang() + ".json", Utils.newGson().toJson(CobbleNotify.lang));

  }
}
