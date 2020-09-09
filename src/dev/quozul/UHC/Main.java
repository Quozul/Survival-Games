package dev.quozul.UHC;

import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class Main extends JavaPlugin {

    public static Main plugin;

    public static void createTeams() {
        // Create teams
        // TODO: Put this in config file
        Game.teamNames.put("Noir", ChatColor.BLACK);
        Game.teamNames.put("Bleu foncé", ChatColor.DARK_BLUE);
        Game.teamNames.put("Vert foncé", ChatColor.DARK_GREEN);
        Game.teamNames.put("Bleu ciel", ChatColor.DARK_AQUA);
        Game.teamNames.put("Marron", ChatColor.DARK_RED);
        Game.teamNames.put("Violet", ChatColor.DARK_PURPLE);
        Game.teamNames.put("Or", ChatColor.GOLD);
        Game.teamNames.put("Gris", ChatColor.GRAY);
        Game.teamNames.put("Gris foncé", ChatColor.DARK_GRAY);
        Game.teamNames.put("Bleu clair", ChatColor.BLUE);
        Game.teamNames.put("Vert clair", ChatColor.GREEN);
        Game.teamNames.put("Cyan", ChatColor.AQUA);
        Game.teamNames.put("Rouge", ChatColor.RED);
        Game.teamNames.put("Magenta", ChatColor.LIGHT_PURPLE);
        Game.teamNames.put("Jaune", ChatColor.YELLOW);
        Game.teamNames.put("Blanc", ChatColor.WHITE);

        Game.teamNames.forEach((name, color) -> {
            Scoreboard board = Bukkit.getServer().getScoreboardManager().getMainScoreboard();

            if (board.getTeam(name) == null) {
                Team team = board.registerNewTeam(name);
                team.setColor(color);
            }
        });
    }

    @Override
    public void onEnable() {
        plugin = this;

        this.getCommand("start").setExecutor(new Game());
        this.getCommand("jointeam").setExecutor(new JoinTeam());
        this.getCommand("regenerateworlds").setExecutor(new RegenWorlds());
        this.getCommand("selectteam").setExecutor(new SelectTeam());

        this.getServer().getPluginManager().registerEvents(new SelectTeam(), this);
        this.getServer().getPluginManager().registerEvents(new GameListeners(), this);


        createTeams();

        // Set the world border for each world to the default one
        List<World> worlds = Game.getUHCWorlds();

        for (World world : worlds) {
            WorldBorder worldBorder = world.getWorldBorder();

            worldBorder.setCenter(0, 0);
            worldBorder.setSize(Game.startSize);
            worldBorder.setDamageAmount(0);

            // Reset time
            world.setFullTime(0);

            // Set gamerules
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

}
