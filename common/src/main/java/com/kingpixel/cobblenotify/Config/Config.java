package com.kingpixel.cobblenotify.Config;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.google.gson.Gson;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.Model.PokemonData;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.concurrent.CompletableFuture;


/**
 * @author Carlos Varas Alonso - 29/04/2024 0:14
 */
@Getter
@ToString
public class Config {
  private boolean debug;
  private String lang;
  private boolean commandaffected;
  private boolean shiny;
  private boolean legendary;
  private boolean notifyspawn;
  private boolean notifyspawnnearplayer;
  private boolean notifycatch;
  private boolean notifydefeat;
  private boolean notifytrades;
  private int distanceplayer;
  private int delaycheckdespawn;
  private List<PokemonData> specialPokemon;

  public Config() {
    debug = false;
    lang = "en";
    commandaffected = false;
    shiny = true;
    legendary = true;
    notifyspawn = true;
    notifyspawnnearplayer = true;
    notifycatch = true;
    notifydefeat = true;
    notifytrades = true;
    distanceplayer = 100;
    delaycheckdespawn = 5;
    specialPokemon = List.of(
      new PokemonData("eevee", "eerie")
    );
  }


  public void init() {
    CompletableFuture<Boolean> futureRead = Utils.readFileAsync(CobbleNotify.PATH, "config.json",
      el -> {
        Gson gson = Utils.newGson();
        Config config = gson.fromJson(el, Config.class);
        debug = config.isDebug();
        lang = config.getLang();
        commandaffected = config.isCommandaffected();
        shiny = config.isShiny();
        legendary = config.isLegendary();
        notifyspawn = config.isNotifyspawn();
        notifyspawnnearplayer = config.isNotifyspawnnearplayer();
        notifycatch = config.isNotifycatch();
        notifydefeat = config.isNotifydefeat();
        notifytrades = config.isNotifytrades();
        distanceplayer = config.getDistanceplayer();
        delaycheckdespawn = config.getDelaycheckdespawn();
        specialPokemon = config.getSpecialPokemon();
        String data = gson.toJson(this);
        CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH, "config.json",
          data);
        if (!futureWrite.join()) {
          CobbleNotify.LOGGER.fatal("Could not write lang.json file for CobbleHunt.");
        }
      });

    if (!futureRead.join()) {
      CobbleNotify.LOGGER.info("No config.json file found for" + CobbleNotify.MOD_NAME + ". Attempting to generate one.");
      Gson gson = Utils.newGson();
      String data = gson.toJson(this);
      CompletableFuture<Boolean> futureWrite = Utils.writeFileAsync(CobbleNotify.PATH, "config.json",
        data);

      if (!futureWrite.join()) {
        CobbleNotify.LOGGER.fatal("Could not write config.json file for CobbleHunt.");
      }
    }

  }

  public boolean isSpecialPokemon(Pokemon pokemon) {
    if (pokemon == null) return false;
    return specialPokemon.contains(PokemonData.from(pokemon));
  }
}
