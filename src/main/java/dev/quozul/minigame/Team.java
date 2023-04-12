package dev.quozul.minigame;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.audience.ForwardingAudience;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Team implements ForwardingAudience {
    @NotNull
    private final Set<Player> members = new HashSet<>();

    public Team(@NotNull Player... players) {
        members.addAll(List.of(players));
    }

    @Override
    public @NotNull Iterable<? extends Audience> audiences() {
        return members;
    }

    public int getSize() {
        return members.size();
    }

    @NotNull
    public Set<Player> getMembers() {
        return members;
    }
}
