package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.UHC.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootTables;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class SpawnChest implements Listener {
    private static final Map<LootTables, Material> LootTablesList = new HashMap<>();
    private final int chestChange = Main.plugin.getConfig().getInt("chest-chance");
    private final int chestDelay = Main.plugin.getConfig().getInt("chest-delay");

    public SpawnChest() {
        LootTablesList.put(LootTables.ABANDONED_MINESHAFT, Material.GRAY_SHULKER_BOX);
        LootTablesList.put(LootTables.BASTION_BRIDGE, Material.RED_SHULKER_BOX);
        LootTablesList.put(LootTables.BASTION_HOGLIN_STABLE, Material.RED_SHULKER_BOX);
        LootTablesList.put(LootTables.BASTION_OTHER, Material.RED_SHULKER_BOX);
        LootTablesList.put(LootTables.BASTION_TREASURE, Material.RED_SHULKER_BOX);
        LootTablesList.put(LootTables.BURIED_TREASURE, Material.BLUE_SHULKER_BOX);
        LootTablesList.put(LootTables.DESERT_PYRAMID, Material.YELLOW_SHULKER_BOX);
        LootTablesList.put(LootTables.END_CITY_TREASURE, Material.MAGENTA_SHULKER_BOX);
        LootTablesList.put(LootTables.IGLOO_CHEST, Material.WHITE_SHULKER_BOX);
        LootTablesList.put(LootTables.JUNGLE_TEMPLE, Material.GREEN_SHULKER_BOX);
        LootTablesList.put(LootTables.NETHER_BRIDGE, Material.BLACK_SHULKER_BOX);
        LootTablesList.put(LootTables.PILLAGER_OUTPOST, Material.BROWN_SHULKER_BOX);
        LootTablesList.put(LootTables.RUINED_PORTAL, Material.PURPLE_SHULKER_BOX);
        LootTablesList.put(LootTables.SHIPWRECK_MAP, Material.LIGHT_BLUE_SHULKER_BOX);
        LootTablesList.put(LootTables.SHIPWRECK_SUPPLY, Material.LIGHT_BLUE_SHULKER_BOX);
        LootTablesList.put(LootTables.SHIPWRECK_TREASURE, Material.LIGHT_BLUE_SHULKER_BOX);
        LootTablesList.put(LootTables.SPAWN_BONUS_CHEST, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.STRONGHOLD_CORRIDOR, Material.LIGHT_GRAY_SHULKER_BOX);
        LootTablesList.put(LootTables.STRONGHOLD_CROSSING, Material.LIGHT_GRAY_SHULKER_BOX);
        LootTablesList.put(LootTables.UNDERWATER_RUIN_BIG, Material.LIGHT_BLUE_SHULKER_BOX);
        LootTablesList.put(LootTables.UNDERWATER_RUIN_SMALL, Material.LIGHT_BLUE_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_ARMORER, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_BUTCHER, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_CARTOGRAPHER, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_DESERT_HOUSE, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_FISHER, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_FLETCHER, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_MASON, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_PLAINS_HOUSE, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_SAVANNA_HOUSE, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_SHEPHERD, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_SNOWY_HOUSE, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_TAIGA_HOUSE, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_TANNERY, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_TEMPLE, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_TOOLSMITH, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.VILLAGE_WEAPONSMITH, Material.LIME_SHULKER_BOX);
        LootTablesList.put(LootTables.WOODLAND_MANSION, Material.BROWN_SHULKER_BOX);
    }

    @EventHandler
    public void onGameTicks(SurvivalGameTickEvent e) {
        // Spawn chest every minutes
        if (e.getGame().getGameTime() % (chestDelay * 20) == 0 && Math.random() > chestChange) {

            World world = Bukkit.getServer().getWorld(e.getGame().getWorldName());
            double size = world.getWorldBorder().getSize();
            Location loc = new Location(world, (Math.random() * size - size / 2) * 0.75, 0, (Math.random() * size - size / 2) * 0.75);
            loc.setY(world.getHighestBlockYAt(loc) + 1);

            Random generator = new Random();
            Object[] values = LootTablesList.values().toArray();
            int index = generator.nextInt(values.length);
            Material randomMaterial = (Material) values[index];

            Block block = loc.getWorld().getBlockAt(loc);
            block.setType(randomMaterial);

            LootTables lootTable = ((LootTables) LootTablesList.keySet().toArray()[index]);

            ShulkerBox shulkerBox = (ShulkerBox) loc.getBlock().getState();
            shulkerBox.setLootTable(Bukkit.getLootTable(lootTable.getKey()));
            shulkerBox.setCustomName(lootTable.toString());
            shulkerBox.update();

            for (Player player : Bukkit.getServer().getOnlinePlayers())
                player.sendTitle("§6§lCoffre apparu", "", 10, 40, 10);

            Bukkit.broadcastMessage(String.format("§7Coffre apparu aux coordonnées :\n%.0f/%.0f/%.0f", loc.getX(), loc.getY(), loc.getZ()));
        }
    }

    @EventHandler
    public void onShulkerBreak(BlockBreakEvent e) {
        if (e.getBlock().getType().toString().contains("SHULKER_BOX"))
            if (((ShulkerBox)e.getBlock().getState()).getInventory().isEmpty())
                e.setDropItems(false);
            else
                e.setCancelled(true);
    }

    @EventHandler
    public void onShulkerOpen(InventoryCloseEvent e) {
        if (e.getInventory().getType().equals(InventoryType.SHULKER_BOX) && e.getInventory().isEmpty())
            e.getInventory().getLocation().getBlock().setType(Material.AIR);
    }
}
