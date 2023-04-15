package dev.quozul.minigame;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface MiniGame {
    /**
     * Called when the Session starts the wait delay.
     */
    default void onLoad() {
    }

    /**
     * Called when the session actually starts.
     */
    void onStart(Session session);

    /**
     * Called when the session ends the game.
     */
    void onFinish();

    /**
     * Called when the session wants to unload the game.
     * If players were still in the world, the game should teleport them back to the lobby.
     */
    default void onUnload() {
    }

    @NotNull RoomRequirements getRequirements();

    @NotNull Component displayName();

    @NotNull TeamCompositor getCompositor();
}
