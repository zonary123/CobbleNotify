package com.kingpixel.ultranotify.models.notification;

import lombok.Data;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:41
 */
@Data
public class NotificationOptions {
  private boolean caught;
  private boolean defeat;
  private boolean spawn;
  private boolean trade;
  private NotificationMessages notificationMessages;

  public NotificationOptions() {
    this.caught = true;
    this.defeat = true;
    this.spawn = true;
    this.trade = true;
    this.notificationMessages = new NotificationMessages();
  }
}
