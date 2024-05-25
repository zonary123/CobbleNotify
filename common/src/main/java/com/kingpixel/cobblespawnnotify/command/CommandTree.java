package com.kingpixel.cobblespawnnotify.command;

import com.kingpixel.cobblespawnnotify.SpawnNotify;
import com.kingpixel.cobblespawnnotify.permissions.SpawnNotifyPermissions;
import com.kingpixel.cobblespawnnotify.utils.TextUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.Objects;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:35
 */
public class CommandTree {
  private static final String literal = "cobblespawnnotify";

  public static void register(
    CommandDispatcher<CommandSourceStack> dispatcher
  ) {
    LiteralArgumentBuilder<CommandSourceStack> base = Commands.literal(literal)
      .requires(source -> SpawnNotifyPermissions.checkPermission(source, SpawnNotify.permissions.SPAWN_NOTIFY_NORMAL_PERMISSION));

    dispatcher.register(base.then(Commands.literal("reload")
      .requires(source -> SpawnNotifyPermissions.checkPermission(source, SpawnNotify.permissions.SPAWN_NOTIFY_RELOAD_PERMISSION))
      .executes(context -> {
        SpawnNotify.load();
        Objects.requireNonNull(context.getSource().getPlayer()).sendSystemMessage(TextUtil.parseHexCodes(SpawnNotify.language.getReload().replace("%prefix%", SpawnNotify.language.getPrefix()), false));
        return 1;
      })));
  }

  public static void register(CommandDispatcher<CommandSourceStack> commandSourceStackCommandDispatcher, CommandBuildContext commandBuildContext, Commands.CommandSelection commandSelection) {
    register(commandSourceStackCommandDispatcher);
  }
}
