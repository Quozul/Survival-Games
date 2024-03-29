package dev.quozul.UHC;

import dev.quozul.UHC.Events.SurvivalGameEndEvent;
import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.minigame.*;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getPluginManager;

public class SurvivalGame implements MiniGame, ForwardingAudience, TimedGame, WorldGame, EndCondition {
    private final int gameDuration;
    private final int borderRadius;
    private final String lobbyWorldName;
    public BossBar borderBossBar;
    private World world;
    private Session session;
    private final RoomRequirements requirements;

    /**
     * Returns if there are alive players in the game.
     */
    @Override
    public boolean isEnded() {
        int alivePlayers = 0;
        for (Player player : session.getPlayers()) {
            GameData data = PlayerData.from(player).getGameData();
            if (data instanceof SurvivalGameData && ((SurvivalGameData) data).isAlive()) {
                if (++alivePlayers > 0) { // TODO: Change 0 to 1
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Create a new survival game
     *
     * @param duration     Game duration in minutes
     * @param borderRadius Radius of the border in blocks
     */
    public SurvivalGame(int duration, int borderRadius) {
        gameDuration = duration * 60 * 20;
        this.borderRadius = borderRadius;
        requirements = new RoomRequirements(2, 16);

        lobbyWorldName = Main.plugin.getConfig().getString("lobby-world-name");
        borderBossBar = BossBar.bossBar(Component.text("Bordure"), 0, BossBar.Color.RED, BossBar.Overlay.PROGRESS);
    }

    public Set<Player> getPlayers() {
        return session.getTeams().stream().map(Team::getMembers).flatMap(Set::stream).collect(Collectors.toSet());
    }

    @Override
    public void onFinish() {
        getPluginManager().callEvent(new SurvivalGameEndEvent(this));
    }

    @Override
    public void onUnload() {
        // Teleport players back to spawn
        for (Player player : getPlayers()) {
            World world = Bukkit.getWorld(getDefaultWorldName());
            Location loc = world.getSpawnLocation();

            player.teleport(loc);
            player.setGameMode(GameMode.ADVENTURE);

            for (PotionEffect potionEffect : player.getActivePotionEffects()) {
                player.removePotionEffect(potionEffect.getType());
            }
        }
    }

    public String getDefaultWorldName() {
        return this.lobbyWorldName;
    }

    @Override
    public @NotNull World getWorld() {
        return this.world;
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
        return session.getElapsedTime();
    }

    @Override
    public void onStart(Session session) {
        this.session = session;
        chests.clear();

        getPluginManager().callEvent(new SurvivalGameStartEvent(this));
    }

    @Override
    public @NotNull RoomRequirements getRequirements() {
        return requirements;
    }

    @Override
    public @NotNull Component displayName() {
        return Component.text("Survival game");
    }

    @Override
    public @NotNull TeamCompositor getCompositor() {
        return TeamCompositor.teamsOfOne();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "survival-game";
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return session.audiences();
    }

    public Set<Team> getTeams() {
        return session.getTeams();
    }

    private final List<Location> chests = new ArrayList<>();

    public List<Location> getChests() {
        return chests;
    }

    public void addChest(Location location) {
        chests.add(location);
    }

    @Override
    public void tick() {
        getPluginManager().callEvent(new SurvivalGameTickEvent(this));
    }

    @Override
    public long getGameDuration() {
        return gameDuration;
    }

    @Override
    public void configureWorldCreator(@NotNull WorldCreator worldCreator) {
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.type(WorldType.NORMAL);
    }

    @Override
    public void configureWorld(@NotNull World world) {
        // Configure world border
        WorldBorder worldBorder = world.getWorldBorder();

        worldBorder.setCenter(0, 0);
        worldBorder.setSize(getInitialBorderRadius());
        worldBorder.setDamageAmount(1);
        worldBorder.setDamageBuffer(0);

        // Make the world border reduce size
        worldBorder.setSize(1, getGameDuration() / 20);

        // Reset time
        world.setFullTime(0);

        // Set gamerules
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

    }

    @Override
    public void setWorld(@NotNull World world) {
        this.world = world;
    }
}
