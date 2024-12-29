package com.kingpixel.cobblenotify.command;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.util.LuckPermsUtil;
import com.kingpixel.cobbleutils.util.PlayerUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

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
            if (!context.getSource().isExecutedByPlayer()) {
              CobbleNotify.LOGGER.info(CobbleNotify.language.getReload().replace(
                "%prefix%", CobbleNotify.language.getPrefix()));
              return 1;
            } else {
              PlayerUtils.sendMessage(
                context.getSource().getPlayerOrThrow(),
                CobbleNotify.language.getReload(),
                CobbleNotify.language.getPrefix()
              );
              return 1;
            }
          })
        )
      );
  }

}
