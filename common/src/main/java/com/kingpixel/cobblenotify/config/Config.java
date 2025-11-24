package com.kingpixel.cobblenotify.config;

import com.kingpixel.cobblenotify.CobbleNotify;
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
    Utils.writeFileAsync(CobbleNotify.PATH, "config.json", Utils.newGson().toJson(CobbleNotify.config));

  }
}
