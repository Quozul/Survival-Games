package dev.quozul.minigame;


import dev.quozul.UHC.Commands.RegenWorlds;
import dev.quozul.UHC.Main;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.bukkit.Bukkit.getScheduler;

enum SessionStatus {
    WAITING,
    IN_GAME;
}

/**
 * Represents a game session.
 */
public class Session implements ForwardingAudience {
    private static final int startDelay = Main.plugin.getConfig().getInt("start-delay");
    @NotNull
    private MiniGame game;
    private int startTask = -1;
    @NotNull
    private SessionStatus status = SessionStatus.WAITING;
    @NotNull
    private Set<Team> teams = new HashSet<>();
    @NotNull
    private final BossBar bossBar;
    private int tickingGameTask;
    private int gameTime = 0;

    public Session(MiniGame game) {
        this.game = game;
        bossBar = BossBar.bossBar(game.displayName(), 0F, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_6);
    }

    public @NotNull MiniGame getGame() {
        return game;
    }

    public boolean isOpen() {
        return status != SessionStatus.IN_GAME;
    }

    public void prepare(@NotNull Room room) {
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
                start(room);
                clearStartTask();
            }
        }, 0, 20);
    }

    public void unprepare(@NotNull Room room) {
        clearStartTask();
        room.hideBossBar(bossBar);
    }

    private void start(@NotNull Room room) {
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

        game.start(this);

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
        // TODO: Update end, might need to be in two steps: handle players and cleanup
        getScheduler().cancelTask(tickingGameTask);
        status = SessionStatus.WAITING;
        game.end();

        if (game instanceof WorldGame) {
            sendMessage(Component.text("Suppression du monde...", NamedTextColor.GRAY));
            RegenWorlds.removeWorld(((WorldGame) game).getWorld());
            sendMessage(Component.text("Monde supprimé!", NamedTextColor.GRAY));
        }

        teams.clear();
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

    public int getElapsedTime() {
        return gameTime;
    }
}
