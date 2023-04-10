package dev.quozul.UHC;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.minigame.MiniGame;
import dev.quozul.minigame.Party;
import dev.quozul.minigame.Room;
import dev.quozul.minigame.RoomRequirements;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.bukkit.Bukkit.getPluginManager;
import static org.bukkit.Bukkit.getScheduler;

public class SurvivalGame implements MiniGame, ForwardingAudience {
    private int gameTime;
    private final int gameDuration;
    private final int borderRadius;
    private final String worldName;
    private final String defaultWorldName;
    public BossBar borderBossBar;

    private int task;
    private final int interval = 20;
    private Room room;

    /**
     * Returns if there are alive players in the game.
     */
    public boolean evaluateUHC() {
        return room.getPlayers()
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
        this.borderBossBar = BossBar.bossBar(Component.text("Bordure"), 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    public List<Player> getPlayers() {
        return room.getPlayers().toList();
    }

    @Override
    public void end() {
        getScheduler().cancelTask(this.task);
        getPluginManager().callEvent(new SurvivalGameEndEvent(this));
        room.release();
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

    @Override
    public void start(Room room) {
        this.room = room;
        getPluginManager().callEvent(new SurvivalGameStartEvent(this));
        chests.clear();

        this.task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
            this.gameTime += interval;

            getPluginManager().callEvent(new SurvivalGameTickEvent(this));

            if (this.gameTime >= this.gameDuration && evaluateUHC()) {
                this.end();
            }

        }, 0, interval);
    }

    @Override
    public @NotNull RoomRequirements getRequirements() {
        return RoomRequirements.zero();
    }

    @Override
    public @NotNull Component displayName() {
        return Component.text("Survival game");
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return room.audiences();
    }

    public Set<Party> getParties() {
        return room.getParties();
    }

    private final List<Location> chests = new ArrayList<>();

    public List<Location> getChests() {
        return chests;
    }

    public void addChest(Location location) {
        chests.add(location);
    }
}
