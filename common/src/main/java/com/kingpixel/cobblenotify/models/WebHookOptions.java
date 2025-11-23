package com.kingpixel.cobblenotify.models;

import lombok.Data;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:40
 */
@Data
public class WebHookOptions {
  private boolean enabled;
  private boolean caught;
  private boolean defeat;
  private boolean spawn;
  private boolean trade;
  private WebHookMessages webHookMessages;

  public WebHookOptions() {
    this.enabled = false;
    this.caught = false;
    this.defeat = false;
    this.spawn = false;
    this.trade = false;
    this.webHookMessages = new WebHookMessages();
  }
}
