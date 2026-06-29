package com.kingpixel.ultranotify.models.notification;

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
    .setRawMessage("%prefix% <#F2B78A>A wild <#94C3E3>%pokemon% <#F2B78A>spawned at " +
      "<#DE9157>x:<#FFFFFF>%x% <#DE9157>y:<#FFFFFF>%y% <#DE9157>z:<#FFFFFF>%z% " +
      "<#F2B78A>in <#CEF2AC>%world% <#F2B78A>(<#CEF2AC>%biome%<#F2B78A>).")
    .build();
  private HiperMessage defeatMessage = HiperMessageBuilder.builder()
    .setType(MessageType.CHAT_BROADCAST)
    .setRawMessage("%prefix% %player% <#F2B78A>has defeated a <#94C3E3>%pokemon%<#F2B78A>!")
    .build();
  private HiperMessage catchMessage = HiperMessageBuilder.builder()
    .setType(MessageType.CHAT_BROADCAST)
    .setRawMessage("%prefix% %player% <#F2B78A>has caught a <#94C3E3>%pokemon%<#F2B78A>!")
    .build();
  private HiperMessage tradeMessage = HiperMessageBuilder.builder()
    .setType(MessageType.CHAT_BROADCAST)
    .setRawMessage("%prefix% <#DE8D2A>%player1% <#F2B78A>has traded <#94C3E3>%pokemon1% <#F2B78A>with " +
      "<#DE8D2A>%player2% <#F2B78A>for <#94C3E3>%pokemon2%<#F2B78A>!")
    .build();


}