package dev.quozul.UHC.Commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.CommandHelp;
import co.aikar.commands.InvalidCommandArgument;
import co.aikar.commands.PaperCommandManager;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

@CommandAlias("party")
public class PartyCommand extends BaseCommand {
    public PartyCommand(PaperCommandManager manager) {
        manager.getCommandContexts().registerContext(Team.class, supplier -> {
            Team team = supplier.getPlayer().getScoreboard().getTeam(supplier.getFirstArg());
            if (team == null) {
                throw new InvalidCommandArgument("Cette équipe n'existe pas");
            }
            return team;
        });

        manager.getCommandCompletions().registerCompletion("teams", handler -> handler
                .getPlayer()
                .getScoreboard()
                .getTeams()
                .stream()
                .map(Team::getName)
                .toList()
        );
    }

    @Default
    @Subcommand("menu")
    void onDefault(Player player) {
        SelectTeam.createAndOpen(player);
    }

    @CommandCompletion("@teams")
    @Subcommand("join")
    void onJoin(Player player, Team team) {
        Component component = Component.text("Tu as rejoint", NamedTextColor.GRAY)
                .appendSpace()
                .append(team.displayName());
        player.sendMessage(component);

        team.addPlayer(player);
    }

    @CommandCompletion("@teams")
    @Subcommand("leave")
    @Description("Quitte son équipe.")
    void onLeave(Player player, Team team) {
        Component component = Component.text("Tu as quitté", NamedTextColor.GRAY)
                .appendSpace()
                .append(team.displayName());
        player.sendMessage(component);

        team.removePlayer(player);

        if (team.getSize() == 0) {
            // If team is empty, remove it
            team.unregister();
            player.sendMessage(Component.text("L'équipe était vide et a donc été supprimée."));
        }
    }

    @Subcommand("new")
    @Description("Créer une nouvelle équipe.")
    void onNew(Player player, OnlinePlayer otherPlayer) {
        Scoreboard scoreboard = player.getScoreboard();

        Team team = scoreboard.getTeam(player.getName());

        if (team == null) {
            team = scoreboard.registerNewTeam(player.getName());
            team.displayName(Component.text("Équipe de").appendSpace().append(player.displayName()));
        }

        otherPlayer.getPlayer().sendMessage(
                Component.text("Tu as été invité à rejoindre l'équipe de", NamedTextColor.GRAY)
                        .appendSpace()
                        .append(player.displayName())
                        .append(Component.text(".", NamedTextColor.GRAY))
                        .appendNewline()
                        .append(
                                Component.text("Clique sur ce message pour accepter.", NamedTextColor.GOLD)
                                        .clickEvent(ClickEvent.runCommand(String.format("/party join %s", player.getName())))
                        )
        );

        team.addPlayer(player);
        player.sendMessage(Component.text("Ton équipe a été créée.", NamedTextColor.GRAY));
    }

    @HelpCommand
    void doHelp(CommandHelp help) {
        help.showHelp();
    }
}
