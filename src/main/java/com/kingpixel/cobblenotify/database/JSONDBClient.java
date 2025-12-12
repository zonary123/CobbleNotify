package com.kingpixel.cobblenotify.database;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.models.history.HistorySpawn;
import com.kingpixel.cobblenotify.models.history.HistoryTrade;
import com.kingpixel.cobbleutils.util.Utils;
import org.bson.Document;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

;

public class JSONDBClient extends DataBaseClient {

  private File base;
  private File spawnFolder;
  private File tradeFolder;
  private File tradeDictionaryFolder;

  @Override
  public void connect() {
    base = Utils.getAbsolutePath(CobbleNotify.PATH + "data/");
    base.mkdirs();

    spawnFolder = new File(base, "spawns");
    spawnFolder.mkdirs();

    tradeFolder = new File(base, "trades");
    tradeFolder.mkdirs();

    tradeDictionaryFolder = new File(tradeFolder, "dictionary");
    tradeDictionaryFolder.mkdirs();
  }

  @Override
  void disconnect() {
  }

  @Override
  boolean isConnected() {
    return base.exists();
  }

  // ------------------------------------------------------
  // SPAWNS
  // ------------------------------------------------------

  @Override
  public void addSpawnedPokemon(HistorySpawn spawn) {
    saveSpawn(spawn);
  }

  @Override
  public void updateHistorySpawn(HistorySpawn spawn) {
    saveSpawn(spawn);
  }

  private void saveSpawn(HistorySpawn spawn) {
    UUID playerId = spawn.getPlayerId();
    if (playerId == null) return;

    File playerFolder = new File(spawnFolder, playerId.toString());
    playerFolder.mkdirs();

    File file = new File(playerFolder, spawn.getIdentifier().toString() + ".json");
    Utils.writeFileSync(file, spawn.toDocument().toJson());
  }

  @Override
  public HistorySpawn getSpawnedPokemonById(UUID uuid) {
    // Search inside all player folders
    for (File playerFolder : Objects.requireNonNull(spawnFolder.listFiles())) {
      File f = new File(playerFolder, uuid.toString() + ".json");
      if (f.exists()) {
        try {
          JSONObject json = new JSONObject(Utils.readFileSync(f));
          return HistorySpawn.fromDocument(Document.parse(json.toString()));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    return null;
  }

  @Override
  public List<HistorySpawn> getSpawnedPokemons(int page) {
    int limit = 50;
    List<HistorySpawn> result = new ArrayList<>();
    var list = spawnFolder.listFiles();
    if (list == null) return List.of();
    for (File player : list) {
      for (File f : Objects.requireNonNull(player.listFiles())) {
        try {
          JSONObject json = new JSONObject(Utils.readFileSync(f));
          result.add(Utils.newWithoutSpacingGson().fromJson(json.toString(), HistorySpawn.class));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }

    return paginate(result, page, limit);
  }

  @Override
  public List<HistorySpawn> getCaughtPokemons(int page) {
    List<HistorySpawn> all = getSpawnedPokemons(Integer.MAX_VALUE);
    List<HistorySpawn> caught = all.stream()
      .filter(HistorySpawn::isCaught)
      .toList();
    return paginate(caught, page, 50);
  }

  // ------------------------------------------------------
  // TRADES
  // ------------------------------------------------------

  @Override
  public void addTradeHistory(HistoryTrade trade) {
    saveTrade(trade);
    indexTradeForPlayer(trade.getTrader1Id(), trade);
    indexTradeForPlayer(trade.getTrader2Id(), trade);
  }

  private void saveTrade(HistoryTrade trade) {
    File file = new File(tradeFolder, trade.getIdentifier().toString() + ".json");
    Utils.writeFileSync(file, trade.toDocument().toJson());
  }

  /**
   * Player dictionary:
   * {
   * "trades": {
   * "tradeUUID": true
   * }
   * }
   */
  private void indexTradeForPlayer(UUID player, HistoryTrade trade) {
    if (player == null) return;

    File file = new File(tradeDictionaryFolder, player.toString() + ".json");

    JSONObject json;
    JSONObject tradeMap;

    try {
      if (file.exists()) {
        json = new JSONObject(Utils.readFileSync(file));
        tradeMap = json.optJSONObject("trades");

        // Fix missing or corrupted "trades" field
        if (tradeMap == null) {
          tradeMap = new JSONObject();
          json.put("trades", tradeMap);
        }
      } else {
        // Create fresh JSON structure
        json = new JSONObject();
        tradeMap = new JSONObject();
        json.put("trades", tradeMap);
      }

      // Insert trade UUID with O(1) lookup
      tradeMap.put(trade.getIdentifier().toString(), true);

      // Pretty write JSON
      Utils.writeFileSync(file, json.toString(2));

    } catch (Exception e) {
      throw new RuntimeException("Failed to update trade dictionary for player " + player, e);
    }
  }


  @Override
  public List<HistoryTrade> getTradesHistory(int page) {
    int limit = 50;
    List<HistoryTrade> list = new ArrayList<>();

    for (File f : Objects.requireNonNull(tradeFolder.listFiles())) {
      if (f.isDirectory()) continue; // skip "dictionary/"
      try {
        JSONObject json = new JSONObject(com.kingpixel.cobbleutils.util.Utils.readFileSync(f));
        list.add(HistoryTrade.fromDocument(Document.parse(json.toString())));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    return paginate(list, page, limit);
  }

  // ------------------------------------------------------
  // Utility
  // ------------------------------------------------------

  private <T> List<T> paginate(List<T> list, int page, int limit) {
    int start = (page - 1) * limit;
    int end = Math.min(start + limit, list.size());
    if (start >= list.size()) return List.of();
    return list.subList(start, end);
  }
}
