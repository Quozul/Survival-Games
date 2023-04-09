package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class GameTick implements Listener {
    @EventHandler
    public void onSurvivalGameTick(SurvivalGameTickEvent event) {
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            // Update scoreboard
            Scoreboard board = player.getScoreboard();

            Objective obj = board.getObjective("UHCInfo");

            int currentBorderRadius = (int) Math.floor(player.getWorld().getWorldBorder().getSize() / 2);
            Score borderRadius = obj.getScore("§7» Rayon");
            borderRadius.setScore(currentBorderRadius);

            player.setScoreboard(board);
        }
    }
}
