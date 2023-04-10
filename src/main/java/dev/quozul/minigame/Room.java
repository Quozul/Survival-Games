package dev.quozul.minigame;

import dev.quozul.UHC.Main;
import dev.quozul.minigame.exceptions.PartyIncompatibleException;
import dev.quozul.minigame.exceptions.PartyIncompatibleReason;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static org.bukkit.Bukkit.getScheduler;

enum RoomStatus {
    WAITING, IN_GAME;
}

public class Room implements ForwardingAudience {
    private static final int startDelay = Main.plugin.getConfig().getInt("start-delay");
    @NotNull
    final static private Set<Room> rooms = new HashSet<>();

    public static @Nullable Room getRoom(Player player) {
        for (Room room : rooms) {
            if (room.getPlayers().anyMatch(predicate -> predicate == player)) {
                return room;
            }
        }
        return null;
    }

    private void register(Room room) {
        rooms.add(room);
    }

    @NotNull
    private final Set<Party> parties = new HashSet<>();
    @NotNull
    private MiniGame game;
    private int startTask = -1;
    @NotNull
    private RoomStatus status = RoomStatus.WAITING;
    @NotNull
    private final BossBar bossBar;

    public Room(@NotNull MiniGame game) {
        this.game = game;
        bossBar = BossBar.bossBar(game.displayName(), 0F, BossBar.Color.YELLOW, BossBar.Overlay.NOTCHED_6);
        register(this);
    }

    public void setGame(MiniGame game) {
        this.game = game;
        bossBar.name(game.displayName());
        clearStartTask();
    }

    public void addParty(Party party) throws RoomInGameException, PartyIncompatibleException {
        if (isLocked()) {
            throw new RoomInGameException();
        }

        RoomRequirements requirements = game.getRequirements();

        if (getPartyCount() >= requirements.maxParties) {
            throw new PartyIncompatibleException(PartyIncompatibleReason.TOO_MANY_TEAMS_IN_ROOM);
        } else if (requirements.minPlayersPerParty < party.getSize()) {
            throw new PartyIncompatibleException(PartyIncompatibleReason.NOT_ENOUGH_PLAYERS_IN_TEAM);
        } else if (party.getSize() > requirements.maxPlayersPerParty) {
            throw new PartyIncompatibleException(PartyIncompatibleReason.TOO_MANY_PLAYERS_IN_TEAM);
        }

        party.setRoom(this);
        parties.add(party);

        partyUpdated();
    }

    public void removeParty(Party party) throws RoomInGameException {
        if (isLocked()) {
            throw new RoomInGameException();
        }

        party.setRoom(null);
        parties.remove(party);
        party.hideBossBar(bossBar);

        partyUpdated();
    }

    void partyUpdated() {
        if (canStart()) {
            startCountDown();
        } else {
            clearStartTask();
        }

        if (!isLocked()) {
            showBossBar(bossBar);
        }
    }

    private void clearStartTask() {
        if (startTask != -1) {
            getScheduler().cancelTask(startTask);
            startTask = -1;
        }

        bossBar.progress(0);

        if (!isLocked()) {
            showBossBar(bossBar);
        }
    }

    /**
     * @return Amount of parties in the room.
     */
    private int getPartyCount() {
        return parties.size();
    }

    /**
     * @return Amount of players in the room.
     */
    private int getPlayerCount() {
        return parties.stream().map(Party::getSize).reduce(0, Integer::sum);
    }

    /**
     * @return Whether the game can start.
     */
    private boolean canStart() {
        RoomRequirements requirements = game.getRequirements();
        return requirements.getMinimumAmountOfPlayers() <= getPlayerCount() && getPlayerCount() <= requirements.getMaximumAmountOfPlayers();
    }

    /**
     * Locks a room.
     */
    private void lock() {
        status = RoomStatus.IN_GAME;
        game.start(this);
        hideBossBar(bossBar);
    }

    private void startCountDown() {
        AtomicInteger startTime = new AtomicInteger();
        showBossBar(bossBar);

        startTask = getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {
            Component mainTitle = Component.text("Début dans", NamedTextColor.GOLD);
            Component subtitle = Component.text(String.format("%d secondes", startDelay - startTime.get()), NamedTextColor.GRAY);

            Title.Times times = Title.Times.times(Duration.ZERO, Duration.ofMillis(1500), Duration.ofSeconds(1));
            Title title = Title.title(mainTitle, subtitle, times);

            showTitle(title);

            startTime.getAndIncrement();

            playSound(Sound.sound(Key.key("minecraft:block.dispenser.dispense"), Sound.Source.MASTER, 1, 1));

            bossBar.progress((float) startTime.get() / startDelay);

            if (startTime.get() >= startDelay) {
                lock();
                clearStartTask();
            }
        }, 0, 20);
    }

    /**
     * Release the room so it can be modified.
     * Should be called from a MiniGame's end method.
     */
    public void release() {
        status = RoomStatus.WAITING;

        for (Party party : parties) {
            party.setRoom(null);
            parties.remove(party);
            party.hideBossBar(bossBar);
        }

        partyUpdated();
    }

    boolean isLocked() {
        return status == RoomStatus.IN_GAME;
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return parties;
    }

    public Stream<Player> getPlayers() {
        return parties.stream().map(Party::getMembers).flatMap(Set::stream);
    }

    public @NotNull Set<Party> getParties() {
        return parties;
    }

    public @NotNull MiniGame getGame() {
        return game;
    }
}
