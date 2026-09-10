package com.kingpixel.ultranotify.models.webhook;

import com.cobblemon.mod.common.entity.pokemon.PokemonEntity;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import com.kingpixel.ultranotify.models.Notification;
import com.kingpixel.ultranotify.models.enums.Actions;
import lombok.Data;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

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

  public boolean sendMessage(Actions action, List<Pokemon> pokemons, @Nullable ServerPlayerEntity player, @Nullable PokemonEntity pokemonEntity) {
    boolean active = isActive(action);
    if (active) {
      WebHookStruct.runAsync(() -> {
        WebHookStruct message = getMessage(action);
        Pokemon pokemon = pokemons.getFirst();
        String playerName = player != null ? player.getGameProfile().getName() : "Unknown";

        List<String> description = new ArrayList<>(message.getDescription());
        String descriptionJoined = String.join("\n", description);
        descriptionJoined = formatPlaceholders(descriptionJoined, playerName, pokemonEntity, pokemon, pokemons);

        String formattedTitle = formatPlaceholders(message.getTitle(), playerName, pokemonEntity, pokemon, pokemons);
        String formattedFooter = formatPlaceholders(message.getFooter(), playerName, pokemonEntity, pokemon, pokemons);

        message.sendMessage(
            replace(formattedTitle),
            List.of(replace(descriptionJoined)),
            replace(formattedFooter),
            pokemon
        );
      });
    }
    return active;
  }

  private String formatPlaceholders(String text, String playerName, @Nullable PokemonEntity pokemonEntity, Pokemon pokemon, List<Pokemon> pokemons) {
    if (text == null || text.isBlank()) return text;
    text = text.replace("%player%", playerName);
    text = text.replace("%nearest%", playerName);
    text = Notification.replaceVariables(pokemonEntity, pokemon, text);
    text = PokemonUtils.replace(text, pokemons);
    return text;
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

  // Replaces
  private static final Pattern REGEX_KYORI = Pattern.compile(
    "<[^>]*>|[&§][0-9A-FK-ORa-fk-or]"
  );

  private String replace(String content) {
    if (content == null) return null;
    return REGEX_KYORI.matcher(content).replaceAll("");
  }


}
