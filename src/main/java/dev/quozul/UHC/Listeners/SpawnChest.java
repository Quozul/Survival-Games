package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.UHC.Main;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.loot.LootTables;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.util.Transformation;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class SpawnChest implements Listener {
    private static final List<LootTables> LootTablesList = List.of(LootTables.ABANDONED_MINESHAFT, LootTables.BURIED_TREASURE, LootTables.DESERT_PYRAMID, LootTables.END_CITY_TREASURE, LootTables.IGLOO_CHEST, LootTables.JUNGLE_TEMPLE, LootTables.JUNGLE_TEMPLE_DISPENSER, LootTables.NETHER_BRIDGE, LootTables.PILLAGER_OUTPOST, LootTables.BASTION_TREASURE, LootTables.BASTION_OTHER, LootTables.BASTION_BRIDGE, LootTables.BASTION_HOGLIN_STABLE, LootTables.ANCIENT_CITY, LootTables.ANCIENT_CITY_ICE_BOX, LootTables.RUINED_PORTAL, LootTables.SHIPWRECK_MAP, LootTables.SHIPWRECK_SUPPLY, LootTables.SHIPWRECK_TREASURE, LootTables.SIMPLE_DUNGEON, LootTables.SPAWN_BONUS_CHEST, LootTables.STRONGHOLD_CORRIDOR, LootTables.STRONGHOLD_CROSSING, LootTables.STRONGHOLD_LIBRARY, LootTables.UNDERWATER_RUIN_BIG, LootTables.UNDERWATER_RUIN_SMALL);
    private final double chestChance = Main.plugin.getConfig().getDouble("chest-chance");
    private final int chestDelay = Main.plugin.getConfig().getInt("chest-delay");
    private final int fallDuration = Main.plugin.getConfig().getInt("chest-fall-duration");

    private void spawnChest(Location location) {
        World world = location.getWorld();

        BlockData blockData = Bukkit.createBlockData(Material.BARREL);

        BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(location, EntityType.BLOCK_DISPLAY);
        blockDisplay.setBlock(blockData);
        blockDisplay.setTransformation(new Transformation(new Vector3f(-0.5F, -0.5F, -0.5F), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f()));
        blockDisplay.setGlowing(true);

        LivingEntity stand = (LivingEntity) world.spawnEntity(location, EntityType.CHICKEN);
        stand.addPassenger(blockDisplay);
        stand.setInvisible(true);
        stand.setInvulnerable(true);
        stand.getPersistentDataContainer().set(namespace, PersistentDataType.BYTE, (byte) 1);

        for (int i = 0; i < 10; i++) {
            double x = Math.cos(i / 10. * Math.PI * 2);
            double z = Math.sin(i / 10. * Math.PI * 2);
            LivingEntity balloon = (LivingEntity) world.spawnEntity(location.clone().add(x, 5, z), EntityType.CHICKEN, true);
            balloon.setLeashHolder(stand);
        }
    }

    @EventHandler
    public void onGameTicks(SurvivalGameTickEvent event) {
        // Spawn chest every minutes
        if (event.getGame().getGameTime() % (chestDelay * 20) == 0 && Math.random() < chestChance) {
            World world = Bukkit.getServer().getWorld(event.getGame().getWorldName());
            double size = world.getWorldBorder().getSize();
            Location location = new Location(world, (Math.random() * size - size / 2) * 0.75, 0, (Math.random() * size - size / 2) * 0.75);
            location.setY(world.getHighestBlockYAt(location) + fallDuration * 2);

            // TODO: Use Kyori's messages
            // Announce
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendTitle("§6§lCoffre apparu", "", 10, 40, 10);
                player.sendMessage(String.format("§7Coffre apparu aux coordonnées : %.0f/%.0f/%.0f (à %.0f blocs d'ici)", location.getX(), location.getY(), location.getZ(), location.distance(player.getLocation())));
                player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
            }

            spawnChest(location);
        }
    }

    @EventHandler
    public void onShulkerBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.BARREL) {
            Barrel state = (Barrel) block.getState();

            if (state.getPersistentDataContainer().has(namespace)) {
                if (state.getInventory().isEmpty()) {
                    event.setDropItems(false);
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }

    static NamespacedKey namespace = new NamespacedKey(Main.plugin, "loot");

    @EventHandler
    public void onEntityChangeBlock(EntityMoveEvent event) {
        Entity entity = event.getEntity();
        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();

        if (dataContainer.has(namespace) && entity.isOnGround()) {
            // Remove falling entity
            for (Entity passenger : entity.getPassengers()) {
                passenger.remove();
            }

            entity.remove();

            // Set block
            Block block = entity.getWorld().getBlockAt(entity.getLocation());
            block.setType(Material.BARREL);

            // Set random loot table
            Barrel barrel = (Barrel) block.getState();
            LootTables lootTables = LootTablesList.get(new Random().nextInt(LootTablesList.size()));
            barrel.setLootTable(lootTables.getLootTable());
            barrel.customName(Component.text(lootTables.name()));
            barrel.getPersistentDataContainer().set(namespace, PersistentDataType.BYTE, (byte) 1);
            barrel.update();
        }
    }
}
