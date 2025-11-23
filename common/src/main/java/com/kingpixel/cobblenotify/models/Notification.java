package com.kingpixel.cobblenotify.models;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobbleutils.Model.PokemonBlackList;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:40
 */
@Data
public class Notification {
  private PokemonBlackList filter;
  private NotificationOptions notificationOptions;
  private WebHookOptions webHookOptions;

  public Notification() {
    this.filter = new PokemonBlackList();
    this.notificationOptions = new NotificationOptions();
    this.webHookOptions = new WebHookOptions();
  }

  public void check() {
  }

  public boolean isBlackListed(Pokemon pokemon) {
    return filter.isBlackListed(pokemon);
  }

  public void computeCaught(Pokemon pokemon, ServerPlayerEntity player) {
    if (!isBlackListed(pokemon)) return;
    // TODO document why this method is empty
  }

  public void computeDefeat(Pokemon pokemon, ServerPlayerEntity player) {
    if (!isBlackListed(pokemon)) return;
    // TODO document why this method is empty
  }

  public boolean computeSpawn(PokemonEntity pokemonEntity) {
    Pokemon pokemon = pokemonEntity.getPokemon();
    if (!isBlackListed(pokemon)) return false;
    return true;
  }

  public void computeTrade(Pokemon pokemon1, Pokemon pokemon2, ServerPlayerEntity player1, ServerPlayerEntity player2) {
    if (!isBlackListed(pokemon1) && !isBlackListed(pokemon2)) return;
    // TODO document why this method is empty
  }


}
