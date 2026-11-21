package com.kingpixel.ultranotify.api.action;

import lombok.Getter;

/**
 * Immutable result representation for CobbleNotify headless actions.
 */
@Getter
public class NotifyActionResult {

  public enum Status {
    SUCCESS,
    RULE_NOT_FOUND,
    INVALID_ARGUMENT,
    WORLD_BLACKLISTED,
    POKEMON_BLACKLISTED,
    PREFERENCE_UPDATED,
    NO_PERMISSION,
    PLAYER_OFFLINE,
    DATABASE_UNAVAILABLE,
    ERROR
  }

  private final boolean success;
  private final Status status;
  private final String message;
  private final String ruleId;

  private NotifyActionResult(boolean success, Status status, String message, String ruleId) {
    this.success = success;
    this.status = status != null ? status : Status.ERROR;
    this.message = message != null ? message : "";
    this.ruleId = ruleId != null ? ruleId : "";
  }

  public static NotifyActionResult success(String message) {
    return new NotifyActionResult(true, Status.SUCCESS, message, "");
  }

  public static NotifyActionResult success(String message, String ruleId) {
    return new NotifyActionResult(true, Status.SUCCESS, message, ruleId);
  }

  public static NotifyActionResult preferenceUpdated(String message, String ruleId) {
    return new NotifyActionResult(true, Status.PREFERENCE_UPDATED, message, ruleId);
  }

  public static NotifyActionResult failure(Status status, String message) {
    return new NotifyActionResult(false, status, message, "");
  }

  public static NotifyActionResult failure(Status status, String message, String ruleId) {
    return new NotifyActionResult(false, status, message, ruleId);
  }

  public static NotifyActionResult ruleNotFound(String ruleId) {
    return failure(Status.RULE_NOT_FOUND, "Notification rule not found: " + (ruleId != null ? ruleId : ""), ruleId);
  }

  public static NotifyActionResult invalidArgument(String message) {
    return failure(Status.INVALID_ARGUMENT, message);
  }

  public static NotifyActionResult offline(String message) {
    return failure(Status.PLAYER_OFFLINE, message);
  }

  public static NotifyActionResult noPermission(String message) {
    return failure(Status.NO_PERMISSION, message);
  }
}
