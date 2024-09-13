package com.kingpixel.cobblenotify.events;

import com.cobblemon.mod.common.api.Priority;
import com.cobblemon.mod.common.api.events.CobblemonEvents;
import com.cobblemon.mod.common.pokemon.Pokemon;
import com.kingpixel.cobblenotify.CobbleNotify;
import com.kingpixel.cobblenotify.utils.NotifyUtils;
import com.kingpixel.cobbleutils.util.PokemonUtils;
import kotlin.Unit;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.List;

/**
 * @author Carlos Varas Alonso - 26/05/2024 2:46
 */
public class TradeEvent {
  public static void registerEvents() {
    CobblemonEvents.TRADE_COMPLETED.subscribe(Priority.NORMAL, (evt) -> {
      try {
        if (!CobbleNotify.config.isNotifytrades()) return Unit.INSTANCE;
        ServerPlayer player1 = CobbleNotify.server.getPlayerList().getPlayer(evt.getTradeParticipant1().getUuid());
        ServerPlayer player2 = CobbleNotify.server.getPlayerList().getPlayer(evt.getTradeParticipant2().getUuid());
        Pokemon pokemon1 = evt.getTradeParticipant2Pokemon();
        Pokemon pokemon2 = evt.getTradeParticipant1Pokemon();
        assert player1 != null;
        assert player2 != null;
        String message = "";
        if ((pokemon1.isLegendary() || pokemon2.isLegendary())) {
          message = notifyString(CobbleNotify.language.getMessagetradelegendary(), player1, player2, pokemon1, pokemon2);
        } else if (pokemon1.getShiny() || pokemon2.getShiny()) {
          message = notifyString(CobbleNotify.language.getMessagetradeshiny(), player1, player2, pokemon1, pokemon2);
        } else {
          message = notifyString(CobbleNotify.language.getMessagetradenormal(), player1, player2, pokemon1, pokemon2);
        }

        NotifyUtils.broadcast(message);
      } catch (Exception e) {
        // Aquí puedes manejar el error como prefieras. Por ejemplo, puedes imprimir el error en la consola:
        System.err.println("Se produjo un error al procesar el evento de intercambio de Pokemon: " + e.getMessage());
        e.printStackTrace();
      }
      return Unit.INSTANCE;
    });
  }

  private static void notifyplayers(List<Player> players, String message) {
    for (Player player : players) {
      NotifyUtils.adventure((ServerPlayer) player, message);
    }

  }

  private static String notifyString(String texto, ServerPlayer player1, ServerPlayer player2, Pokemon pokemon1,
                                     Pokemon pokemon2) {
    List<Pokemon> pokemonList = List.of(pokemon1, pokemon2);
    return PokemonUtils.replace(texto.replace("%player1%", player1.getName().getString())
      .replace("%player2%", player2.getName().getString()), pokemonList);
  }
}
