package com.kingpixel.cobblespawnnotify.Model;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:06
 */
public class InfoSpawn {
  private String pokemon;
  private String form;
  private String world;
  private String biome;
  private String player;
  private double x;
  private double y;
  private double z;


  public InfoSpawn() {
  }

  public InfoSpawn(String pokemon, String form, String biome, double x, double y, double z) {
    this.pokemon = pokemon;
    this.form = form;
    this.biome = biome;
    this.x = x;
    this.y = y;
    this.z = z;
  }

  public String getPlayer() {
    return player;
  }

  public void setPlayer(String player) {
    this.player = player;
  }

  public String getWorld() {
    return world;
  }

  public void setWorld(String world) {
    this.world = world;
  }

  public String getPokemon() {
    return pokemon;
  }

  public void setPokemon(String pokemon) {
    this.pokemon = pokemon;
  }

  public String getBiome() {
    return biome;
  }

  public String getForm() {
    return form;
  }

  public void setForm(String form) {
    this.form = form;
  }

  public void setBiome(String biome) {
    this.biome = biome;
  }

  public double getX() {
    return x;
  }

  public void setX(double x) {
    this.x = x;
  }

  public double getY() {
    return y;
  }

  public void setY(double y) {
    this.y = y;
  }

  public double getZ() {
    return z;
  }

  public void setZ(double z) {
    this.z = z;
  }
}
