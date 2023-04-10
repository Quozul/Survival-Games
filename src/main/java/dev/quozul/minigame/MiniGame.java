package dev.quozul.minigame;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface MiniGame {
    void start(Room room);

    void end();

    @NotNull RoomRequirements getRequirements();

    @NotNull Component displayName();
}
