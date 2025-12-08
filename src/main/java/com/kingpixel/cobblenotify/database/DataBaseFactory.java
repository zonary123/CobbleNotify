package com.kingpixel.cobblenotify.database;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.Model.DataBaseConfig;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:36
 */
public class DataBaseFactory {
  public static DataBaseClient createDataBaseClient(DataBaseConfig config) {
    try {
      DataBaseClient client = CobbleNotify.databaseClient;
      if (client != null) client.disconnect();
      client = switch (config.getType()) {
        case MONGODB -> new MongoDBClient();
        default -> null;
      };
      if (client != null) client.connect();
      return client;
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
