package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Commands.StartCommand;
import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.UHC.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class GameBossBars implements Listener {
    private final NamespacedKey progressBossBarKey = new NamespacedKey(Main.plugin, "uhc_progress");

    @EventHandler
    public void onSurvivalGameStart(SurvivalGameStartEvent e) {
        // Create the step progress boss bar
        BossBar borderBossBar = Bukkit.getServer().createBossBar(progressBossBarKey, "Bordure", BarColor.RED, BarStyle.SOLID);
        borderBossBar.setVisible(true);
        borderBossBar.setProgress(0);

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            // Create player bossbar
            BossBar playerBossBar = Bukkit.getServer().createBossBar(new NamespacedKey(Main.plugin, player.getName()), "", BarColor.WHITE, BarStyle.SOLID);
            playerBossBar.setVisible(true);
            playerBossBar.setProgress(0);

            playerBossBar.addPlayer(player);

            // Add bossbar(s) to player
            borderBossBar.addPlayer(player);
        }
    }

    @EventHandler
    public void onSurvivalGameTick(SurvivalGameTickEvent e) {
        double borderSize = Bukkit.getServer().getWorld(e.getGame().getWorldName()).getWorldBorder().getSize();
        double bossBarPercentage = borderSize / (float)e.getGame().getInitialBorderRadius();
        double borderRadius = borderSize / 2;

        // Border bossbar
        BossBar bossBar = Bukkit.getServer().getBossBar(progressBossBarKey);
        if (bossBar != null) {
            bossBar.setProgress(bossBarPercentage);
            bossBar.setTitle(String.format("Bordure : %.0f", borderRadius));
        }

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            // Player bossbar
            BossBar playerBossBar = Bukkit.getServer().getBossBar(new NamespacedKey(Main.plugin, player.getName()));

            if (playerBossBar != null) {
                Location loc = player.getLocation();
                playerBossBar.setTitle(String.format("X : %.0f Z : %.0f", loc.getX(), loc.getZ()));

                double highestPos = Math.max(Math.abs(loc.getX()), Math.abs(loc.getZ()));
                double progress = Math.min(Math.max(highestPos / borderRadius, 0), 1);
                playerBossBar.setProgress(progress);
            }
        }
    }

    @EventHandler
    public void onSurvivalGameEnd(SurvivalGameEndEvent e) {
        // Remove border bossbar
        BossBar bossBar = Bukkit.getServer().getBossBar(progressBossBarKey);
        if (bossBar != null) bossBar.removeAll();
        Bukkit.getServer().removeBossBar(progressBossBarKey);

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            // Remove player bossbar
            NamespacedKey playerKey = new NamespacedKey(Main.plugin, player.getName());

            BossBar playerBossBar = Bukkit.getServer().getBossBar(playerKey);
            if (playerBossBar != null) playerBossBar.removeAll();
            Bukkit.getServer().removeBossBar(playerKey);
        }
    }
}
