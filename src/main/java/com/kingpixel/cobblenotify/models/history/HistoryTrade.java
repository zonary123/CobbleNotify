package com.kingpixel.cobblenotify.models.history;

import ca.landonjw.gooeylibs2.api.button.GooeyButton;
import com.cobblemon.mod.common.item.PokemonItem;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.models.Notification;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;
import org.bson.Document;

import java.time.Instant;

/**
 * @author Carlos Varas Alonso - 07/12/2025 21:35
 */
@Data
public class HistoryTrade {
  private String notificationId;
  private String trader1;
  private Pokemon pokemon1;
  private String trader2;
  private Pokemon pokemon2;
  private Instant tradeTime = Instant.now();


  public HistoryTrade(Pokemon poke1, Pokemon poke2, ServerPlayerEntity player1,
                      ServerPlayerEntity player2, Notification notification) {
    this.trader1 = player1.getGameProfile().getName();
    this.pokemon1 = poke1;
    this.trader2 = player2.getGameProfile().getName();
    this.pokemon2 = poke2;
    this.notificationId = notification.getIdentifier();
  }


  // Convert to MongoDB Document
  public Document toDocument() {
    return Document.parse(Utils.newWithoutSpacingGson().toJson(this, HistoryTrade.class));
  }

  public static HistoryTrade fromDocument(Document document) {
    return Utils.newWithoutSpacingGson().fromJson(document.toJson(), HistoryTrade.class);
  }

  public GooeyButton toButton() {
    return GooeyButton.builder()
      .display(PokemonItem.from(pokemon1))
      .build();
  }


}
