package com.kingpixel.cobblenotify.Config;

import com.google.gson.Gson;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.Model.discord.WebHookStruct;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * @author Carlos Varas Alonso - 28/04/2024 23:58
 */
@Getter
@ToString
public class Lang {
  private String prefix;
  private String reload;
  private WebHookStruct messageWebHookCatch;
  private WebHookStruct messageWebHookDefeat;
  private WebHookStruct messageWebHookSpawn;
  private WebHookStruct messageWebHookTrade;

  public Lang() {
    prefix = "&7[<#E39651>CobbleSpawnNotify&7] <#EA814F>» ";
    reload = "%prefix%<#E39651>The plugin has been reloaded!";
    messageWebHookCatch = new WebHookStruct();
    messageWebHookCatch.setEmbeds(List.of(new WebHookStruct.Embed("Catch", "%pokemon% has caught by %player1%\n " +
      "%gender% %form% %shiny% %player%\nShiny: %shiny%\n - Ivs: %ivshp% / %ivsatk% / %ivsdef% / %ivsspa% / %ivsspdef% / %ivsspeed%\n - Evs: %evshp% / %evsatk% / %evsdef% / %evsspa% / %evsspdef% / %evsspeed%\nAbility: %ability%\nNature: %nature%\nOwner: %owner%\nCountry: %country%\nBall: %ball%\nSize: %size%\nMoves: %move1% - %move2% - %move3% - %move4%\nTradeable: %tradeable%\nBreedable: %breedable%")));
    messageWebHookDefeat = new WebHookStruct();
    messageWebHookDefeat.setEmbeds(List.of(new WebHookStruct.Embed("Defeat", "%pokemon% has defeated by %player1%\n " +
      "%gender% %form% %shiny% %player%\nShiny: %shiny%\n - Ivs: %ivshp% / %ivsatk% / %ivsdef% / %ivsspa% / %ivsspdef% / %ivsspeed%\n - Evs: %evshp% / %evsatk% / %evsdef% / %evsspa% / %evsspdef% / %evsspeed%\nAbility: %ability%\nNature: %nature%\nOwner: %owner%\nCountry: %country%\nBall: %ball%\nSize: %size%\nMoves: %move1% - %move2% - %move3% - %move4%\nTradeable: %tradeable%\nBreedable: %breedable%")));
    messageWebHookSpawn = new WebHookStruct();
    messageWebHookSpawn.setEmbeds(List.of(new WebHookStruct.Embed("Spawn", "%pokemon% has spawned in %world% %x% %y% %z% %biome%\n " +
      "%gender% %form% %shiny% %player%\nShiny: %shiny%\n - Ivs: %ivshp% / %ivsatk% / %ivsdef% / %ivsspa% / %ivsspdef% / %ivsspeed%\n - Evs: %evshp% / %evsatk% / %evsdef% / %evsspa% / %evsspdef% / %evsspeed%\nAbility: %ability%\nNature: %nature%\nOwner: %owner%\nCountry: %country%\nBall: %ball%\nSize: %size%\nMoves: %move1% - %move2% - %move3% - %move4%\nTradeable: %tradeable%\nBreedable: %breedable%")));
    messageWebHookTrade = new WebHookStruct();
    messageWebHookTrade.setEmbeds(List.of(new WebHookStruct.Embed("Trade", "%pokemon% has traded by %player1%\n " +
      "%gender% %form% %shiny% %player%\nShiny: %shiny%\n - Ivs: %ivshp% / %ivsatk% / %ivsdef% / %ivsspa% / %ivsspdef% / %ivsspeed%\n - Evs: %evshp% / %evsatk% / %evsdef% / %evsspa% / %evsspdef% / %evsspeed%\nAbility: %ability%\nNature: %nature%\nOwner: %owner%\nCountry: %country%\nBall: %ball%\nSize: %size%\nMoves: %move1% - %move2% - %move3% - %move4%\nTradeable: %tradeable%\nBreedable: %breedable%")));
  }

  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleNotify.PATH + "lang/", CobbleNotify.config.getLang() + ".json",
      el -> {
        Gson gson = Utils.newGson();
        CobbleNotify.language = gson.fromJson(el, Lang.class);
        String data = gson.toJson(CobbleNotify.language);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH + "lang/", CobbleNotify.config.getLang() + ".json",
          data);
        if (!futureWrite.join()) {
          CobbleNotify.LOGGER.fatal("Could not write lang.json file for CobbleNotify.");
        }
      });

    if (!futureRead.join()) {
      CobbleNotify.LOGGER.info("No lang.json file found for" + CobbleNotify.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH + "lang/", CobbleNotify.config.getLang() + ".json",
        data);

      if (!futureWrite.join()) {
        CobbleNotify.LOGGER.fatal("Could not write lang.json file for CobbleNotify.");
      }
    }
  }

}
