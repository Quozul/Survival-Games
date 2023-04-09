package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.HelpCommand;
import co.aikar.commands.annotation.Optional;
import dev.quozul.UHC.Main;
import dev.quozul.UHC.SurvivalGame;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

@CommandAlias("start")
public class StartCommand extends BaseCommand {

    // Create default teams
    public static Map<String, ChatColor> teamNames = new HashMap<>();

    // TODO: Change this variable to non-static
    public static SurvivalGame game = null;

    @Default
    void onCommand(CommandSender commandSender, @Optional Integer gameTime, @Optional Integer borderRadius) {
        if (!commandSender.isOp()) {
            return;
        }

        if (gameTime == null) {
            gameTime = Main.plugin.getConfig().getInt("game-duration");
        }

        if (borderRadius == null) {
            borderRadius = Main.plugin.getConfig().getInt("border-radius");
        }

        game = new SurvivalGame(gameTime, borderRadius);

        commandSender.sendMessage(Component.text(String.format("Démarrage d'une partie de %d minutes avec un rayon de %d blocks.", gameTime, borderRadius)));
    }

    @HelpCommand
    void doHelp(CommandHelp help) {
        help.showHelp();
    }
}
