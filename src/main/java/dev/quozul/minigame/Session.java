package dev.quozul.minigame;


import dev.quozul.UHC.Main;
import dev.quozul.minigame.exceptions.RoomInGameException;
import dev.quozul.minigame.exceptions.WorldNotDeletedException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static org.bukkit.Bukkit.getScheduler;

enum SessionStatus {
    WAITING,
    IN_GAME
}

/**
 * Represents a game session.
 */
public class Session implements ForwardingAudience {
    private static final int startDelay = Main.plugin.getConfig().getInt("start-delay");
    @NotNull
    private final MiniGame game;
    private int startTask = -1;
    @NotNull
    private SessionStatus status = SessionStatus.WAITING;
    @NotNull
    private Set<Team> teams = new HashSet<>();
    @NotNull
    private final BossBar bossBar;
    private int tickingGameTask;
    private int gameTime = 0;
    private boolean isPreparing = false;
    @NotNull
    private final Room room;

    public Session(MiniGame game, @NotNull Room room) {
        this.game = game;
        this.room = room;
        bossBar = BossBar.bossBar(game.displayName(), 0F, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_6);
    }

    public @NotNull MiniGame getGame() {
        return game;
    }

    /**
     * Returns true if the Session is not currently in a game therefore allowing parties to leave the room.
     */
    public boolean isOpen() {
        return status != SessionStatus.IN_GAME;
    }

    public void prepare() {
        isPreparing = true;
        AtomicInteger startTime = new AtomicInteger();
        room.showBossBar(bossBar);

        startTask = getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
            Component mainTitle = Component.text("Début dans", NamedTextColor.GOLD);
            Component subtitle = Component.text(String.format("%d secondes", startDelay - startTime.get()), NamedTextColor.GRAY);

            Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofMillis(1500), Duration.ofSeconds(1));
            Title title = Title.title(mainTitle, subtitle, times);

            room.showTitle(title);

            startTime.getAndIncrement();

            room.playSound(Sound.sound(Key.key("minecraft:block.dispenser.dispense"), Sound.Source.MASTER, 1, 1));

            bossBar.progress((float) startTime.get() / startDelay);

            if (startTime.get() >= startDelay) {
                game.onLoad();
                start();
                clearStartTask();
            }
        }, 0, 20);
    }

    public void unprepare() {
        clearStartTask();
        room.hideBossBar(bossBar);
        isPreparing = false;
    }

    private void start() {
        teams = game.getCompositor().getTeams(room);

        if (game instanceof WorldGame) {
            room.sendMessage(Component.text("Génération du monde...", NamedTextColor.GRAY));

            WorldCreator worldCreator = new WorldCreator(UUID.randomUUID().toString());
            ((WorldGame) game).configureWorldCreator(worldCreator);

            World world = worldCreator.createWorld();
            assert world != null;
            ((WorldGame) game).configureWorld(world);

            ((WorldGame) game).setWorld(world);

            room.sendMessage(Component.text("Monde généré!", NamedTextColor.GRAY));
        }

        status = SessionStatus.IN_GAME;
        room.hideBossBar(bossBar);

        game.onStart(this);

        if (game instanceof TimedGame) {
            long gameDuration = ((TimedGame) game).getGameDuration();
            gameTime = 0;

            tickingGameTask = getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
                gameTime += 20;

                ((TimedGame) game).tick();

                if (gameTime >= gameDuration || (game instanceof EndCondition && ((EndCondition) game).isEnded())) {
                    this.end();
                }
            }, 0, 20);
        }
    }

    private void end() {
        getScheduler().cancelTask(tickingGameTask);
        game.onFinish();

        // Unload game after 10 seconds
        getScheduler().runTaskLater(Main.plugin, () -> {
            game.onUnload();

            if (game instanceof WorldGame) {
                try {
                    RegenWorlds.removeWorld(((WorldGame) game).getWorld());
                    Bukkit.getLogger().warning("World deleted!");
                } catch (WorldNotDeletedException e) {
                    Bukkit.getLogger().warning("World was not deleted.");
                }
            }

            teams.clear();
            status = SessionStatus.WAITING;

            try {
                room.clear();
            } catch (RoomInGameException e) {
                Bukkit.getLogger().warning("Room was not cleared.");
            }
        }, 200);
    }

    private void clearStartTask() {
        if (startTask != -1) {
            getScheduler().cancelTask(startTask);
            startTask = -1;
        }

        bossBar.progress(0);

        if (isOpen()) {
            showBossBar(bossBar);
        } else {
            hideBossBar(bossBar);
        }
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return teams;
    }

    public @NotNull Set<Team> getTeams() {
        return teams;
    }

    public @NotNull Set<Player> getPlayers() {
        return teams.stream().flatMap(team -> team.getMembers().stream()).collect(Collectors.toSet());
    }

    public int getElapsedTime() {
        return gameTime;
    }

    public @NotNull BossBar getBossBar() {
        return bossBar;
    }

    public @NotNull SessionStatus getStatus() {
        return status;
    }

    public boolean isPreparing() {
        return isPreparing;
    }
}
