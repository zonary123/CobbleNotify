package com.kingpixel.cobblenotify.models;

import com.kingpixel.cobbleutils.Model.messages.HiperMessage;
import com.kingpixel.cobbleutils.Model.messages.HiperMessageBuilder;
import com.kingpixel.cobbleutils.Model.messages.MessageType;
import lombok.Data;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:43
 */
@Data
public class NotificationMessages {
  private HiperMessage spawnMessage = HiperMessageBuilder.builder()
    .setType(MessageType.CHAT_BROADCAST)
    .setRawMessage("%prefix% A wild %pokemon% has appeared in coords: x:%x% y:%y% z:%z%, world: %world%, biome: " +
      "%biome%!")
    .build();
  private HiperMessage defeatMessage = HiperMessageBuilder.builder()
    .setType(MessageType.CHAT_BROADCAST)
    .setRawMessage("%prefix% %player% has defeated a %pokemon%!")
    .build();
  private HiperMessage catchMessage = HiperMessageBuilder.builder()
    .setType(MessageType.CHAT_BROADCAST)
    .setRawMessage("%prefix% %player% has caught a %pokemon%!")
    .build();
  private HiperMessage tradeMessage = HiperMessageBuilder.builder()
    .setType(MessageType.CHAT_BROADCAST)
    .setRawMessage("%prefix% %player1% has traded a %pokemon1% with %player2%'s %pokemon2%!")
    .build();


}