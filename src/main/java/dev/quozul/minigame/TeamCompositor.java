package dev.quozul.minigame;

import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.stream.Collectors;

class SingleCompositor implements TeamCompositor {
    @Override
    public Set<Team> getTeams(@NotNull Room room) {
        return room.getPlayers().map(Team::new).collect(Collectors.toSet());
    }
}

public interface TeamCompositor {
    static TeamCompositor teamsOfOne() {
        return new SingleCompositor();
    }

    Set<Team> getTeams(@NotNull Room room);
}
