package dev.quozul.UHC;

import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

public class GameListeners implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        Player player = e.getPlayer();

        // Remove slow falling when player reach the ground
        if (player.getScoreboardTags().contains("spawning") && player.isOnGround())
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent e) {
        Player player = e.getEntity();

        if (player.getScoreboardTags().contains("playing")) {
            // Play death sound
            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);
            player.setHealth(20);
            player.setGameMode(GameMode.SPECTATOR);

            player.getScoreboardTags().add("died");

            // Evaluate UHC end
            if (Game.evaluateUHC()) {
                Bukkit.getServer().getScheduler().cancelTask(Game.task);
                Game.finishUHC();
            }

            if (player.getKiller() != null)
                player.sendMessage(String.format("§7%s avait %.0f points de vie", player.getKiller().getDisplayName(), Math.ceil(player.getKiller().getHealth())));
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);

        for (Team team : player.getScoreboard().getTeams())
            team.removeEntry(player.getName());

        // Teleport player to default world
        World world = Bukkit.getWorld(Game.serverDefaultWorldName);
        Location loc = world.getSpawnLocation();

        player.teleport(loc);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        Player player = e.getPlayer();

        if (player.getScoreboardTags().contains("playing")) {
            player.setHealth(0);
            player.getScoreboardTags().remove("playing");
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent e) {
        if (e.getFrom().getWorld().getName().equals(Game.worldName))
            e.getTo().setWorld(Bukkit.getWorld(Game.worldName + "_nether"));

        if (e.getFrom().getWorld().getName().equals(Game.worldName + "_nether"))
            e.getTo().setWorld(Bukkit.getWorld(Game.worldName));
    }
}
