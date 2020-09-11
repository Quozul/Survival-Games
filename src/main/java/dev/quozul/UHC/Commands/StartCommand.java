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

    // Create default teams
    public static Map<String, ChatColor> teamNames = new HashMap<>();

    // TODO: Change this variable to non-static
    public static SurvivalGame game = null;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!commandSender.isOp())
            return false;

        int gameTime = Main.plugin.getConfig().getInt("game-duration");
        int borderRadius = Main.plugin.getConfig().getInt("border-radius");

        if (args.length >= 1)
            gameTime = Integer.parseInt(args[0]);
        if (args.length >= 2)
            borderRadius = Integer.parseInt(args[1]);

        game = new SurvivalGame(gameTime, borderRadius);

        return true;
    }

}
