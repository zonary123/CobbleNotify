package com.kingpixel.cobblenotify.config;

import lombok.Data;

/**
 * @author Carlos Varas Alonso - 23/11/2025 19:46
 */
@Data
public class Config {
  private boolean debug = false;
  private boolean affectCommand = true;

  public void init() {

  }
}
