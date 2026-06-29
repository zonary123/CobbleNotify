package com.kingpixel.ultranotify.command;

import com.kingpixel.cobbleutils.util.LuckPermsUtil;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.command.base.CaughtCommand;
import com.kingpixel.ultranotify.command.base.SpawnCommand;
import com.kingpixel.ultranotify.command.base.TradeCommand;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:35
 */
public class CommandTree {
  private static final String literal = "ultranotify";

  public static void register(
    CommandDispatcher<ServerCommandSource> dispatcher
  ) {
    LiteralArgumentBuilder<ServerCommandSource> base = CommandManager.literal(literal)
      .requires(source -> LuckPermsUtil.checkPermission(source, 2, "ultranotify.admin"));
    base
      .executes(context -> {
        if (!context.getSource().isExecutedByPlayer()) return 1;
        var player = context.getSource().getPlayer();
        UltraNotify.lang.getGlobalGui().open(player);
        return 1;
      })
      .then(CommandManager.literal("reload")
        .requires(source -> LuckPermsUtil.checkPermission(source, 2, "ultranotify.admin"))
        .executes(context -> {
          UltraNotify.load();
          context.getSource().sendMessage(
            Text.literal("CobbleNotify configuration reloaded!")
          );
          return 1;
        })
      );

    SpawnCommand.register(base);
    TradeCommand.register(base);
    CaughtCommand.register(base);
    dispatcher.register(base);
  }

}
