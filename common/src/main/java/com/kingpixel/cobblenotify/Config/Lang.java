package com.kingpixel.cobblenotify.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:58
 */
@Getter
@ToString
public class Lang {
  private String prefix;
  private String reload;
  private String messagenotifyshiny;
  private String messagenotifylegendary;
  private String messagedefeatshiny;
  private String messagedefeatlegendary;
  private String messagecatchshiny;
  private String messagecatchlegendary;
  private String messagetradenormal;
  private String messagetradeshiny;
  private String messagetradelegendary;
  private String messageDeathPokemon;
  private String messageDespawnPokemon;
  private String messageSpecialPokemon;
  private Map<String, String> worlds;

  public Lang() {
    prefix = "&7[<#E39651>CobbleSpawnNotify&7] <#EA814F>» ";
    reload = "%prefix%<#E39651>The plugin has been reloaded!";
    messagenotifyshiny = "%prefix%<#86E19F>A wild %pokemon% %gender% %shiny% &f(&b%form%&f) <#86E19F>has spawned in a" +
      " biome: &b%biome% <#86E19F>world: &b%world% <#86E19F>x:<#5DD4C5>%x% <#86E19F>y:<#5DD4C5>%y% <#86E19F>z:<#5DD4C5>%z%";
    messagenotifylegendary = "%prefix%<#86E19F>A wild <#9F66E7>legendary <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f) <#86E19F>has spawned in a " +
      "biome: &b%biome% <#86E19F>world: &b%world% <#EA814F>x:<#5DD4C5>%x% <#EA814F>y:<#5DD4C5>%y% <#EA814F>z:<#5DD4C5>%z%";
    messagedefeatshiny = "%prefix%<#86E19F>The <#E6E83B>shiny <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f) <#86E19F>has been defeated by " +
      "&e%player%";
    messagedefeatlegendary = "%prefix%<#86E19F>The <#9F66E7>legendary <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f) <#86E19F>has " +
      "been defeated by &e%player%";
    messagecatchshiny = "%prefix%<#86E19F>%player% has caught a <#E6E83B>shiny <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f)";
    messagecatchlegendary = "%prefix%<#86E19F>%player% has caught a <#9F66E7>legendary <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f)";
    messagetradenormal = "%prefix%<#86E19F>%player1% has traded a <#EAA34F>%pokemon1% &f(&b%form1%&f) <#86E19F>for a " +
      "%player2% <#EAA34F>%pokemon2% %form2%";
    messagetradeshiny = "%prefix%<#86E19F>%player1% has traded to <#EAA34F>%pokemon1% %legendary1% %gender1% %shiny1% &f(&b%form1%&f) <#86E19F>for to %player2% <#EAA34F>%pokemon2% %legendary2% %gender2% % shiny2% %form2%";
    messagetradelegendary = "%prefix%<#86E19F>%player1% has traded to <#EAA34F>%pokemon1% %legendary1% %gender1% %shiny1% &f(&b%form1%&f) <#86E19F>for to %player2% <#EAA34F>%pokemon2% %legendary2% %gender2% % shiny2% %form2%";
    messageDeathPokemon = "%prefix%<#86E19F>The <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f) <#86E19F>has died " +
      "by &6%entity%";
    messageDespawnPokemon = "%prefix%<#86E19F>The <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f) <#86E19F>has despawned";
    messageSpecialPokemon = "%prefix%<#86E19F>A special wild <#EAA34F>%pokemon% %gender% %shiny% &f(&b%form%&f) " +
      "<#86E19F>has spawned in a biome: &b%biome% <#86E19F>world: &b%world% <#EA814F>x:<#5DD4C5>%x% <#EA814F>y:<#5DD4C5>%y% <#EA814F>z:<#5DD4C5>%z%";
    worlds = new HashMap<>();
    worlds.put("overworld", "World");
    worlds.put("the_nether", "Hell");
    worlds.put("the_end", "End");
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleNotify.PATH + "lang/", CobbleNotify.config.getLang() + ".json",
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
        messagetradenormal = lang.getMessagetradenormal();
        messagetradeshiny = lang.getMessagetradeshiny();
        messagetradelegendary = lang.getMessagetradelegendary();
        messageDeathPokemon = lang.getMessageDeathPokemon();
        messageDespawnPokemon = lang.getMessageDespawnPokemon();
        messageSpecialPokemon = lang.getMessageSpecialPokemon();
        worlds = lang.getWorlds();
        String data = gson.toJson(this);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH + "lang/", CobbleNotify.config.getLang() + ".json",
          data);
        if (!futureWrite.join()) {
          CobbleNotify.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
        }
      });

    if (!futureRead.join()) {
      CobbleNotify.LOGGER.info("No lang.json file found for" + CobbleNotify.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH + "lang/", CobbleNotify.config.getLang() + ".json",
        data);

      if (!futureWrite.join()) {
        CobbleNotify.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
      }
    }
  }

}
