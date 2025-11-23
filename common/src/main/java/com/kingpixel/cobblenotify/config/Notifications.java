package com.kingpixel.cobblenotify.config;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.models.Notification;
import com.kingpixel.cobbleutils.util.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:49
 */
public class Notifications {
  public static final List<Notification> NOTIFICATIONS = new ArrayList<>();
  public static final String PATH = CobbleNotify.PATH + "notifications/";

  public static void init() {
    NOTIFICATIONS.clear();
    File file = new File(PATH);
    if (!file.exists()) {
      file.mkdirs();
    }
    var files = Utils.getFiles(file);
    for (File f : files) {
      try {
        var notification = Utils.newGson().fromJson(Utils.readFileSync(f), Notification.class);
        if (notification != null) {
          notification.check();
          NOTIFICATIONS.add(notification);
          Utils.writeFileAsync(f, Utils.newGson().toJson(notification));
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
