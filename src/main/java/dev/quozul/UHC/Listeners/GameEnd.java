package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Commands.StartCommand;
import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Main;
import org.bukkit.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameEnd implements Listener {
    @EventHandler
    public void onSurvivalGameEnd(SurvivalGameEndEvent e) {
        Player winner = null;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);

            player.sendTitle("§6Bien joué !", "", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

            // Get winner
            if (!player.getScoreboardTags().contains("died") && player.getScoreboardTags().contains("playing"))
                winner = player;

            player.getScoreboardTags().remove("playing");

            player.getInventory().clear();
        }

        if (winner != null)
            Bukkit.broadcastMessage("§6§lVainqueur : §7" + winner.getDisplayName());

        // Teleport players back to spawn after 10 seconds
        Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                World world = Bukkit.getWorld(e.getGame().getDefaultWorldName());
                Location loc = world.getSpawnLocation();

                player.teleport(loc);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }, 200);
    }
}
