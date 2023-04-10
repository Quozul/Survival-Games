package dev.quozul.minigame;

import dev.quozul.UHC.Main;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import static org.bukkit.Bukkit.getScheduler;

public class ExampleMiniGame implements MiniGame {
    private Room room;

    @Override
    public void start(Room room) {
        this.room = room;
        room.sendMessage(Component.text("Game has started and will end in 10 seconds."));

        // End game after 10 seconds
        getScheduler().runTaskLater(Main.plugin, this::end, 200);
    }

    @Override
    public void tick() {

    }

    @Override
    public void end() {
        room.sendMessage(Component.text("Game has ended."));
        room.release();
    }

    @Override
    public @NotNull RoomRequirements getRequirements() {
        return RoomRequirements.zero();
    }

    @Override
    public @NotNull Component displayName() {
        return Component.text("Exemple");
    }
}
