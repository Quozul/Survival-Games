package dev.quozul.minigame;

import dev.quozul.UHC.Main;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityAirChangeEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LobbyListener implements Listener {
    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);
        player.getInventory().clear();

        // Teleport player to default world
        World world = Bukkit.getWorld(Main.plugin.getConfig().getString("lobby-world-name"));
        Location loc = world.getSpawnLocation();

        player.teleport(loc);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        Party party = PlayerData.from(player).getParty();
        party.forceRemovePlayer(player);
        PlayerData.remove(player);
    }

    @EventHandler
    void onPlayerPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    void onPlayerDamage(EntityDamageEvent event) {
        if (PlayerData.shouldBeInvisible(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerHunger(FoodLevelChangeEvent event) {
        if (PlayerData.shouldBeInvisible(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerAirChange(EntityAirChangeEvent event) {
        if (PlayerData.shouldBeInvisible(event.getEntity())) {
            event.setCancelled(true);
        }
    }
}
