package com.kingpixel.ultranotify.database;

import com.kingpixel.cobbleutils.Model.DataBaseConfig;
import com.kingpixel.ultranotify.UltraNotify;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:36
 */
public class DataBaseFactory {
  public static DataBaseClient createDataBaseClient(DataBaseConfig config) {
    try {
      DataBaseClient client = UltraNotify.databaseClient;
      if (client != null) client.disconnect();
      client = switch (config.getType()) {
        case MONGODB -> new MongoDBClient();
        case JSON -> new JSONDBClient();
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
