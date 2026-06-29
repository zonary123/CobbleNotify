package com.kingpixel.ultranotify.gui;

import ca.landonjw.gooeylibs2.api.UIManager;
import ca.landonjw.gooeylibs2.api.button.Button;
import ca.landonjw.gooeylibs2.api.helpers.PaginationHelper;
import ca.landonjw.gooeylibs2.api.page.LinkedPage;
import ca.landonjw.gooeylibs2.api.template.types.ChestTemplate;
import com.kingpixel.cobbleutils.Model.ItemModel;
import com.kingpixel.cobbleutils.Model.Rectangle;
import com.kingpixel.cobbleutils.util.AdventureTranslator;
import com.kingpixel.ultranotify.UltraNotify;
import com.kingpixel.ultranotify.models.history.HistoryTrade;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:49
 */
@Data
public class TradeGui {
  private int rows;
  private String title;
  private Rectangle rectangle;
  private ItemModel previous;
  private ItemModel close;
  private ItemModel next;

  public TradeGui() {
    this.rows = 6;
    this.title = "Spawn History";
    this.rectangle = new Rectangle();
    this.previous = new ItemModel("minecraft:arrow", "&aPrevious Page");
    this.previous.setSlot(45);
    this.close = new ItemModel("minecraft:barrier", "&cClose");
    this.close.setSlot(49);
    this.next = new ItemModel("minecraft:arrow", "&aNext Page");
    this.next.setSlot(53);
  }

  public void open(ServerPlayerEntity player, int numPage) {
    UltraNotify.runAsync(() -> {
      ChestTemplate template = ChestTemplate
        .builder(rows)
        .build();

      rectangle.apply(template);
      List<Button> buttons = new ArrayList<>();
      List<HistoryTrade> trades = new ArrayList<>(UltraNotify.databaseClient.getTradesHistory(numPage));

      int size = trades.size();
      int totalSlots = rectangle.getTotalSlots();
      boolean hasNext = size > totalSlots;
      if (numPage > 1) {
        previous.applyTemplate(template, previous.getButton(buttonAction -> open(player, numPage - 1)));
      }

      close.applyTemplate(template, close.getButton(buttonAction -> UIManager.closeUI(player)));

      if (hasNext) {
        next.applyTemplate(template, next.getButton(buttonAction -> open(player, numPage + 1)));
      }
      if (size > totalSlots) trades.removeLast();
      for (HistoryTrade trade : trades) {
        buttons.add(trade.toButton());
      }

      var builder = LinkedPage.builder()
        .template(template)
        .title(AdventureTranslator.toNative(title));
      LinkedPage page = PaginationHelper.createPagesFromPlaceholders(template, buttons, builder);

      UltraNotify.server.execute(() -> UIManager.openUIForcefully(player, page));
    });
  }
}
