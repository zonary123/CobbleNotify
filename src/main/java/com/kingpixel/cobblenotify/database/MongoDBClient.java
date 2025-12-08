package com.kingpixel.cobblenotify.database;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.models.history.HistorySpawn;
import com.kingpixel.cobblenotify.models.history.HistoryTrade;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:40
 */
public class MongoDBClient extends DataBaseClient {
  private MongoClient client;
  private MongoCollection<Document> spawnCollection;
  private MongoCollection<Document> tradeCollection;

  public MongoDBClient() {
    super();
  }

  @Override public void connect() {
    var config = getConfig();
    var settigns = MongoClientSettings.builder()
      .applyConnectionString(new com.mongodb.ConnectionString(config.getUrl()))
      .applicationName("CobbleNotify")
      .build();
    client = MongoClients.create(settigns);
    var database = client.getDatabase(config.getDatabase());
    spawnCollection = database.getCollection("spawned_pokemons");
    tradeCollection = database.getCollection("trade_history");
  }

  @Override void disconnect() {
    if (client != null) client.close();
  }

  @Override boolean isConnected() {
    return false;
  }

  @Override public void addSpawnedPokemon(HistorySpawn historySpawn) {
    var document = historySpawn.toDocument();
    spawnCollection.insertOne(document);
  }

  @Override public void addTradeHistory(HistoryTrade historyTrade) {
    var document = historyTrade.toDocument();
    tradeCollection.insertOne(document);
  }

  @Override public void updateHistorySpawn(HistorySpawn history) {
    var filter = Filters.eq("identifier", history.getIdentifier().toString());
    var document = history.toDocument();
    spawnCollection.replaceOne(filter, document);
  }


  @Override public HistorySpawn getSpawnedPokemonById(UUID uuid) {
    var filter = Filters.eq("identifier", uuid.toString());
    var document = spawnCollection.find(filter).first();
    if (document != null) {
      return HistorySpawn.fromDocument(document);
    }
    return null;
  }


  @Override public List<HistorySpawn> getSpawnedPokemons(int page) {
    int limit = CobbleNotify.lang.getSpawnGui().getRectangle().getTotalSlots();
    var documents = spawnCollection.find()
      .skip((page - 1) * limit)
      .limit(limit + 1)
      .into(new ArrayList<>());
    return documents.stream().map(HistorySpawn::fromDocument).toList();
  }

  @Override public List<HistorySpawn> getCaughtPokemons(int page) {
    int limit = CobbleNotify.lang.getCaughtGui().getRectangle().getTotalSlots();
    var filter = Filters.eq("caught", true);
    var documents = spawnCollection.find(filter)
      .skip((page - 1) * limit)
      .limit(limit + 1)
      .into(new ArrayList<>());
    return documents.stream().map(HistorySpawn::fromDocument).toList();
  }

  @Override public List<HistoryTrade> getTradesHistory(int page) {
    int limit = CobbleNotify.lang.getTradeGui().getRectangle().getTotalSlots();
    var documents = tradeCollection.find()
      .skip((page - 1) * limit)
      .limit(limit + 1)
      .into(new ArrayList<>());
    return documents.stream().map(HistoryTrade::fromDocument).toList();
  }
}
