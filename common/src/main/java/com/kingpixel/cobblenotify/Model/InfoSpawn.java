package com.kingpixel.cobblenotify.Model;

import com.cobblemon.mod.common.pokemon.Pokemon;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:06
 */
@Getter
@Setter
@ToString
public class InfoSpawn {
  private Pokemon pokemon;
  private String form;
  private String world;
  private String biome;
  private String player;
  private double x;
  private double y;
  private double z;


  public InfoSpawn() {
  }

  public InfoSpawn(Pokemon pokemon, String form, String world, String biome, String player, double x, double y, double z) {
    this.pokemon = pokemon;
    this.form = form;
    this.world = world;
    this.biome = biome;
    this.player = player;
    this.x = x;
    this.y = y;
    this.z = z;
  }
}
