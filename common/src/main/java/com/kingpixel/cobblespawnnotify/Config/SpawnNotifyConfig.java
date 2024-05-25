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
    @SerializedName("command.cobblehuntparty") public int COMMAND_PARTY_PERMISSION_LEVEL = 0;
    // Admin
    @SerializedName("command.cobblehuntother") public int COMMAND_COBBLEHUNT_OTHER_PERMISSION_LEVEL = 2;
    @SerializedName("command.cobblehuntreload") public int COMMAND_RELOAD_PERMISSION_LEVEL = 2;
    @SerializedName("command.cobblehuntrestart") public int COMMAND_RESTART_PERMISSION_LEVEL = 2;
    @SerializedName("command.cobblehunterrestartall") public int COMMAND_RESTARTALL_PERMISSION_LEVEL = 2;

  }
}