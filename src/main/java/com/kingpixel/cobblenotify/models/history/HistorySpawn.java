package com.kingpixel.cobblenotify.models.history;

import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.models.Notification;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;
import org.bson.Document;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:35
 */
@Data
public class HistorySpawn {
  private String notificationId;
  private UUID identifier;
  private UUID playerId;
  private Instant spawnTime;
  private String world;
  private String location;
  private List<String> nearbyPlayers = new ArrayList<>();
  private boolean caught;
  private String caughtBy;
  private Instant caughtDate;
  private Pokemon pokemon;

  public HistorySpawn(PokemonEntity entity, List<PlayerEntity> players, Notification notification) {
    this.spawnTime = Instant.now();
    if (!players.isEmpty()) {
      this.playerId = players.getFirst().getUuid();
    }
    World entityWorld = entity.getWorld();
    this.world = entityWorld.getRegistryKey().getValue().toString();
    var pos = entity.getBlockPos();
    this.location = "X: " + pos.getX() + " Y: " + pos.getY() + " Z: " + pos.getZ();
    for (PlayerEntity player : players) {
      this.nearbyPlayers.add(player.getGameProfile().getName());
    }
    this.caught = false;
    Pokemon poke = entity.getPokemon();
    this.identifier = poke.getUuid();
    this.pokemon = poke;
    this.notificationId = notification.getIdentifier();
  }

  public void caught(ServerPlayerEntity player) {
    this.caught = true;
    this.caughtBy = player.getGameProfile().getName();
    this.caughtDate = Instant.now();
  }

  // Convert to MongoDB Document
  public Document toDocument() {
    return Document.parse(Utils.newWithoutSpacingGson().toJson(this, HistorySpawn.class));
  }

  public static HistorySpawn fromDocument(Document document) {
    if (document == null) return null;
    return Utils.newWithoutSpacingGson().fromJson(document.toJson(), HistorySpawn.class);
  }

  public GooeyButton toButton() {
    List<String> lore = new ArrayList<>(PokemonUtils.replace(CobbleNotify.lang.getSpawnLore(), pokemon));
    lore.replaceAll(s -> s
      .replace("%spawnTime%", getFormatTime(spawnTime))
      .replace("%world%", world != null ? CobbleUtils.language.getWorlds().getOrDefault(world, world) : "-")
      .replace("%nearbyPlayers%", String.join(", ", nearbyPlayers.isEmpty() ? List.of("-") : nearbyPlayers))
      .replace("%caught%", caught ? CobbleUtils.language.getYes() : CobbleUtils.language.getNo())
      .replace("%caughtBy%", caughtBy != null ? caughtBy : "-")
      .replace("%caughtDate%", caughtDate != null ? getFormatTime(caughtDate) : "-")
      .replace("%location%", location != null ? location : "-")
    );
    ItemStack pokemonItem = pokemon == null ? Items.PAPER.getDefaultStack() : PokemonItem.from(pokemon);
    if (pokemonItem == null) pokemonItem = Items.PAPER.getDefaultStack();
    return GooeyButton.builder()
      .display(pokemonItem)
      .with(DataComponentTypes.CUSTOM_NAME, AdventureTranslator.toNative(
        PokemonUtils.replace(pokemon)
      ))
      .with(DataComponentTypes.LORE, new LoreComponent(
        AdventureTranslator.toNativeL(
          lore
        )
      ))
      .build();
  }

  private static final DateTimeFormatter FORMATTER =
    DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
      .withZone(ZoneId.systemDefault());

  private String getFormatTime(Instant instant) {
    return FORMATTER.format(instant);
  }

}


