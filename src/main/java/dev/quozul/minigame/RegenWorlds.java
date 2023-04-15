package dev.quozul.minigame;

import dev.quozul.UHC.Main;
import dev.quozul.minigame.exceptions.WorldNotDeletedException;
import org.bukkit.*;

import static org.bukkit.Bukkit.getServer;

class ChunkNotUnloadedException extends Throwable {
    private final Chunk chunk;

    public ChunkNotUnloadedException(Chunk chunk) {
        this.chunk = chunk;
    }

    public Chunk getChunk() {
        return chunk;
    }
}

public class RegenWorlds {
    public static void removeWorld(World world) throws WorldNotDeletedException {
        // Unload the chunks.
        for (Chunk chunk : world.getLoadedChunks()) {
            chunk.unload(false);
        }

        // Unload the world.
        getServer().unloadWorld(world, false);

        if (!world.getWorldFolder().delete()) {
            world.getWorldFolder().deleteOnExit();
        }
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
