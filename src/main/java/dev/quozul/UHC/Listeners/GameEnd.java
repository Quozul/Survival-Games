package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Main;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;


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

            // Get winner
            if (!player.getScoreboardTags().contains("died") && player.getScoreboardTags().contains("playing"))
                winner = player;

            player.getInventory().clear();
        }

        if (winner != null) {
            Component component = Component.text("Vainqueur : ", NamedTextColor.GRAY)
                    .append(winner.displayName().color(NamedTextColor.GOLD));
            event.getGame().sendMessage(component);
        }

        // Teleport players back to spawn after 10 seconds
        Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, () -> {
            List<? extends Player> players = event.getGame().getPlayers();
            for (Player player : players) {
                World world = Bukkit.getWorld(event.getGame().getDefaultWorldName());
                Location loc = world.getSpawnLocation();

                player.teleport(loc);
                player.setGameMode(GameMode.ADVENTURE);

                player.getScoreboardTags().remove("playing");
            }
        }, 200);
    }
}
