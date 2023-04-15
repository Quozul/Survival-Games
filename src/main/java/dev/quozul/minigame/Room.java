package dev.quozul.minigame;

import dev.quozul.minigame.exceptions.PartyIncompatibleException;
import dev.quozul.minigame.exceptions.PartyIncompatibleReason;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class Room implements ForwardingAudience {
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
    private final Session session;

    public Room(@NotNull Session session) {
        this.session = session;
        register(this);
    }

    public void addParty(Party party) throws RoomInGameException, PartyIncompatibleException {
        if (!getSession().isOpen()) {
            throw new RoomInGameException();
        }

        RoomRequirements requirements = session.getGame().getRequirements();

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
        if (!getSession().isOpen()) {
            throw new RoomInGameException();
        }

        party.setRoom(null);
        parties.remove(party);
        party.hideBossBar(session.getBossBar());

        partyUpdated();
    }

    void partyUpdated() {
        if (canStart()) {
            session.prepare(this);
        } else {
            session.unprepare(this);
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
        RoomRequirements requirements = session.getGame().getRequirements();
        return requirements.getMinimumAmountOfPlayers() <= getPlayerCount() && getPlayerCount() <= requirements.getMaximumAmountOfPlayers();
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return parties;
    }

    public Stream<Player> getPlayers() {
        return parties.stream().map(Party::getMembers).flatMap(Set::stream);
    }

    public @NotNull Session getSession() {
        return session;
    }
}
