package com.kingpixel.ultranotify.api.model;

import lombok.Getter;

import java.time.Instant;
import java.util.UUID;

/**
 * Immutable viewmodel representing a historical trade record.
 */
@Getter
public class TradeHistoryView {

  private final UUID identifier;
  private final String notificationId;
  private final UUID trader1Id;
  private final String trader1Name;
  private final String pokemon1Name;
  private final UUID trader2Id;
  private final String trader2Name;
  private final String pokemon2Name;
  private final Instant tradeTime;

  public TradeHistoryView(UUID identifier,
                          String notificationId,
                          UUID trader1Id,
                          String trader1Name,
                          String pokemon1Name,
                          UUID trader2Id,
                          String trader2Name,
                          String pokemon2Name,
                          Instant tradeTime) {
    this.identifier = identifier;
    this.notificationId = notificationId != null ? notificationId : "";
    this.trader1Id = trader1Id;
    this.trader1Name = trader1Name != null ? trader1Name : "";
    this.pokemon1Name = pokemon1Name != null ? pokemon1Name : "";
    this.trader2Id = trader2Id;
    this.trader2Name = trader2Name != null ? trader2Name : "";
    this.pokemon2Name = pokemon2Name != null ? pokemon2Name : "";
    this.tradeTime = tradeTime;
  }
}
