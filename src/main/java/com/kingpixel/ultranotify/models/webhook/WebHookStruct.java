package com.kingpixel.ultranotify.models.webhook;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobbleutils.CobbleUtils;
import com.kingpixel.ultranotify.UltraNotify;
import lombok.Data;

import java.awt.Color;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import static com.kingpixel.cobbleutils.Model.discord.WebHookStruct.getGif;

/**
 * @author Carlos Varas Alonso - 24/11/2025 4:17
 */
@Data
public class WebHookStruct {
  public static final ExecutorService WEBHOOK_EXECUTOR = UltraNotify.getAsyncContext().getExecutor();
  private String color;
  private String title;
  private String titleUrl;
  private List<String> description;
  private String footer;
  private String footerIconUrl;

  public WebHookStruct() {
    this.color = "#25D293";
    this.title = "CobbleNotify WebHook";
    this.description = List.of(
      "Pokemon: %pokemon%",
      "Level: %level%",
      "Types: %types%",
      "Form: %form%",
      "Gender: %gender%",
      "FriendShip: %friendship%",
      "Tradeable: %tradeable%",
      "Breedable: %breedable%",
      "Nature: %nature% (↑%up%/↓%down%)",
      "Ability: %ability% %ha%",
      "IVS: %ivs%&7/&e31",
      " HP: %ivshp% (%htivshp%) ATK: %ivsatk% (%htivsatk%) DEF: %ivsdef% (%htivsdef%)",
      " SP_ATK: %ivsspa% (%htivsspa%) SP_DEF: %ivsspdef% (%htivsspdef%) SPEED: %ivsspeed% (%htivsspeed%)",
      "EVS: &e%evs%&7/&e510",
      " HP: %evshp% ATK: %evsatk% DEF: %evsdef%",
      " SP_ATK: %evsspa%  SP_DEF: %evsspdef% SPEED: %evsspeed%",
      "Ball: %ball%",
      "Size: %size%",
      "Held Item: %item%",
      "Moves: ",
      " - %move1%",
      " - %move2%",
      " - %move3%",
      " - %move4%",
      "Country: %country%"
    );
    this.footer = "CobbleNotify Footer";
  }

  public WebHookStruct(String color, String title, List<String> description, String footer) {
    this.color = color;
    this.title = title;
    this.description = description;
    this.footer = footer;
  }

  public void sendMessage(String modifiedTitle, List<String> modifiedDescription, String modifiedFooter, Pokemon pokemon) {
    try {
      var webHookData = UltraNotify.config.getWebHookData();
      if (webHookData == null || !webHookData.isENABLED()) return;
      var client = webHookData.getWebhookClient();
      if (client == null) {
        UltraNotify.LOGGER.warn("Discord webhook is enabled, but webhook client is null. Please check webhook URL configuration.");
        return;
      }
      String gif = getGif(pokemon);
      int rgbColor;
      try {
        String hexColor = (color != null && !color.isBlank()) ? (color.startsWith("#") ? color : "#" + color) : "#25D293";
        rgbColor = Color.decode(hexColor).getRGB() & 0xFFFFFF;
      } catch (Exception e) {
        rgbColor = 0x25D293;
      }
      String displayTitle = modifiedTitle != null ? modifiedTitle : (title != null ? title : "");
      String displayFooter = modifiedFooter != null ? modifiedFooter : (footer != null ? footer : "");
      List<String> descList = modifiedDescription != null ? modifiedDescription : (description != null ? description : List.of());

      var message = new WebhookMessageBuilder()
        .addEmbeds(
          new WebhookEmbedBuilder()
            .setTitle(new WebhookEmbed.EmbedTitle(displayTitle, titleUrl))
            .setColor(rgbColor)
            .setThumbnailUrl(gif)
            .setDescription(String.join("\n", descList))
            .setFooter(new WebhookEmbed.EmbedFooter(displayFooter, gif))
            .build()
        )
        .build();
      client.send(message);
    } catch (Exception e) {
      UltraNotify.LOGGER.error("Error building or sending webhook message: {}", e.getMessage(), e);
    }
  }

  public void sendMessage(List<String> modifiedDescription, Pokemon pokemon) {
    sendMessage(title, modifiedDescription, footer, pokemon);
  }

  public static void runAsync(Runnable runnable) {
    CompletableFuture.runAsync(runnable, WEBHOOK_EXECUTOR)
      .orTimeout(5, TimeUnit.SECONDS)
      .exceptionally(ex -> {
        UltraNotify.LOGGER.error("Error executing webhook task: {}", ex.getMessage(), ex);
        return null;
      });
  }

}
