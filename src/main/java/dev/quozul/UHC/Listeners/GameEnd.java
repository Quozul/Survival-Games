package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.SurvivalGameData;
import dev.quozul.minigame.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class GameEnd implements Listener {
    @EventHandler
    public void onSurvivalGameEnd(SurvivalGameEndEvent event) {
        Player winner = null;

        Component mainTitle = Component.text("Bien joué !", NamedTextColor.GOLD);
        Title title = Title.title(mainTitle, Component.empty());
        event.getGame().showTitle(title);

        event.getGame().playSound(Sound.sound(Key.key("minecraft:entity.ender_dragon.death"), Sound.Source.MASTER, 1, 1));

        for (Player player : event.getGame().getPlayers()) {
            player.setGameMode(GameMode.SPECTATOR);
            player.getInventory().clear();

            // Get winner
            if (PlayerData.from(player).getGameData() instanceof SurvivalGameData data && data.isAlive()) {
                winner = player;
            }
        }

        if (winner != null) {
            Component component = Component.text("Vainqueur : ", NamedTextColor.GRAY)
                    .append(winner.displayName().color(NamedTextColor.GOLD));
            event.getGame().sendMessage(component);
        }
    }
}
