package dev.quozul.UHC.Commands;

import dev.quozul.UHC.Main;
import org.bukkit.*;

import static org.bukkit.Bukkit.getServer;

public class RegenWorlds {
    public static void removeWorld(World world) {
        // Unload the world.
        getServer().unloadWorld(world, false);

        // Unload the chunks.
        for (Chunk chunk : world.getLoadedChunks()) {
            chunk.unload(false);
        }

        world.getWorldFolder().delete();
    }

    public static World generateWorld(String worldName) {
        // Generating worlds
        WorldCreator worldCreator = new WorldCreator(worldName);
        worldCreator.environment(World.Environment.NORMAL);
        worldCreator.type(WorldType.NORMAL);
        World world = worldCreator.createWorld();

        assert world != null;
        WorldBorder worldBorder = world.getWorldBorder();

        int startSize = Main.plugin.getConfig().getInt("border-radius");
        worldBorder.setCenter(0, 0);
        worldBorder.setSize(startSize);
        worldBorder.setDamageAmount(0);
        worldBorder.setDamageBuffer(0);

        // Reset time
        world.setFullTime(0);

        // Set gamerules
        world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.DO_INSOMNIA, false);
        world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

        return world;
    }
}
