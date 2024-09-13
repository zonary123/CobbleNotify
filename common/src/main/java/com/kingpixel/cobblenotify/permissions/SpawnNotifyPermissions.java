package com.kingpixel.cobblenotify.permissions;

import com.cobblemon.mod.common.Cobblemon;
import com.cobblemon.mod.common.api.permission.CobblemonPermission;
import com.cobblemon.mod.common.api.permission.PermissionLevel;
import com.kingpixel.cobblenotify.CobbleNotify;
import net.minecraft.commands.CommandSourceStack;

/**
 * @author Carlos Varas Alonso - 10/05/2024 20:37
 */
public class SpawnNotifyPermissions {

  public final CobblemonPermission SPAWN_NOTIFY_RELOAD_PERMISSION;
  public final CobblemonPermission SPAWN_NOTIFY_NORMAL_PERMISSION;
  public final CobblemonPermission SPAWN_NOTIFY_SHINY_PERMISSION;
  public final CobblemonPermission SPAWN_NOTIFY_LEGENDARY_PERMISSION;

  public SpawnNotifyPermissions() {
    this.SPAWN_NOTIFY_NORMAL_PERMISSION = new CobblemonPermission("cobblenotify.command.cobblenotify.normal",
      toPermLevel(CobbleNotify.dexpermission.permissionLevels.COMMAND_COBBLESPAWNNOTIFY_PERMISSION_LEVEL));
    this.SPAWN_NOTIFY_SHINY_PERMISSION = new CobblemonPermission("cobblenotify.command.cobblenotify.shiny",
      toPermLevel(CobbleNotify.dexpermission.permissionLevels.COMMAND_COBBLESPAWNNOTIFY_PERMISSION_LEVEL));
    this.SPAWN_NOTIFY_LEGENDARY_PERMISSION = new CobblemonPermission("cobblenotify.command.cobblenotify" +
      ".legendary", toPermLevel(CobbleNotify.dexpermission.permissionLevels.COMMAND_COBBLESPAWNNOTIFY_PERMISSION_LEVEL));

    // Admin
    this.SPAWN_NOTIFY_RELOAD_PERMISSION = new CobblemonPermission("cobblenotify.command.cobblenotify.reload",
      PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS);
  }

  public PermissionLevel toPermLevel(int permLevel) {
    for (PermissionLevel value : PermissionLevel.values()) {
      if (value.ordinal() == permLevel) {
        return value;
      }
    }
    return PermissionLevel.CHEAT_COMMANDS_AND_COMMAND_BLOCKS;
  }

  public static boolean checkPermission(CommandSourceStack source, CobblemonPermission permission) {
    return Cobblemon.INSTANCE.getPermissionValidator().hasPermission(source, permission);
  }
}

