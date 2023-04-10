package dev.quozul.minigame;

import dev.quozul.minigame.exceptions.PartyIsPrivate;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Party implements ForwardingAudience {
    @NotNull
    final static private Set<Party> parties = new HashSet<>();

    public static @Nullable Party getParty(Player player) {
        for (Party party : parties) {
            if (party.hasMember(player)) {
                return party;
            }
        }
        return null;
    }

    public static Party getParty(String name) {
        for (Party party : parties) {
            if (party.getName().equals(name)) {
                return party;
            }
        }
        return null;
    }

    public static Set<Party> getPublicParties() {
        return parties.stream().filter(Party::isPublic).collect(Collectors.toSet());
    }

    private void register(Party party) {
        parties.add(party);
    }

    private void unregister(Party party) {
        parties.remove(party);
    }

    @NotNull
    private final Set<Player> members = new HashSet<>();
    @NotNull
    private final Team team;
    @Nullable
    private Room room;
    @NotNull
    final private Player owner;
    @NotNull
    final private Set<Player> invitedPlayers = new HashSet<>();
    private boolean isPublic;

    public Party(Player owner, boolean isPublic) {
        this.owner = owner;
        this.team = owner.getScoreboard().registerNewTeam(owner.getName());
        this.isPublic = isPublic;
        members.add(owner);
        register(this);
    }

    public String getName() {
        return team.getName();
    }

    public Component displayName() {
        return team.displayName();
    }

    public Party(Player owner) {
        this(owner, false);
    }

    public @NotNull Player getOwner() {
        return owner;
    }

    public @NotNull Team getTeam() {
        return team;
    }

    public @Nullable Room getRoom() {
        return room;
    }

    void setRoom(@Nullable Room room) {
        this.room = room;
    }

    /**
     * @return The amount of players in the party.
     */
    public int getSize() {
        return members.size();
    }

    public void invitePlayer(Player player) throws RoomInGameException {
        if (room != null && room.isLocked()) {
            throw new RoomInGameException();
        }
        invitedPlayers.add(player);
    }

    public void join(Player player) throws RoomInGameException, PartyIsPrivate {
        if (room != null && room.isLocked()) {
            throw new RoomInGameException();
        }

        if (isPublic) {
            members.add(player);
            team.addPlayer(player);
        } else if (invitedPlayers.contains(player)) {
            members.add(player);
            team.addPlayer(player);
        } else {
            throw new PartyIsPrivate();
        }

        invitedPlayers.remove(player);

        room.partyUpdated();
    }

    public void leave(Player player) throws RoomInGameException {
        if (room != null && room.isLocked()) {
            throw new RoomInGameException();
        }

        forceLeave(player);
    }

    public void forceLeave(Player player) {
        team.removePlayer(player);
        members.remove(player);

        // Remove party if empty
        if (members.isEmpty()) {
            team.unregister();
            unregister(this);
        }

        if (room != null) {
            room.partyUpdated();
        }
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return members;
    }

    public boolean hasMember(Player player) {
        for (Player member : members) {
            if (member == player) {
                return true;
            }
        }

        return false;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public @NotNull Set<Player> getMembers() {
        return members;
    }
}
