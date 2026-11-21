package com.kingpixel.ultranotify.api.model;

import lombok.Getter;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * Immutable viewmodel representing a historical spawn or catch record.
 */
@Getter
public class SpawnHistoryView {

  private final UUID identifier;
  private final String notificationId;
  private final UUID playerId;
  private final String pokemonName;
  private final Instant spawnTime;
  private final String world;
  private final String location;
  private final List<String> nearbyPlayers;
  private final boolean caught;
  private final String caughtBy;
  private final Instant caughtDate;

  public SpawnHistoryView(UUID identifier,
                          String notificationId,
                          UUID playerId,
                          String pokemonName,
                          Instant spawnTime,
                          String world,
                          String location,
                          List<String> nearbyPlayers,
                          boolean caught,
                          String caughtBy,
                          Instant caughtDate) {
    this.identifier = identifier;
    this.notificationId = notificationId != null ? notificationId : "";
    this.playerId = playerId;
    this.pokemonName = pokemonName != null ? pokemonName : "";
    this.spawnTime = spawnTime;
    this.world = world != null ? world : "";
    this.location = location != null ? location : "";
    this.nearbyPlayers = nearbyPlayers != null ? Collections.unmodifiableList(List.copyOf(nearbyPlayers)) : Collections.emptyList();
    this.caught = caught;
    this.caughtBy = caughtBy != null ? caughtBy : "";
    this.caughtDate = caughtDate;
  }
}
