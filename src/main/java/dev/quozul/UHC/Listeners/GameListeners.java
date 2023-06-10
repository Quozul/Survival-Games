package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.SurvivalGameData;
import dev.quozul.minigame.PlayerData;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

public class GameListeners implements Listener {

    @EventHandler
    void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (PlayerData.from(player).getGameData() instanceof SurvivalGameData data) {
            // Remove slow falling when player reach the ground
            if (data.isSpawning() && (player.isOnGround() || player.isInWater())) {
                player.removePotionEffect(PotionEffectType.SLOW_FALLING);
                data.setAlive();
            }
        }
    }

    @EventHandler
    void onPlayerDie(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (PlayerData.from(player).getGameData() instanceof SurvivalGameData data && data.isAlive()) {
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

            data.setDead();

            Player killer = player.getKiller();
            if (killer != null) {
                Component message = killer.displayName()
                        .appendSpace()
                        .append(Component.text(String.format("avait %.0f points de vie.", Math.ceil(player.getKiller().getHealth())), NamedTextColor.GRAY));

                player.sendMessage(message);
            }
        }
    }
}
