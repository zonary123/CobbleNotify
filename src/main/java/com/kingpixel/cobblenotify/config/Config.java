package com.kingpixel.cobblenotify.config;

import com.kingpixel.cobblenotify.CobbleNotify;
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
  private WebHookData webHookData = new WebHookData("", "", "");
  private PokemonBlackList globalBlackList = new PokemonBlackList();

  public Config() {

  }

  public void init() {
    var futureRead = Utils.readFileAsync(CobbleNotify.PATH, "config.json", data -> {
      try {
        var config = Utils.newGson().fromJson(data, Config.class);
        if (config != null) CobbleNotify.config = config;
      } catch (Exception e) {
        e.printStackTrace();
      }
    });
    futureRead.join();
    var blacklist = CobbleNotify.config.getGlobalBlackList();
    blacklist.getLabels().clear();
    blacklist.getPokemons().clear();
    blacklist.getAspects().add("plushie");

    Utils.writeFileAsync(CobbleNotify.PATH, "config.json", Utils.newGson().toJson(CobbleNotify.config));

  }
}
