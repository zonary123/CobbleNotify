package com.kingpixel.cobblenotify.command.base;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:48
 */
public class CaughtCommand {
  public static void register(LiteralArgumentBuilder<ServerCommandSource> base) {
    base.then(
      CommandManager.literal("caught")
        .executes(context -> {
          CobbleNotify.lang.getCaughtGui().open(
            context.getSource().getPlayer(),
            1
          );
          return 1;
        })
    );
  }
}
