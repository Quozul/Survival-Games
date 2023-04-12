package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.UHC.SurvivalGame;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class GameBossBars implements Listener {
    @EventHandler
    public void onSurvivalGameStart(SurvivalGameStartEvent event) {
        SurvivalGame game = event.getGame();
        game.showBossBar(game.borderBossBar.progress(0));
    }

    @EventHandler
    public void onSurvivalGameTick(SurvivalGameTickEvent event) {
        double borderSize = event.getGame().getWorld().getWorldBorder().getSize();
        double bossBarPercentage = borderSize / (float) event.getGame().getInitialBorderRadius();
        double borderRadius = borderSize / 2;

        net.kyori.adventure.bossbar.BossBar bar = event.getGame().borderBossBar;
        bar.progress((float) bossBarPercentage);
        bar.name(Component.text(String.format("Bordure : %.0f", borderRadius)));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSurvivalGameEnd(SurvivalGameEndEvent event) {
        SurvivalGame game = event.getGame();
        game.hideBossBar(game.borderBossBar.progress(0));
    }
}
