package dev.quozul.UHC;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class RegenWorlds implements CommandExecutor {

    public static void regenerateWorld(String worldName, World.Environment worldEnvironment) throws IOException {
        World world = Bukkit.getServer().getWorld(worldName);
        if (world != null) {
            File worldFolder = world.getWorldFolder();

            // Unload the world.
            Bukkit.getServer().unloadWorld(world, false);

            // Unload the chunks.
            Chunk[] chunks = world.getLoadedChunks();

            for (Chunk chunk : chunks)
                chunk.unload(false);

            FileUtils.deleteDirectory(worldFolder);
        }

        generateWorlds(worldName, worldEnvironment);
    }

    public static void generateWorlds(String worldName, World.Environment worldEnvironment) {
        // Generating worlds
        WorldCreator uhcWorldCreator = new WorldCreator(worldName);
        uhcWorldCreator.environment(worldEnvironment);
        uhcWorldCreator.type(WorldType.NORMAL);
        uhcWorldCreator.createWorld();
    }

    public static void regenerateUHCWorlds() throws IOException {
        regenerateWorld(Game.worldName, World.Environment.NORMAL);
        regenerateWorld(Game.worldName + "_nether", World.Environment.NETHER);
    }

    public static void generateWorlds() throws IOException {
        regenerateWorld(Game.worldName, World.Environment.NORMAL);
        regenerateWorld(Game.worldName + "_nether", World.Environment.NETHER);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        try {
            regenerateUHCWorlds();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

}
