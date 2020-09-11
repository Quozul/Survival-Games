package dev.quozul.UHC.Commands;

import dev.quozul.UHC.Main;
import dev.quozul.UHC.SurvivalGame;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

public class StartCommand implements CommandExecutor {

    // Constants

    public static NamespacedKey gameProgressBossBarNamespace = new NamespacedKey(Main.plugin, "uhc_progress");
    //private double circle = Math.PI * 2;

    // Global game variables

    //private static int gameTime; // Game time in UHC in ticks

    // Game parameters

    // TODO: Put this in config file
    //public static int durationMinutes = 20; // Duration of the UHC game in minutes
    // Recommended values: 128, 256, 512, 1024, 2048
    //public static int startSize = 2048; // Diameter of the border on start
    //public static int maxDamage = 10; // Max world border damage

    // Create default teams
    public static Map<String, ChatColor> teamNames = new HashMap<>();

    // Calculated parameters
    //public static int durationTicks = durationMinutes * 60 * 20;

    public static SurvivalGame game = null;


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.isOp())
            return false;

        int gameTime = Integer.parseInt(args[0]);
        game = new SurvivalGame(gameTime, Integer.parseInt(args[1]));

        return true;
    }
}
