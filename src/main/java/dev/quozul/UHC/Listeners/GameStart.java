package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.List;

public class GameStart implements Listener {
    private static double circle = Math.PI * 2;

    @EventHandler
    public void onSurvivalGameStart(SurvivalGameStartEvent e) {
        // Set the world border for each world to the default one
        List<World> worlds = e.getGame().getWorlds();

        for (World world : worlds) {
            // World border
            WorldBorder worldBorder = world.getWorldBorder();

            worldBorder.setCenter(0, 0);
            worldBorder.setSize(e.getGame().getInitialBorderRadius());
            worldBorder.setDamageAmount(1);

            worldBorder.setSize(1, e.getGame().getGameDuration() / 20);

            // Reset time
            world.setFullTime(0);
            world.setStorm(false);

            // Set gamerules
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        }

        // Count teams with at least 1 player
        int filledTeams = 0;

        List<Team> teams = new ArrayList<>(Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams());
        for (Team team : teams)
            if (team.getSize() > 0) filledTeams++;

        double radiusBetweenTeams = circle / filledTeams;
        double salt = Math.random() * circle;
        int i = 0;

        for (Team team : teams) {
            if (team.getSize() > 0) {
                i++;

                for (String entry : team.getEntries()) {
                    Player player = Bukkit.getPlayer(entry);

                    if (player != null) {
                        // Teleports every players with their team to a equal distance from each others
                        int x = (int) Math.round(Math.cos(i * radiusBetweenTeams + salt) * (e.getGame().getInitialBorderRadius() / 2.5));
                        int z = (int) Math.round(Math.sin(i * radiusBetweenTeams + salt) * (e.getGame().getInitialBorderRadius() / 2.5));

                        World world = Bukkit.getWorld(e.getGame().getWorldName());
                        Location loc = new Location(world, x, 255, z);

                        player.teleport(loc);

                        // Add potion effects
                        player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 999999, 0));
                    }
                }
            }
        }

        for (Player player : e.getGame().getPlayers()) {
            // Heal player
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 255));

            // Clear inventory and reset xp
            player.getInventory().clear();
            player.setExp(0);
            player.setLevel(0);

            // TODO: Give each player a map of the overworld

            // Play sound and display title on game start
            player.sendTitle("§6Bonne chance", "", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

            // Create info scoreboard
            Scoreboard board = Bukkit.getScoreboardManager().getMainScoreboard();

            Objective infoObjective;
            if (board.getObjective("UHCInfo") != null)
                infoObjective = board.getObjective("UHCInfo");
            else
                infoObjective = board.registerNewObjective("UHCInfo", "dummy", "§6§lUHC");

            Score borderRadius = infoObjective.getScore("§7» Rayon");
            borderRadius.setScore(0);

            infoObjective.setDisplaySlot(DisplaySlot.SIDEBAR);

            // Create kill scoreboard
            Objective killObjective;
            if (board.getObjective("UHCKills") != null)
                killObjective = board.getObjective("UHCKills");
            else
                killObjective = board.registerNewObjective("UHCKills", "playerKillCount", "§6§lKills");

            killObjective.setDisplaySlot(DisplaySlot.BELOW_NAME);

            player.setScoreboard(board);

            // Set player's gamemode
            player.setGameMode(GameMode.SURVIVAL);

            // Set player's tags
            player.addScoreboardTag("spawning");
            player.addScoreboardTag("playing");
            player.removeScoreboardTag("died");
        }
    }
}
