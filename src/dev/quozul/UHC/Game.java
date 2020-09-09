package dev.quozul.UHC;

import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Game implements CommandExecutor {

    // Constants

    private double circle = Math.PI * 2;

    // Global game variables

    private static int gameTime; // Game time in UHC in ticks
    private static final int interval = 20; // Interval for the loop in ticks
    public static final String worldName = "uhc_world"; // UHC's world
    public static final String serverDefaultWorldName = "world"; // Server's default world name
    public static int task;

    // Game parameters

    // TODO: Put this in config file
    public static int durationMinutes = 10; // Duration of the UHC game in minutes
    public static int durationTicks = durationMinutes * 60 * 20;
    // Recommended values: 128, 256, 512, 1024, 2048
    public static int startSize = 1024; // Diameter of the border on start
    public static int steps = 10; // Border decrease steps
    public static int borderDecreaseSpeed = 60; // Time for the border to reach the next step in seconds
    public static int maxDamage = 10; // Max world border damage
    // Create default teams
    public static Map<String, ChatColor> teamNames = new HashMap<>();

    // Calculated parameters

    public static int stepSize = durationTicks / steps;
    public static int borderDecreaseSize = startSize / steps;
    private int a = steps;

    // Constants
    public static NamespacedKey gameProgressBossBarNamespace = new NamespacedKey(Main.plugin, "uhc_progress");

    public static List<World> getUHCWorlds() {
        List<World> worlds = new ArrayList<>();
        worlds.add(Bukkit.getServer().getWorld(Game.worldName));
        worlds.add(Bukkit.getServer().getWorld(Game.worldName + "_nether"));
        return worlds;
    }

    private void initUHC() {
        gameTime = 0;

        // Set the world border for each world to the default one
        List<World> worlds = getUHCWorlds();

        for (World world : worlds) {
            // World border
            WorldBorder worldBorder = world.getWorldBorder();

            worldBorder.setCenter(0, 0);
            worldBorder.setSize(startSize);
            worldBorder.setDamageAmount(1);

            worldBorder.setSize(1, durationTicks / 20);

            // Reset time
            world.setFullTime(0);

            // Set gamerules
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            world.setGameRule(GameRule.DO_INSOMNIA, false);
            world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);
        }

        // Create the step progress boss bar
        BossBar bossBar = Bukkit.getServer().createBossBar(gameProgressBossBarNamespace, "Test", BarColor.YELLOW, BarStyle.SEGMENTED_10);
        bossBar.setVisible(true);
        bossBar.setProgress(0);

        // TODO: Do a dynamic boss bar displaying different information

        // Count teams with at least 1 player
        int filledTeams = 0;

        List<Team> teams = new ArrayList<>(Bukkit.getServer().getScoreboardManager().getMainScoreboard().getTeams());
        for (Team team : teams)
            if (team.getSize() > 0) filledTeams++;

        double radiusBetweenTeams = circle / filledTeams;

        double salt = Math.random() * circle;

        for (Team team : teams) {
            int teamIndex = teams.indexOf(team);

            for (String entry : team.getEntries()) {
                Player player = Bukkit.getPlayer(entry);

                if (player != null) {

                    // Teleports every players with their team to a equal distance from each others
                    int x = (int) Math.round(Math.cos(teamIndex * radiusBetweenTeams + salt) * (startSize / 2.5));
                    int z = (int) Math.round(Math.sin(teamIndex * radiusBetweenTeams + salt) * (startSize / 2.5));

                    World world = Bukkit.getWorld(worldName);
                    Location loc = new Location(world, x, 255, z);

                    player.teleport(loc);

                    // Add potion effects
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 999999, 0));

                    player.addScoreboardTag("spawning");
                    player.addScoreboardTag("playing");

                }
            }
        }

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            // Add bossbar(s) to player
            bossBar.addPlayer(player);

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

            Score step = infoObjective.getScore("§7» Etape");
            step.setScore(0);
            Score nextBorderRadius = infoObjective.getScore("§7» Prochain rayon");
            nextBorderRadius.setScore(0);
            Score nextBorder = infoObjective.getScore("§7» Prochaine zone (s)");
            nextBorder.setScore(0);
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
        }

    }

    public static boolean evaluateUHC() {
        int playersAlive = 0;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (player.getHealth() > 0 && player.getGameMode() == GameMode.SURVIVAL) playersAlive++;
        }

        if (playersAlive <= 1)
            return true;

        return false;
    }

    public static void finishUHC() {
        Player winner = null;

        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            player.setGameMode(GameMode.SPECTATOR);

            player.sendTitle("§6Bien joué !", "", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

            // Get winner
            if (!player.getScoreboardTags().contains("died") && player.getScoreboardTags().contains("playing"))
                winner = player;

            player.getScoreboardTags().remove("playing");
        }

        if (winner != null)
            Bukkit.broadcastMessage("§6§lVainqueur : §7" + winner.getDisplayName());

        // Remove bossbar
        BossBar bossBar = Bukkit.getServer().getBossBar(gameProgressBossBarNamespace);
        bossBar.removeAll();
        Bukkit.getServer().removeBossBar(gameProgressBossBarNamespace);

        // Teleport players back to spawn after 10 seconds
        Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, () -> {
            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                World world = Bukkit.getWorld(serverDefaultWorldName);
                Location loc = world.getSpawnLocation();

                player.teleport(loc);
                player.setGameMode(GameMode.ADVENTURE);
            }
        }, 200);
    }

    public int borderDiameter(int step) {
        double borderPercentage = step * borderDecreaseSize / (double)startSize;
        double borderPercentageExponential = ((Math.pow(a, (1 - borderPercentage)) - 1) / (a - 1));
        int currentBorder = (int) Math.floor(startSize * borderPercentageExponential);

        return currentBorder;
    }

    public int getStep() {
        return (int) Math.floor(gameTime / (float)stepSize);
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.isOp())
            return false;

        initUHC();

        task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(Main.plugin, () -> {

            gameTime += interval;

            int currentStep = getStep();
            int currentBorder = borderDiameter(currentStep);
            int nextBorderDiameter = borderDiameter(currentStep + 1);
            int damageAmount = currentStep / steps * (maxDamage - 1) + 1;
            boolean newStep = gameTime % stepSize == 0;
            int nextStepCooldown = (currentStep + 1) * stepSize - gameTime; // Next step in ticks

            if (newStep) {
                // World border
                List<World> worlds = getUHCWorlds();

                for (World world : worlds) {
                    WorldBorder worldBorder = world.getWorldBorder();

                    worldBorder.setSize(currentBorder, borderDecreaseSpeed);
                    worldBorder.setDamageAmount(damageAmount);
                }

                BossBar bossBar = Bukkit.getServer().getBossBar(new NamespacedKey(Main.plugin, "uhc_progress"));
                bossBar.setProgress(currentStep / (float)steps);

                // TODO: Play sound and display title on new step/border resize
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    player.sendTitle("§4Réduction de la bordure", "§7Diamètre : §8" + currentBorder, 10, 40, 10);
                    player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);
                }

                Bukkit.broadcastMessage("§4Réduction de la bordure. §7Diamètre : §8" + currentBorder);
            }

            for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                // Update scoreboard
                Scoreboard board = player.getScoreboard();

                Objective obj = board.getObjective("UHCInfo");

                Score step = obj.getScore("§7» Etape");
                step.setScore(currentStep);

                Score nextBorder = obj.getScore("§7» Prochaine zone (s)");
                nextBorder.setScore(nextStepCooldown / 20);

                Score nextRadius = obj.getScore("§7» Prochain rayon");
                nextRadius.setScore(nextBorderDiameter / 2);

                Score borderRadius = obj.getScore("§7» Rayon");
                borderRadius.setScore(currentBorder);

                player.setScoreboard(board);
            }

            if (currentStep >= steps)
                if (evaluateUHC()) {
                    Bukkit.getServer().getScheduler().cancelTask(task);
                    finishUHC();
                }

        }, 0, interval);

        return true;
    }

}
