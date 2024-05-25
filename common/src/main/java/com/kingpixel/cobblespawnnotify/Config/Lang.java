package com.kingpixel.cobblespawnnotify.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblespawnnotify.SpawnNotify;
import com.kingpixel.cobblespawnnotify.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:58
 */

public class Lang {
  private String prefix;
  private String reload;
  private String messagenotifyshiny;
  private String messagenotifylegendary;
  private String messagedefeatshiny;
  private String messagedefeatlegendary;
  private String messagecatchshiny;
  private String messagecatchlegendary;
  private Map<String, String> forms;
  private Map<String, String> biomes;
  private Map<String, String> worlds;

  public Lang() {
    prefix = "{#E39651}CobbleSpawnNotify {#EA814F}»";
    reload = "{#E39651}The plugin has been reloaded!";

    messagenotifyshiny = "{#86E19F}A wild {#E6E83B}shiny {#EAA34F}%pokemon% " +
      "%form%has spawned in a %biome% biome %world% {#EA814F}x:{#5DD4C5}%x% {#EA814F}y:{#5DD4C5}%y% " +
      "{#EA814F}z:{#5DD4C5}%z%";
    messagenotifylegendary = "{#86E19F}A wild {#9F66E7}legendary {#EAA34F}%pokemon% " +
      "%form%has spawned in a %biome% biome %world% {#EA814F}x:{#5DD4C5}%x% {#EA814F}y:{#5DD4C5}%y% " +
      "{#EA814F}z:{#5DD4C5}%z%";
    messagedefeatshiny = "{#86E19F}The {#E6E83B}shiny {#EAA34F}%pokemon% " +
      "%form%has been defeated by %player%";
    messagedefeatlegendary = "{#86E19F}The {#9F66E7}legendary {#EAA34F}%pokemon% " +
      "%form%has been defeated by %player%";
    messagecatchshiny = "{#86E19F}%player% has caught a {#E6E83B}shiny {#EAA34F}%pokemon% " +
      "%form%";
    messagecatchlegendary = "{#86E19F}%player% has caught a {#9F66E7}legendary {#EAA34F}%pokemon% " +
      "%form%";
    forms = new HashMap<>();
    forms.put("Normal", "");
    forms.put("Alolan", "Alolan ");
    forms.put("Galarian", "Galarian ");
    forms.put("Hisui", "{##6342db}Hisui ");
    biomes = new HashMap<>();
    biomes.put("beach", "Playa");
    worlds = new HashMap<>();
    worlds.put("overworld", "Mundo");
    worlds.put("the_nether", "Hell");
    worlds.put("the_end", "End");
  }

  public Map<String, String> getForms() {
    return forms;
  }

  public Map<String, String> getBiomes() {
    return biomes;
  }

  public String getMessagedefeatshiny() {
    return messagedefeatshiny;
  }

  public String getMessagedefeatlegendary() {
    return messagedefeatlegendary;
  }

  public String getMessagecatchshiny() {
    return messagecatchshiny;
  }

  public String getMessagecatchlegendary() {
    return messagecatchlegendary;
  }

  public Map<String, String> getWorlds() {
    return worlds;
  }

  public String getPrefix() {
    return prefix;
  }

  public String getReload() {
    return reload;
  }

  public String getMessagenotifyshiny() {
    return messagenotifyshiny;
  }

  public String getMessagenotifylegendary() {
    return messagenotifylegendary;
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(SpawnNotify.path + "lang/", SpawnNotify.config.getLang() + ".json",
      el -> {
        Gson gson = Utils.newGson();
        Lang lang = gson.fromJson(el, Lang.class);
        prefix = lang.getPrefix();
        reload = lang.getReload();
        messagenotifyshiny = lang.getMessagenotifyshiny();
        messagenotifylegendary = lang.getMessagenotifylegendary();
        messagedefeatshiny = lang.getMessagedefeatshiny();
        messagedefeatlegendary = lang.getMessagedefeatlegendary();
        messagecatchshiny = lang.getMessagecatchshiny();
        messagecatchlegendary = lang.getMessagecatchlegendary();
        forms = lang.getForms();
        biomes = lang.getBiomes();
        worlds = lang.getWorlds();
        String data = gson.toJson(this);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(SpawnNotify.path + "lang/", SpawnNotify.config.getLang() + ".json",
          data);
        if (!futureWrite.join()) {
          SpawnNotify.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
        }
      });

    if (!futureRead.join()) {
      SpawnNotify.LOGGER.info("No lang.json file found for" + SpawnNotify.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(SpawnNotify.path + "lang/", SpawnNotify.config.getLang() + ".json",
        data);

      if (!futureWrite.join()) {
        SpawnNotify.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
      }
    }
  }

}
