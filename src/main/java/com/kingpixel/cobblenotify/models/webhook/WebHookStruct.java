package com.kingpixel.cobblenotify.models.webhook;

import club.minnced.discord.webhook.send.WebhookEmbed;
import club.minnced.discord.webhook.send.WebhookEmbedBuilder;
import club.minnced.discord.webhook.send.WebhookMessageBuilder;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobbleutils.CobbleUtils;
import lombok.Data;

import java.awt.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author Carlos Varas Alonso - 24/11/2025 4:17
 */
@Data
public class WebHookStruct {
  public static final ExecutorService WEBHOOK_EXECUTOR = Executors.newFixedThreadPool(2);
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
      "Types: &f%types%",
      "Form: &f%form%",
      "Gender: %gender%",
      "FriendShip: &f%friendship%",
      "Tradeable: &f%tradeable%",
      "Breedable: &f%breedable%",
      "Nature: &f%nature% &f(&a↑%up%&f/&c↓%down%&f)",
      "Ability: &f%ability% %ha%",
      "IVS: &e%ivs%&7/&e31",
      " HP: &f%ivshp% (%htivshp%) ATK: &f%ivsatk% (%htivsatk%) DEF: &f%ivsdef% (%htivsdef%)",
      " SP_ATK: &f%ivsspa% (%htivsspa%) SP_DEF: &f%ivsspdef% (%htivsspdef%) SPEED: &f%ivsspeed% (%htivsspeed%)",
      "EVS: &e%evs%&7/&e510",
      " HP: &f%evshp% ATK: &f%evsatk% DEF: &f%evsdef%",
      " SP_ATK: &f%evsspa%  SP_DEF: &f%evsspdef% SPEED: &f%evsspeed%",
      "Ball: &f%ball%",
      "Size: &f%size%",
      "Held Item: &f%item%",
      "Moves: ",
      " - %move1%",
      " - %move2%",
      " - %move3%",
      " - %move4%",
      "Country: &f%country%"
    );
    this.footer = "CobbleNotify Footer";
  }

  public WebHookStruct(String color, String title, List<String> description, String footer) {
    this.color = color;
    this.title = title;
    this.description = description;
    this.footer = footer;
  }

  public void sendMessage(List<String> modifiedDescription, Pokemon pokemon) {
    var webHookData = CobbleNotify.config.getWebHookData();
    if (!webHookData.isENABLED()) return;
    var client = webHookData.getWebhookClient();
    String gif = com.kingpixel.cobbleutils.Model.discord.WebHookStruct.getGif(pokemon);
    var message = new WebhookMessageBuilder()
      .addEmbeds(
        new WebhookEmbedBuilder()
          .setTitle(new WebhookEmbed.EmbedTitle(title, titleUrl))
          .setColor(Color.decode(color).getRGB() & 0xFFFFFF)
          .setThumbnailUrl(gif)
          .setDescription(String.join("\n", modifiedDescription))
          .setFooter(new WebhookEmbed.EmbedFooter(footer, gif))
          .build()
      )
      .build();
    client.send(message);

  }

  public static void runAsync(Runnable runnable) {
    CompletableFuture.runAsync(runnable, WEBHOOK_EXECUTOR)
      .orTimeout(5, TimeUnit.SECONDS)
      .exceptionally(ex -> {
        CobbleUtils.LOGGER.error(CobbleNotify.MOD_ID, "Error executing webhook task: " + ex.getMessage());
        ex.printStackTrace();
        return null;
      });
  }

}
