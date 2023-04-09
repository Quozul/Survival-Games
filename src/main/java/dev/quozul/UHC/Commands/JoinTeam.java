package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

@CommandAlias("team")
public class JoinTeam extends BaseCommand {
    public JoinTeam(PaperCommandManager manager) {
        manager.getCommandContexts().registerContext(Team.class, supplier -> {
            Team team = supplier.getPlayer().getScoreboard().getTeam(supplier.getFirstArg());
            if (team == null) {
                throw new InvalidCommandArgument("Cette équipe n'existe pas");
            }
            return team;
        });

        manager.getCommandCompletions().registerCompletion("teams", handler -> handler.getPlayer().getScoreboard().getTeams().stream().map(Team::getName).toList());
    }

    @Default
    void onDefault(Player player) {
        SelectTeam.createAndOpen(player);
    }


    @CommandCompletion("@teams")
    @Subcommand("join")
    void onJoin(Player player, Team team) {
        Component component = Component.text("Vous avez rejoint l'équipe", NamedTextColor.GRAY)
                .append(team.displayName());
        player.sendMessage(component);

        team.addPlayer(player);
    }

    @CommandCompletion("@teams")
    @Subcommand("leave")
    void onLeave(Player player, Team team) {
        Component component = Component.text("Vous avez quitté l'équipe", NamedTextColor.GRAY)
                .append(team.displayName());
        player.sendMessage(component);

        team.removePlayer(player);
    }

    @HelpCommand
    void doHelp(CommandHelp help) {
        help.showHelp();
    }
}
