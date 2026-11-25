package com.kingpixel.ultranotify.command.base;

import com.kingpixel.ultranotify.api.ui.NotifyMenuRegistry;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:48
 */
public class TradeCommand {
  public static void register(LiteralArgumentBuilder<ServerCommandSource> base) {
    base.then(
      CommandManager.literal("trade")
        .executes(context -> {
          if (!context.getSource().isExecutedByPlayer()) {
            context.getSource().sendError(net.minecraft.text.Text.literal("This command can only be executed by a player."));
            return 0;
          }
          NotifyMenuRegistry.openTradeHistoryMenu(
            context.getSource().getPlayer(),
            1
          );
          return 1;
        })
    );
  }
}
