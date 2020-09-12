package dev.quozul.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.lang.management.BufferPoolMXBean;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

public class JoinTeam implements CommandExecutor, TabExecutor {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        Player player = (Player) commandSender;

        Set<Team> teams = player.getScoreboard().getTeams();

        List<String> results = new ArrayList<>();

        String teamName = String.join(" ", args).toLowerCase();

        for (Team team : teams) {
            String name = team.getName();

            if (name.toLowerCase().contains(teamName))
                results.add(name);
        }


        return results;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (args.length == 0) return false;

        Player player = (Player) commandSender;
        String teamName = String.join(" ", args);
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
