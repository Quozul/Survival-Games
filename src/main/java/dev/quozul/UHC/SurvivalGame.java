package dev.quozul.UHC;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SurvivalGame {
    private int gameTime;
    private final int gameDuration;
    private final int borderRadius;
    private final String worldName;
    private final String defaultWorldName;

    private int task;
    private int startTask;
    private final int interval = 20;
    private int startTime;

    public boolean evaluateUHC() {
        int playersAlive = 0;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getHealth() > 0 && player.getGameMode() == GameMode.SURVIVAL) playersAlive++;
        }

        if (playersAlive <= 1)
            return true;

        return false;
    }

    /**
     * Create a new survival game
     * @param d Game duration in minutes
     * @param r Radius of the border in blocks
     */
    public SurvivalGame(int d, int r) {
        this.gameTime = 0;
        this.gameDuration = d * 60 * 20;
        this.borderRadius = r;
        this.worldName = Main.plugin.getConfig().getString("game-worldname");
        this.defaultWorldName = Main.plugin.getConfig().getString("server-worldname");

        final int startCooldown = Main.plugin.getConfig().getInt("start-cooldown");
        this.startTime = 0;

        this.startTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
            this.startTime++;

            for (Player player : Bukkit.getServer().getOnlinePlayers())
                player.sendTitle("§6§lDébut dans", String.format("§7%d secondes", startCooldown - this.startTime), 0, 30, 0);

            if (this.startTime >= startCooldown) {
                Bukkit.getPluginManager().callEvent(new SurvivalGameStartEvent(this));

                this.task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {

                    this.gameTime += interval;

                    Bukkit.getPluginManager().callEvent(new SurvivalGameTickEvent(this));

                    if (this.gameTime >= this.gameDuration) {
                        if (evaluateUHC())
                            this.endGame();
                    }

                }, 0, interval);

                Bukkit.getServer().getScheduler().cancelTask(this.startTask);
            }

        }, 0, interval);
    }

    public void endGame() {
        Bukkit.getServer().getScheduler().cancelTask(this.task);
        Bukkit.getPluginManager().callEvent(new SurvivalGameEndEvent(this));
    }

    public String getWorldName() {
        return this.worldName;
    }

    public String getDefaultWorldName() {
        return this.defaultWorldName;
    }

    /**
     * @return List of worlds used by the game
     */
    public List<World> getWorlds() {
        List<World> worlds = new ArrayList<>();
        worlds.add(Bukkit.getServer().getWorld(this.worldName));
        worlds.add(Bukkit.getServer().getWorld(this.worldName + "_nether"));

        return worlds;
    }

    /**
     * @return Total game duration in Minecraft ticks
     */
    public int getGameDuration() {
        return this.gameDuration;
    }

    /**
     * @return Initial border radius
     */
    public int getInitialBorderRadius() {
        return this.borderRadius;
    }

    /**
     * @return Current game time in Minecraft ticks
     */
    public int getGameTime() {
        return this.gameTime;
    }
}
