package dev.quozul.UHC.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.List;

public class JoinTeam implements CommandExecutor, TabExecutor {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> results = new ArrayList<>(StartCommand.teamNames.keySet());

        // TODO: Do filtering on results
        for (String result : results)
            results.set(results.indexOf(result), result.replace(" ", "_"));

        return results;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) return false;

        Player player = (Player) commandSender;
        String teamName = args[0].replace("_", " ");
        Scoreboard scoreboard = player.getScoreboard();

        // Get willed team
        Team team = scoreboard.getTeam(teamName);

        // If team exists, leave previous teams and join it
        if (team == null) return false;

        for (Team leaveTeam : scoreboard.getTeams()) {
            leaveTeam.removeEntry(player.getName());
        }

        player.sendMessage("§7Vous avez rejoint l'équipe " + team.getColor() + team.getDisplayName());
        team.addEntry(player.getName());

        player.setScoreboard(scoreboard);

        return true;
    }

}
