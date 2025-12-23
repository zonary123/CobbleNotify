package com.kingpixel.cobblenotify.models;

import lombok.Data;
import net.minecraft.world.World;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Carlos Varas Alonso - 23/12/2025 21:24
 */
@Data
public class WorldFilter {
  private Set<String> blacklistWorlds;
  private Set<String> whitelistWorlds;

  public WorldFilter() {
    this.blacklistWorlds = new HashSet<>();
    this.whitelistWorlds = new HashSet<>();
  }

  public boolean isBlackListed(World world) {
    return isBlackListed(world.getRegistryKey().getValue().toString());
  }

  public boolean isBlackListed(String world) {
    if (whitelistWorlds != null && !whitelistWorlds.isEmpty()) {
      return !whitelistWorlds.contains(world);
    }
    if (blacklistWorlds != null && !blacklistWorlds.isEmpty()) {
      return blacklistWorlds.contains(world);
    }
    return false;
  }
}
