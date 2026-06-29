package com.kingpixel.ultranotify.config;

import com.kingpixel.cobbleutils.util.Utils;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.gui.CaughtGui;
import com.kingpixel.ultranotify.gui.GlobalGui;
import com.kingpixel.ultranotify.gui.SpawnGui;
import com.kingpixel.ultranotify.gui.TradeGui;
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
    File folder = Utils.getAbsolutePath(UltraNotify.PATH + "/lang/");
    if (!folder.exists()) {
      folder.mkdirs();
    }

    var futureRead = Utils.readFileAsync(UltraNotify.PATH + "/lang/", UltraNotify.config.getLang() + ".json",
      data -> {
        try {
          var lang = Utils.newGson().fromJson(data, Lang.class);
          if (lang != null) {
            UltraNotify.lang = lang;
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      });

    futureRead.join();
    Utils.writeFileAsync(UltraNotify.PATH + "/lang/", UltraNotify.config.getLang() + ".json", Utils.newGson().toJson(UltraNotify.lang));

  }
}
