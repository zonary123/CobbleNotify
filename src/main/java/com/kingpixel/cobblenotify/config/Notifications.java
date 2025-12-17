package com.kingpixel.cobblenotify.config;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.models.Notification;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.cobbleutils.util.Utils;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:49
 */
@Data
public class Notifications {
  public static final List<Notification> NOTIFICATIONS = new ArrayList<>();
  public static final String PATH = CobbleNotify.PATH + "notifications/";

  public static void init() {
    NOTIFICATIONS.clear();
    File file = Utils.getAbsolutePath(PATH);
    if (!file.exists()) {
      file.mkdirs();
    }
    var files = Utils.getFiles(file);
    if (!files.isEmpty()) {
      CobbleUtils.LOGGER.info(CobbleNotify.MOD_ID, "Loading " + files.size() + " notification files... File path: " + file.getAbsolutePath());
      for (File f : files) {
        try {
          var notification = Utils.newGson().fromJson(Utils.readFileSync(f), Notification.class);
          if (notification != null) {
            notification.check();
            NOTIFICATIONS.add(notification);
            Utils.writeFileAsync(f, Utils.newGson().toJson(notification));
          } else {
            Utils.writeFileAsync(f, Utils.newGson().toJson(new Notification(f.getName().replace(".json", ""))));
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    } else {
      // Shiny Notification
      var shinyNotification = new Notification("shiny");
      shinyNotification.getFilter().getAspects().add("shiny");
      writeNotificationFile(shinyNotification, "shiny_notification.json");
      // Legendary Notification
      var legendaryNotification = new Notification("legendary");
      legendaryNotification.getFilter().getLabels().add("legendary");
      writeNotificationFile(legendaryNotification, "legendary_notification.json");
      // Boss Notification
      var bossNotification = new Notification("boss");
      bossNotification.getFilter().getPersistentDataMap().clear();
      bossNotification.getFilter().getPersistentDataMap().put("boss", List.of("default", "common", "*"));
      writeNotificationFile(bossNotification, "boss_notification.json");
    }
    NOTIFICATIONS.sort((n1, n2) -> Integer.compare(n2.getPriority(), n1.getPriority()));
  }

  private static void writeNotificationFile(Notification notification, String fileName) {
    notification.check();
    NOTIFICATIONS.add(notification);
    var file1 = Utils.getAbsolutePath(PATH + fileName);
    CobbleUtils.LOGGER.info(CobbleNotify.MOD_ID, "Creating default notification file at " + file1);
    Utils.writeFileAsync(file1, Utils.newGson().toJson(notification));
  }
}
