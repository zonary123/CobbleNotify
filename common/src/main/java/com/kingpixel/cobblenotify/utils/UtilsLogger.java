package com.kingpixel.cobblenotify.utils;

import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.util.Utils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.CompletableFuture;

/**
 * Class for logging.
 */
public class UtilsLogger {
  private Logger logger; // Log for the console.
  private String prefix = "[" + CobbleNotify.MOD_NAME + "] ";

  // Enums used for the log file.
  private enum Level {
    INFO,
    ERROR,
    FATAL
  }

  // Constructor that creates the logger.
  public UtilsLogger() {
    logger = LogManager.getLogger(CobbleNotify.MOD_NAME);
  }

  /**
   * Info log method.
   *
   * @param message The message to log.
   */
  public void info(String message) {
    logger.info(prefix + message);
//		write(Level.INFO, message);
  }

  /**
   * Error log method.
   *
   * @param message The message to log.
   */
  public void error(String message) {
    logger.error(prefix + message);
//		write(Level.ERROR, message);
  }

  /**
   * Fatal log method.
   *
   * @param message The message to log.
   */
  public void fatal(String message) {
    logger.fatal(prefix + message);
//		write(Level.FATAL, message);
  }

  /**
   * Write method to save the logs to file.
   *
   * @param level   The level that the log is (INFO, ERROR or FATAL).
   * @param message The message to log.
   */
  private void write(Level level, String message) {
    // TODO Can't append to file.

    String output = prefix + "[" + level + "]: " + message;

    CompletableFuture<Boolean> future = Utils.writeFileAsync(CobbleNotify.PATH, "logs.txt", output);

    System.out.println("GTS WRITE FUTURE: " + future.join());
  }
}
