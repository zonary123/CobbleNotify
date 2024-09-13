package com.kingpixel.cobblenotify.Config;

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
    @SerializedName("command.cobblenotify") public int COMMAND_COBBLESPAWNNOTIFY_PERMISSION_LEVEL = 2;
    // Admin


  }
}