package com.kingpixel.ultranotify.config;

import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.cobbleutils.Model.DataBaseType;
import com.kingpixel.cobbleutils.Model.PokemonBlackList;
import com.kingpixel.cobbleutils.Model.WebHookData;
import com.kingpixel.cobbleutils.util.Utils;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.models.WorldFilter;
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
  private PokemonBlackList globalBlackList = PokemonBlackList.createBlackList();

  public Config() {
    if (database == null) {
      database = new DataBaseConfig();
      database.setType(DataBaseType.JSON);
      database.setUrl("mongodb://localhost:27017");
      database.setDatabase("ultranotify");
    }
    if (globalBlackList == null) {
      globalBlackList = PokemonBlackList.createBlackList();
    }
  }

  public void init() {
    var futureRead = Utils.readFileAsync(UltraNotify.PATH, "config.json", data -> {
      try {
        var config = Utils.newGson().fromJson(data, Config.class);
        config.check();
        UltraNotify.config = config;
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    futureRead.join();
    Utils.writeFileAsync(UltraNotify.PATH, "config.json", Utils.newGson().toJson(UltraNotify.config));

  }

  private void check() {
    if (worldFilter == null) {
      worldFilter = new WorldFilter();
    }
    if (database == null) {
      database = new DataBaseConfig();
      database.setType(DataBaseType.JSON);
      database.setUrl("mongodb://localhost:27017");
      database.setDatabase("ultranotify");
    }
    if (globalBlackList == null) {
      globalBlackList = new PokemonBlackList();
      globalBlackList.getLabels().clear();
      globalBlackList.getPokemons().clear();
      globalBlackList.getAspects().add("plushie");
    }
  }
}
