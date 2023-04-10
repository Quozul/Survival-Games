package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import dev.quozul.UHC.Main;
import org.bukkit.*;
import org.bukkit.command.CommandSender;

public class RegenWorlds extends BaseCommand {

    static void regenerateWorld(String worldName, World.Environment worldEnvironment) {
        World world = Bukkit.getServer().getWorld(worldName);
        if (world != null) {
            // Unload the world.
            Bukkit.getServer().unloadWorld(world, false);

            // Unload the chunks.
            Chunk[] chunks = world.getLoadedChunks();

            for (Chunk chunk : chunks)
                chunk.unload(false);

            world.getWorldFolder().delete();
        }

        generateWorld(worldName, worldEnvironment);
    }

    static void generateWorld(String worldName, World.Environment worldEnvironment) {
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

    private static void regenerateUHCWorlds() {
        String worldName = Main.plugin.getConfig().getString("game-world-name");

        regenerateWorld(worldName, World.Environment.NORMAL);
        regenerateWorld(worldName + "_nether", World.Environment.NETHER);
    }

    public static void generateWorlds() {
        String worldName = Main.plugin.getConfig().getString("game-world-name");

        generateWorld(worldName, World.Environment.NORMAL);
        generateWorld(worldName + "_nether", World.Environment.NETHER);
    }

    @Default
    void onCommand(CommandSender commandSender) {
        if (!commandSender.isOp()) {
            return;
        }

        regenerateUHCWorlds();
    }

    @HelpCommand
    void doHelp(CommandHelp help) {
        help.showHelp();
    }
}
