package com.kingpixel.cobblespawnnotify.Config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;


public class SpawnNotifyConfig {

  public static Gson GSON = new GsonBuilder()
    .disableHtmlEscaping()
    .setPrettyPrinting()
    .create();
  @SerializedName("permissionlevels") public PermissionLevels permissionLevels = new PermissionLevels();

  public class PermissionLevels {
    // User
    @SerializedName("command.cobblespawnnotify") public int COMMAND_COBBLEHUNT_PERMISSION_LEVEL = 0;
    // Admin


  }
}