package com.kingpixel.ultranotify.database;

import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.models.history.HistorySpawn;
import com.kingpixel.ultranotify.models.history.HistoryTrade;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:34
 */
public abstract class DataBaseClient {
  protected static DataBaseConfig getConfig() {
    return UltraNotify.config.getDatabase();
  }

  public abstract void connect();

  abstract void disconnect();

  abstract boolean isConnected();

  // ADD Methods
  public abstract void addSpawnedPokemon(HistorySpawn pokemon);

  public abstract void addTradeHistory(HistoryTrade historyTrade);

  // UPDATE Methods
  public abstract void updateHistorySpawn(HistorySpawn history);

  // Get Methods
  public abstract @Nullable HistorySpawn getSpawnedPokemonById(UUID uuid);

  public abstract List<HistorySpawn> getSpawnedPokemons(int page);

  public abstract List<HistorySpawn> getCaughtPokemons(int page);

  public abstract List<HistoryTrade> getTradesHistory(int page);

}
