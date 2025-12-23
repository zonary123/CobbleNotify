package com.kingpixel.cobblenotify.config;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.models.WorldFilter;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.cobbleutils.Model.DataBaseType;
import com.kingpixel.cobbleutils.Model.PokemonBlackList;
import com.kingpixel.cobbleutils.Model.WebHookData;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:46
 */
@Data
public class Config {
  private boolean debug = false;
  private boolean affectCommand = true;
  private String lang = "en_us";
  private WorldFilter worldFilter = new WorldFilter();
  private DataBaseConfig database;
  private WebHookData webHookData = new WebHookData("", "", "");
  private PokemonBlackList globalBlackList = new PokemonBlackList();

  public Config() {
    if (database == null) {
      database = new DataBaseConfig();
      database.setType(DataBaseType.JSON);
      database.setUrl("mongodb://localhost:27017");
      database.setDatabase("cobblenotify");
    }
    if (globalBlackList == null) {
      globalBlackList = new PokemonBlackList();
      globalBlackList.getLabels().clear();
      globalBlackList.getPokemons().clear();
      globalBlackList.getAspects().add("plushie");
    }
  }

  public void init() {
    var futureRead = Utils.readFileAsync(CobbleNotify.PATH, "config.json", data -> {
      try {
        var config = Utils.newGson().fromJson(data, Config.class);
        config.check();
        CobbleNotify.config = config;
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    futureRead.join();
    Utils.writeFileAsync(CobbleNotify.PATH, "config.json", Utils.newGson().toJson(CobbleNotify.config));

  }

  private void check() {
  }
}
