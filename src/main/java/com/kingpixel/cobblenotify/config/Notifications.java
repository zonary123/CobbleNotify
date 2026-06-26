package com.kingpixel.cobblenotify.config;

import com.google.gson.Gson;
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
  private static final Gson GSON = Utils.newGson();

  private Notifications() {
    throw new IllegalStateException("Utility class");
  }

  public static void init() {
    NOTIFICATIONS.clear();
    File folder = Utils.getAbsolutePath(PATH);
    if (!folder.exists() && !folder.mkdirs()) {
      CobbleUtils.LOGGER.error(CobbleNotify.MOD_ID, "Failed to create notifications folder: " + folder.getAbsolutePath());
      return;
    }

    var files = Utils.getFiles(folder).stream()
      .filter(File::isFile)
      .filter(f -> f.getName().endsWith(".json"))
      .toList();

    if (!files.isEmpty()) {
      CobbleUtils.LOGGER.info(CobbleNotify.MOD_ID, "Loading " + files.size() + " notification files... File path: " + folder.getAbsolutePath());
      for (File f : files) {
        try {
          var notification = GSON.fromJson(Utils.readFileSync(f), Notification.class);
          if (notification == null) {
            notification = new Notification(f.getName().replace(".json", ""));
            CobbleUtils.LOGGER.warn(CobbleNotify.MOD_ID, "Notification file returned null. Regenerating defaults for: " + f.getName());
          }

          notification.check();
          addOrReplaceNotification(notification);
          Utils.writeFileAsync(f, GSON.toJson(notification));
        } catch (Exception e) {
          CobbleUtils.LOGGER.error(CobbleNotify.MOD_ID, "Error loading notification file " + f.getName() + ": " + e.getMessage());
        }
      }
    } else {
      writeNotificationFile(createLegendaryDefault(), "legendary_notification.json");
      writeNotificationFile(createShinyDefault(), "shiny_notification.json");
      writeNotificationFile(createBossDefault(), "boss_notification.json");
    }

    NOTIFICATIONS.sort((n1, n2) -> Integer.compare(n2.getPriority(), n1.getPriority()));
  }

  private static Notification createLegendaryDefault() {
    var notification = new Notification("legendary");
    notification.getFilter().getLabels().add("legendary");
    notification.setPriority(10);
    notification.getNotificationOptions().setSpawn(true);
    notification.getNotificationOptions().setCaught(true);
    notification.getNotificationOptions().setDefeat(true);
    notification.getNotificationOptions().setTrade(false);
    setDefaultMessages(notification, "Legendary");
    return notification;
  }

  private static Notification createShinyDefault() {
    var notification = new Notification("shiny");
    notification.getFilter().getAspects().add("shiny");
    notification.setPriority(5);
    notification.getNotificationOptions().setSpawn(true);
    notification.getNotificationOptions().setCaught(true);
    notification.getNotificationOptions().setDefeat(false);
    notification.getNotificationOptions().setTrade(false);
    setDefaultMessages(notification, "Shiny");
    return notification;
  }

  private static Notification createBossDefault() {
    var notification = new Notification("boss");
    notification.setPriority(15);
    notification.getFilter().getPersistentDataMap().clear();
    notification.getFilter().getPersistentDataMap().put("boss", List.of("default", "common", "*"));
    notification.getNotificationOptions().setSpawn(true);
    notification.getNotificationOptions().setCaught(true);
    notification.getNotificationOptions().setDefeat(true);
    notification.getNotificationOptions().setTrade(false);
    setDefaultMessages(notification, "Boss");
    return notification;
  }

  private static void setDefaultMessages(Notification notification, String category) {
    var messages = notification.getNotificationOptions().getNotificationMessages();
    messages.getSpawnMessage().setRawMessage(
      "cb:%prefix% <#F2B78A>[" + category + "] <#94C3E3>%pokemon% <#F2B78A>spawned at " +
        "<#DE9157>x:<#FFFFFF>%x% <#DE9157>y:<#FFFFFF>%y% <#DE9157>z:<#FFFFFF>%z% " +
        "<#F2B78A>in <#CEF2AC>%world% <#F2B78A>(<#CEF2AC>%biome%<#F2B78A>)."
    );
    messages.getCatchMessage().setRawMessage(
      "cb:%prefix% <#F2B78A>[" + category + "] <#DE8D2A>%player% <#F2B78A>caught <#94C3E3>%pokemon%<#F2B78A>!"
    );
    messages.getDefeatMessage().setRawMessage(
      "cb:%prefix% <#F2B78A>[" + category + "] <#DE8D2A>%player% <#F2B78A>defeated <#94C3E3>%pokemon%<#F2B78A>!"
    );
    messages.getTradeMessage().setRawMessage(
      "cb:%prefix% <#F2B78A>[" + category + "] <#DE8D2A>%player1% <#F2B78A>traded <#94C3E3>%pokemon1% " +
        "<#F2B78A>with <#DE8D2A>%player2% <#F2B78A>for <#94C3E3>%pokemon2%<#F2B78A>."
    );
  }

  private static void addOrReplaceNotification(Notification notification) {
    String identifier = notification.getIdentifier() == null ? "" : notification.getIdentifier();
    for (int i = 0; i < NOTIFICATIONS.size(); i++) {
      Notification current = NOTIFICATIONS.get(i);
      if (current.getIdentifier() != null && current.getIdentifier().equalsIgnoreCase(identifier)) {
        if (notification.getPriority() >= current.getPriority()) {
          NOTIFICATIONS.set(i, notification);
        }
        return;
      }
    }
    NOTIFICATIONS.add(notification);
  }

  private static void writeNotificationFile(Notification notification, String fileName) {
    notification.check();
    addOrReplaceNotification(notification);
    var file = Utils.getAbsolutePath(PATH + fileName);
    CobbleUtils.LOGGER.info(CobbleNotify.MOD_ID, "Creating default notification file at " + file);
    Utils.writeFileAsync(file, GSON.toJson(notification));
  }
}
