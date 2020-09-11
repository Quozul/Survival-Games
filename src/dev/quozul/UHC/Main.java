package dev.quozul.UHC;

import dev.quozul.UHC.Commands.JoinTeam;
import dev.quozul.UHC.Commands.RegenWorlds;
import dev.quozul.UHC.Commands.StartCommand;
import dev.quozul.UHC.Listeners.GameEnd;
import dev.quozul.UHC.Listeners.GameListeners;
import dev.quozul.UHC.Listeners.GameStart;
import dev.quozul.UHC.Listeners.GameTick;
import org.bukkit.*;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.IOException;
import java.util.List;

public class Main extends JavaPlugin {

    public static Main plugin;

    /*public static void createTeams() {
        // Create teams
        // TODO: Put this in config file
        SurvivalGameStartEvent.teamNames.put("Noir", ChatColor.BLACK);
        SurvivalGameStartEvent.teamNames.put("Bleu foncé", ChatColor.DARK_BLUE);
        SurvivalGameStartEvent.teamNames.put("Vert foncé", ChatColor.DARK_GREEN);
        SurvivalGameStartEvent.teamNames.put("Bleu ciel", ChatColor.DARK_AQUA);
        SurvivalGameStartEvent.teamNames.put("Marron", ChatColor.DARK_RED);
        SurvivalGameStartEvent.teamNames.put("Violet", ChatColor.DARK_PURPLE);
        SurvivalGameStartEvent.teamNames.put("Or", ChatColor.GOLD);
        SurvivalGameStartEvent.teamNames.put("Gris", ChatColor.GRAY);
        SurvivalGameStartEvent.teamNames.put("Gris foncé", ChatColor.DARK_GRAY);
        SurvivalGameStartEvent.teamNames.put("Bleu clair", ChatColor.BLUE);
        SurvivalGameStartEvent.teamNames.put("Vert clair", ChatColor.GREEN);
        SurvivalGameStartEvent.teamNames.put("Cyan", ChatColor.AQUA);
        SurvivalGameStartEvent.teamNames.put("Rouge", ChatColor.RED);
        SurvivalGameStartEvent.teamNames.put("Magenta", ChatColor.LIGHT_PURPLE);
        SurvivalGameStartEvent.teamNames.put("Jaune", ChatColor.YELLOW);
        SurvivalGameStartEvent.teamNames.put("Blanc", ChatColor.WHITE);

        SurvivalGameStartEvent.teamNames.forEach((name, color) -> {
            Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

            if (board.getTeam(name) == null) {
                Team team = board.registerNewTeam(name);
                team.setColor(color);
            }
        });
    }*/

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("start").setExecutor(new StartCommand());
        this.getCommand("jointeam").setExecutor(new JoinTeam());
        this.getCommand("regenerateworlds").setExecutor(new RegenWorlds());
        //this.getCommand("selectteam").setExecutor(new SelectTeam());

        //this.getServer().getPluginManager().registerEvents(new SelectTeam(), this);
        this.getServer().getPluginManager().registerEvents(new GameListeners(), this);

        this.getServer().getPluginManager().registerEvents(new GameStart(), this);
        this.getServer().getPluginManager().registerEvents(new GameTick(), this);
        this.getServer().getPluginManager().registerEvents(new GameEnd(), this);

        plugin.saveDefaultConfig();

        try {
            RegenWorlds.generateWorlds();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //createTeams();

        // Set the world border for each world to the default one
        /*List<World> worlds = SurvivalGameStartEvent.getUHCWorlds();

        for (World world : worlds) {
            WorldBorder worldBorder = world.getWorldBorder();

            worldBorder.setCenter(0, 0);
            worldBorder.setSize(SurvivalGameStartEvent.startSize);
            worldBorder.setDamageAmount(0);

            // Reset time
            world.setFullTime(0);

            // Set gamerules
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        }*/
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

}
