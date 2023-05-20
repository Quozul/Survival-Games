package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Main;
import dev.quozul.minigame.Party;
import dev.quozul.minigame.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
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

public class GameListeners implements Listener {

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        // Remove slow falling when player reach the ground
        if (player.getScoreboardTags().contains("spawning") && (player.isOnGround() || player.isInWater())) {
            player.removePotionEffect(PotionEffectType.SLOW_FALLING);
            player.removeScoreboardTag("spawning");
        }
    }

    @EventHandler
    void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (player.getScoreboardTags().contains("playing")) {
            event.setCancelled(true);

            org.bukkit.Sound deathSound = event.getDeathSound();
            if (deathSound != null) {
                player.getWorld().playSound(Sound.sound(deathSound.key(), Sound.Source.MASTER, event.getDeathSoundVolume(), event.getDeathSoundPitch()));
            }

            Component deathMessage = event.deathMessage();
            if (deathMessage != null) {
                player.getWorld().sendMessage(deathMessage);
            }

            // Play death sound
            player.getWorld().playSound(Sound.sound(Key.key("entity.lightning_bolt.impact"), Sound.Source.MASTER, 1, 1));
            player.setHealth(20);
            player.setGameMode(GameMode.SPECTATOR);

            player.getScoreboardTags().add("died");

            Player killer = player.getKiller();
            if (killer != null) {
                Component message = killer.displayName()
                        .appendSpace()
                        .append(Component.text(String.format("avait %.0f points de vie.", Math.ceil(player.getKiller().getHealth())), NamedTextColor.GRAY));

                player.sendMessage(message);
            }
        }
    }

    @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.setGameMode(GameMode.ADVENTURE);

        // Teleport player to default world
        World world = Bukkit.getWorld(Main.plugin.getConfig().getString("lobby-world-name"));
        Location loc = world.getSpawnLocation();

        player.teleport(loc);
    }

    @EventHandler
    void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        if (player.getScoreboardTags().contains("playing")) {
            player.setHealth(0);
            player.getScoreboardTags().remove("playing");
        }

        Party party = PlayerData.from(player).getParty();
        party.forceLeave(player);
    }

    @EventHandler
    void onPlayerPortal(PlayerPortalEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    void onPlayerDamage(EntityDamageEvent event) {
        if (event.getEntityType() == EntityType.PLAYER && ((Player) event.getEntity()).getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    void onPlayerHunger(FoodLevelChangeEvent event) {
        if (event.getEntity().getGameMode() == GameMode.ADVENTURE) {
            event.setCancelled(true);
        }
    }
}
