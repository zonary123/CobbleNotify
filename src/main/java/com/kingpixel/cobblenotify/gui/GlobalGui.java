package com.kingpixel.cobblenotify.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.page.GooeyPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import net.minecraft.server.network.ServerPlayerEntity;

/**
 * @author Carlos Varas Alonso - 08/12/2025 0:29
 */
public class GlobalGui {
  private int rows;
  private String title;
  private ItemModel spawnHistoryItem;
  private ItemModel caughtHistoryItem;
  private ItemModel tradeHistoryItem;

  public GlobalGui() {
    this.rows = 3;
    this.title = "History Menu";
    this.spawnHistoryItem = new ItemModel("minecraft:chest", "&aSpawn History");
    this.spawnHistoryItem.setSlot(11);
    this.caughtHistoryItem = new ItemModel("minecraft:chest", "&aCaught History");
    this.caughtHistoryItem.setSlot(13);
    this.tradeHistoryItem = new ItemModel("minecraft:chest", "&aTrade History");
    this.tradeHistoryItem.setSlot(15);
  }

  public void open(ServerPlayerEntity player) {
    CobbleNotify.runAsync(() -> {
      ChestTemplate template = ChestTemplate
        .builder(rows)
        .build();

      spawnHistoryItem.applyTemplate(template, spawnHistoryItem.getButton(buttonAction -> {
        CobbleNotify.lang.getSpawnGui().open(player, 1);
      }));

      caughtHistoryItem.applyTemplate(template, caughtHistoryItem.getButton(buttonAction -> {
        CobbleNotify.lang.getCaughtGui().open(player, 1);
      }));

      tradeHistoryItem.applyTemplate(template, tradeHistoryItem.getButton(buttonAction -> {
        CobbleNotify.lang.getTradeGui().open(player, 1);
      }));

      GooeyPage page = GooeyPage.builder()
        .title(AdventureTranslator.toNative(title))
        .template(template)
        .build();

      CobbleNotify.server.execute(() -> UIManager.openUIForcefully(player, page));
    });
  }
}
