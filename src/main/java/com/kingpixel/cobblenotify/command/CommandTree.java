package com.kingpixel.cobblenotify.command;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.util.LuckPermsUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;

/**
 * @author Carlos Varas Alonso - 25/05/2024 19:35
 */
public class CommandTree {
  private static final String literal = "cobblenotify";

  public static void register(
    CommandDispatcher<ServerCommandSource> dispatcher
  ) {
    LiteralArgumentBuilder<ServerCommandSource> base = CommandManager.literal(literal)
      .requires(source -> LuckPermsUtil.checkPermission(source, 2, "cobblenotify.admin"));

    dispatcher
      .register(base
        .then(CommandManager.literal("reload")
          .requires(source -> LuckPermsUtil.checkPermission(source, 2, "cobblenotify.admin"))
          .executes(context -> {
            CobbleNotify.load();
            context.getSource().sendMessage(
              Text.literal("CobbleNotify configuration reloaded!")
            );
            return 1;
          })
        )
      );
  }

}
