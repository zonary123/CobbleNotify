package com.kingpixel.cobblenotify.config;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;

import java.io.File;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:47
 */
@Data
public class Lang {
  private String prefix = "&6[&bCobbleNotify&6]&r";

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
