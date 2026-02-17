package com.kingpixel.cobblenotify.models.webhook;

import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.models.Notification;
import com.kingpixel.cobblenotify.models.enums.Actions;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

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

  public boolean sendMessage(Actions action, List<Pokemon> pokemons) {
    boolean active = isActive(action);
    if (active) {
      WebHookStruct.runAsync(() -> {
        WebHookStruct message = getMessage(action);
        Pokemon pokemon = pokemons.getFirst();
        List<String> description = new ArrayList<>(message.getDescription());
        String descriptionJoined = String.join("\n", description);
        descriptionJoined = Notification.replaceVariables(pokemon.getEntity(), descriptionJoined);
        descriptionJoined = PokemonUtils.replace(descriptionJoined, pokemons);
        message.sendMessage(List.of(descriptionJoined), pokemon);
      });
    }
    return active;
  }

  private boolean isActive(Actions action) {
    return switch (action) {
      case CAUGHT -> caught;
      case DEFEAT -> defeat;
      case SPAWN -> spawn;
      case TRADE -> trade;
    };
  }

  private WebHookStruct getMessage(Actions action) {
    return switch (action) {
      case CAUGHT -> webHookMessages.getCatchMessage();
      case DEFEAT -> webHookMessages.getDefeatMessage();
      case SPAWN -> webHookMessages.getSpawnMessage();
      case TRADE -> webHookMessages.getTradeMessage();
    };
  }


}
