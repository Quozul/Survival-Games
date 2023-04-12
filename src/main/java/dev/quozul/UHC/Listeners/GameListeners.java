package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Main;
import dev.quozul.UHC.SurvivalGame;
import dev.quozul.minigame.Party;
import dev.quozul.minigame.Room;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scoreboard.Team;

public class GameListeners implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Remove slow falling when player reach the ground
        if (player.getScoreboardTags().contains("spawning") && player.isOnGround()) {
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
            player.removeScoreboardTag("spawning");
        }
    }

    @EventHandler
    public void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getScoreboardTags().contains("playing")) {
            // Play death sound

            player.getWorld().playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_IMPACT, 1, 1);
            player.setHealth(20);
            player.setGameMode(GameMode.SPECTATOR);

            player.getScoreboardTags().add("died");

            // Evaluate UHC end
            Room room = Room.getRoom(player);
            if (room != null) {
                SurvivalGame game = (SurvivalGame) room.getSession().getGame();
                if (game.isEnded()) {
                    game.end();
                }
            }

            if (player.getKiller() != null) {
                player.sendMessage(String.format("§7%s avait %.0f points de vie", player.getKiller().getDisplayName(), Math.ceil(player.getKiller().getHealth())));
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);

        for (Team team : player.getScoreboard().getTeams())
            team.removeEntry(player.getName());

        // Teleport player to default world
        World world = Bukkit.getWorld(Main.plugin.getConfig().getString("lobby-world-name"));
        Location loc = world.getSpawnLocation();

        player.teleport(loc);
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getScoreboardTags().contains("playing")) {
            player.setHealth(0);
            player.getScoreboardTags().remove("playing");
        }

        Party party = Party.getParty(player);
        if (party != null) {
            party.forceLeave(player);
        }
    }

    @EventHandler
    public void onPlayerPortal(PlayerPortalEvent event) {
        String worldName = Main.plugin.getConfig().getString("game-world-name");

        if (event.getFrom().getWorld().getName().equals(worldName)) {
            event.getTo().setWorld(Bukkit.getWorld(worldName + "_nether"));
        }

        if (event.getFrom().getWorld().getName().equals(worldName + "_nether")) {
            event.getTo().setWorld(Bukkit.getWorld(worldName));
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && ((Player) event.getEntity()).getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }
}
