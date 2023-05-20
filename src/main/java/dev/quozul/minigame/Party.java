package dev.quozul.minigame;

import dev.quozul.minigame.exceptions.PartyIsPrivate;
import dev.quozul.minigame.exceptions.RoomInGameException;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Party implements ForwardingAudience {
    @NotNull
    final static private Map<String, Party> parties = new HashMap<>();

    public static @Nullable Party getParty(String name) {
        return parties.get(name);
    }

    public static Set<Party> getPublicParties() {
        return parties.values().stream().filter(Party::isPublic).collect(Collectors.toSet());
    }

    private void register(Party party) {
        parties.put(party.getName(), party);
    }

    private void unregister(Party party) {
        parties.remove(party.getName());
    }

    @NotNull
    private final Set<Player> members = new HashSet<>();
    @Nullable
    private Room room;
    @NotNull
    final private Player owner;
    @NotNull
    final private Set<Player> invitedPlayers = new HashSet<>();
    private boolean isPublic;

    public Party(@NotNull Player owner, boolean isPublic) {
        this.owner = owner;
        this.isPublic = isPublic;
        members.add(owner);
        register(this);
    }

    public String getName() {
        return owner.getName();
    }

    public Component displayName() {
        return owner.displayName()
                .append(Component.text("'s party"));
    }

    public @NotNull Player getOwner() {
        return owner;
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
        if (room != null && !room.getSession().isOpen()) {
            throw new RoomInGameException();
        }
        invitedPlayers.add(player);
    }

    public void join(Player player) throws RoomInGameException, PartyIsPrivate {
        if (room != null && !room.getSession().isOpen()) {
            throw new RoomInGameException();
        }

        if (isPublic) {
            members.add(player);
        } else if (invitedPlayers.contains(player)) {
            members.add(player);
        } else {
            throw new PartyIsPrivate();
        }

        invitedPlayers.remove(player);

        if (room != null) {
            room.partyUpdated();
        }
        sendMessage(player.displayName().append(Component.text(" a rejoint l'équipe.")));
    }

    public void leave(Player player) throws RoomInGameException {
        if (room != null && !room.getSession().isOpen()) {
            throw new RoomInGameException();
        }

        forceLeave(player);
    }

    public void forceLeave(Player player) {
        members.remove(player);

        // Remove party if empty
        if (members.isEmpty()) {
            unregister(this);
        }

        if (room != null) {
            room.partyUpdated();
        }

        sendMessage(player.displayName().append(Component.text(" a quitté l'équipe.")));
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
