package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.CustomRenderer;
import dev.quozul.UHC.Events.SurvivalGameStartEvent;
import dev.quozul.UHC.SurvivalGame;
import dev.quozul.UHC.SurvivalGameData;
import dev.quozul.minigame.PlayerData;
import dev.quozul.minigame.Team;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GameStart implements Listener {
    private final static double circle = Math.PI * 2;
    private final static Map<Integer, MapView.Scale> sizes = new HashMap<>();

    public GameStart() {
        sizes.put(128, MapView.Scale.CLOSEST);
        sizes.put(256, MapView.Scale.CLOSE);
        sizes.put(512, MapView.Scale.NORMAL);
        sizes.put(1024, MapView.Scale.FAR);
        sizes.put(2048, MapView.Scale.FARTHEST);
    }

    public static MapView.Scale getMapScale(SurvivalGame game) {
        int initial = game.getInitialBorderRadius();

        for (Map.Entry<Integer, MapView.Scale> entry : sizes.entrySet()) {
            if (initial <= entry.getKey()) {
                return entry.getValue();
            }
        }
        return MapView.Scale.CLOSEST;
    }

    public static ItemStack getMap(World world, SurvivalGame game, MapView.Scale scale) {
        MapView view = Bukkit.createMap(world);

        CustomRenderer renderer = new CustomRenderer(game);
        view.addRenderer(renderer);

        Location center = world.getWorldBorder().getCenter();

        view.setCenterX(center.getBlockX());
        view.setCenterZ(center.getBlockZ());
        view.setScale(scale);

        ItemStack item = new ItemStack(Material.FILLED_MAP, 1);

        MapMeta meta = (MapMeta) item.getItemMeta();
        meta.setMapView(view);

        item.setItemMeta(meta);

        return item;
    }

    @EventHandler
    void onSurvivalGameStart(SurvivalGameStartEvent event) {
        World world = event.getGame().getWorld();

        @NotNull Set<Team> teams = event.getGame().getTeams();

        // Count teams with at least 1 player
        long filledTeams = teams.stream().filter(team -> team.getSize() > 0).count();

        double radiusBetweenTeams = circle / filledTeams;
        double salt = Math.random() * circle;
        int i = 0;

        for (Team team : teams) {
            i++;

            for (Player player : team.getMembers()) {
                // Teleports every player with their team to an equal distance from each others
                int x = (int) Math.round(Math.cos(i * radiusBetweenTeams + salt) * (event.getGame().getInitialBorderRadius() / 2.5));
                int z = (int) Math.round(Math.sin(i * radiusBetweenTeams + salt) * (event.getGame().getInitialBorderRadius() / 2.5));

                Location loc = new Location(world, x, 255, z);

                player.teleport(loc);

                // Add potion effects
                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 999999, 0));
            }
        }

        for (Player player : event.getGame().getPlayers()) {
            // Heal player
            player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 255));
            player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 20, 255));

            // Clear inventory and reset xp
            player.getInventory().clear();
            player.setExp(0);
            player.setLevel(0);
            player.setFireTicks(0);

            player.getInventory().addItem(getMap(world, event.getGame(), getMapScale(event.getGame())));

            // Play sound and display title on game start
            player.sendTitle("§6Bonne chance", "", 10, 40, 10);
            player.playSound(player.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

            // Set player's gamemode
            player.setGameMode(GameMode.SURVIVAL);

            // Set player's tags
            PlayerData.from(player).setGameData(new SurvivalGameData());
        }
    }
}
