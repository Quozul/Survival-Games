package dev.quozul.UHC.Commands;

import dev.quozul.UHC.Main;
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

        generateWorld(worldName, worldEnvironment);
    }

    public static void generateWorld(String worldName, World.Environment worldEnvironment) {
        // Generating worlds
        WorldCreator uhcWorldCreator = new WorldCreator(worldName);
        uhcWorldCreator.environment(worldEnvironment);
        uhcWorldCreator.type(WorldType.NORMAL);
        World world = uhcWorldCreator.createWorld();

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
    }

    public static void regenerateUHCWorlds() throws IOException {
        String worldName = Main.plugin.getConfig().getString("game-worldname");

        regenerateWorld(worldName, World.Environment.NORMAL);
        regenerateWorld(worldName + "_nether", World.Environment.NETHER);
    }

    public static void generateWorlds() throws IOException {
        String worldName = Main.plugin.getConfig().getString("game-worldname");

        generateWorld(worldName, World.Environment.NORMAL);
        generateWorld(worldName + "_nether", World.Environment.NETHER);
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
