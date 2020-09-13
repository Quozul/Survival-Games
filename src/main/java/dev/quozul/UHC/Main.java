package dev.quozul.UHC;

import dev.quozul.UHC.Commands.*;
import dev.quozul.UHC.Listeners.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;

public class Main extends JavaPlugin {

    public static Main plugin;

    public static void createTeams() {
        // Create teams
        // TODO: Put this in config file
        StartCommand.teamNames.put("black", ChatColor.BLACK);
        StartCommand.teamNames.put("dark_blue", ChatColor.DARK_BLUE);
        StartCommand.teamNames.put("dark_green", ChatColor.DARK_GREEN);
        StartCommand.teamNames.put("dark_aqua", ChatColor.DARK_AQUA);
        StartCommand.teamNames.put("dark_red", ChatColor.DARK_RED);
        StartCommand.teamNames.put("dark_purple", ChatColor.DARK_PURPLE);
        StartCommand.teamNames.put("gold", ChatColor.GOLD);
        StartCommand.teamNames.put("gray", ChatColor.GRAY);
        StartCommand.teamNames.put("dark_gray", ChatColor.DARK_GRAY);
        StartCommand.teamNames.put("blue", ChatColor.BLUE);
        StartCommand.teamNames.put("green", ChatColor.GREEN);
        StartCommand.teamNames.put("cyan", ChatColor.AQUA);
        StartCommand.teamNames.put("red", ChatColor.RED);
        StartCommand.teamNames.put("light_purple", ChatColor.LIGHT_PURPLE);
        StartCommand.teamNames.put("yellow", ChatColor.YELLOW);
        StartCommand.teamNames.put("white", ChatColor.WHITE);

        Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();
        for (Team team : board.getTeams())
            team.unregister();

        StartCommand.teamNames.forEach((name, color) -> {
            if (board.getTeam(name) == null) {
                Team team = board.registerNewTeam(name);
                team.setColor(color);
            }
        });
    }

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("start").setExecutor(new StartCommand());
        this.getCommand("jointeam").setExecutor(new JoinTeam());
        this.getCommand("regenerateworlds").setExecutor(new RegenWorlds());

        this.getCommand("selectteam").setExecutor(new SelectTeam());
        this.getServer().getPluginManager().registerEvents(new SelectTeam(), this);

        this.getServer().getPluginManager().registerEvents(new GameListeners(), this);

        this.getServer().getPluginManager().registerEvents(new GameStart(), this);
        this.getServer().getPluginManager().registerEvents(new GameTick(), this);
        this.getServer().getPluginManager().registerEvents(new GameEnd(), this);
        this.getServer().getPluginManager().registerEvents(new GameBossBars(), this);

        SpawnChest spawnChest = new SpawnChest();
        this.getCommand("spawnchest").setExecutor(spawnChest);
        this.getServer().getPluginManager().registerEvents(spawnChest, this);

        plugin.saveDefaultConfig();

        try {
            RegenWorlds.generateWorlds();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //createTeams();
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

}
