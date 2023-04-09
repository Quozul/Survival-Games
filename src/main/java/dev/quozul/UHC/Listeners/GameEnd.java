package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Main;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class GameEnd implements Listener {
    @EventHandler
    public void onSurvivalGameEnd(SurvivalGameEndEvent event) {
        Player winner = null;

        for (Player player : event.getGame().getPlayers()) {
            player.setGameMode(GameMode.SPECTATOR);

            player.sendTitle("§6Bien joué !", "", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);

            // Get winner
            if (!player.getScoreboardTags().contains("died") && player.getScoreboardTags().contains("playing"))
                winner = player;

            player.getInventory().clear();
        }

        if (winner != null) {
            Bukkit.broadcastMessage("§6§lVainqueur : §7" + winner.getDisplayName());
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
