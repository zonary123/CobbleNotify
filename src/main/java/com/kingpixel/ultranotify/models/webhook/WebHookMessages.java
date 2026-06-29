package com.kingpixel.ultranotify.models.webhook;

import lombok.Data;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:43
 */
@Data
public class WebHookMessages {
  private WebHookStruct spawnMessage;
  private WebHookStruct defeatMessage;
  private WebHookStruct catchMessage;
  private WebHookStruct tradeMessage;

  public WebHookMessages() {
    this.spawnMessage = new WebHookStruct();
    this.defeatMessage = new WebHookStruct();
    this.catchMessage = new WebHookStruct();
    this.tradeMessage = new WebHookStruct();
  }
}
