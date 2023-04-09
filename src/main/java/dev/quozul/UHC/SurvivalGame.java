package dev.quozul.UHC;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

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

    /**
     * Returns if there are alive players in the game.
     */
    public boolean evaluateUHC() {
        return Bukkit.getServer()
                .getOnlinePlayers()
                .stream()
                .filter(player -> player.getGameMode() == GameMode.SURVIVAL)
                .count() <= 1;
    }

    /**
     * Create a new survival game
     *
     * @param duration     Game duration in minutes
     * @param borderRadius Radius of the border in blocks
     */
    public SurvivalGame(int duration, int borderRadius) {
        this.gameTime = 0;
        this.gameDuration = duration * 60 * 20;
        this.borderRadius = borderRadius;
        this.worldName = Main.plugin.getConfig().getString("game-world-name");
        this.defaultWorldName = Main.plugin.getConfig().getString("lobby-world-name");

        final int startDelay = Main.plugin.getConfig().getInt("start-delay");
        this.startTime = 0;

        this.startTask = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
            this.startTime++;

            // Add players with teams
            List<Team> teams = new ArrayList<>(Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams());
            for (Team team : teams) {
                for (String entry : team.getEntries()) {
                    Bukkit.getPlayer(entry).addScoreboardTag("playing");
                }
            }

            // Display countdown
            for (Player player : this.getPlayers()) {
                player.sendTitle("§6§lDébut dans", String.format("§7%d secondes", startDelay - this.startTime), 0, 30, 0);
                player.playSound(player.getLocation(), Sound.BLOCK_DISPENSER_DISPENSE, 1, 1);
            }

            // Start game
            if (this.startTime >= startDelay) {
                Bukkit.getPluginManager().callEvent(new SurvivalGameStartEvent(this));

                this.task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
                    this.gameTime += interval;

                    Bukkit.getPluginManager().callEvent(new SurvivalGameTickEvent(this));

                    if (this.gameTime >= this.gameDuration && evaluateUHC()) {
                        this.endGame();
                    }

                }, 0, interval);

                Bukkit.getServer().getScheduler().cancelTask(this.startTask);
            }

        }, 0, interval);
    }

    public List<Player> getPlayers() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());

        players.removeIf(player -> !player.getScoreboardTags().contains("playing"));

        return players;
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
