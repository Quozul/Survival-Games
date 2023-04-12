package dev.quozul.minigame;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.jetbrains.annotations.NotNull;

public interface WorldGame {
    void configureWorldCreator(@NotNull WorldCreator worldCreator);

    void configureWorld(@NotNull World world);

    void setWorld(@NotNull World world);

    @NotNull World getWorld();
}
