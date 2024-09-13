package com.kingpixel.cobblenotify.command;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.permissions.SpawnNotifyPermissions;
import com.kingpixel.cobblenotify.utils.NotifyUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:35
 */
public class CommandTree {
  private static final String literal = "cobblenotify";

  public static void register(
    CommandDispatcher<CommandSourceStack> dispatcher
  ) {
    LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal(literal)
      .requires(source -> SpawnNotifyPermissions.checkPermission(source, CobbleNotify.permissions.SPAWN_NOTIFY_NORMAL_PERMISSION));

    dispatcher.register(base.then(Commands.literal("reload")
      .requires(source -> SpawnNotifyPermissions.checkPermission(source, CobbleNotify.permissions.SPAWN_NOTIFY_RELOAD_PERMISSION))
      .executes(context -> {
        CobbleNotify.load();
        if (!context.getSource().isPlayer()) {
          CobbleNotify.LOGGER.info(CobbleNotify.language.getReload().replace(
            "%prefix%", CobbleNotify.language.getPrefix()));
          return 1;
        } else {
          NotifyUtils.adventure(context.getSource().getPlayerOrException(),
            CobbleNotify.language.getReload().replace(
              "%prefix%", CobbleNotify.language.getPrefix()));
          return 1;
        }
      })));
  }

}
