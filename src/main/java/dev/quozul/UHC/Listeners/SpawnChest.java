package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.UHC.Main;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.data.BlockData;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.loot.LootTables;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public class SpawnChest implements Listener, CommandExecutor {
    private static final Map<LootTables, Material> LootTablesList = new HashMap<>();
    private final double chestChance = Main.plugin.getConfig().getDouble("chest-chance");
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

    private void spawnChest(Location loc, List<Player> players) {
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

        for (Player player : players) {
            player.sendTitle("§6§lCoffre apparu", "", 10, 40, 10);
            player.sendMessage(String.format("§7Coffre apparu aux coordonnées : %.0f/%.0f/%.0f (à %.0f blocs d'ici)", loc.getX(), loc.getY(), loc.getZ(), loc.distance(player.getLocation())));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
        }
    }

    @EventHandler
    public void onGameTicks(SurvivalGameTickEvent e) {
        // Spawn chest every minutes
        if (e.getGame().getGameTime() % (chestDelay * 20) == 0 && Math.random() > chestChance) {

            World world = Bukkit.getServer().getWorld(e.getGame().getWorldName());
            double size = world.getWorldBorder().getSize();
            Location loc = new Location(world, (Math.random() * size - size / 2) * 0.75, 0, (Math.random() * size - size / 2) * 0.75);
            loc.setY(world.getHighestBlockYAt(loc) + 1);

            spawnChest(loc, e.getGame().getPlayers());

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
    public void onShulkerClose(InventoryCloseEvent e) {
        if (e.getInventory().getType().equals(InventoryType.SHULKER_BOX) && e.getInventory().isEmpty()) {
            Location loc = e.getInventory().getLocation();
            loc.getBlock().setType(Material.AIR);

            loc.getWorld().spawnParticle(Particle.SMOKE_NORMAL, loc.add(.5, .5, .5), 100, 0, .1, .1, .1);
            loc.getWorld().playSound(loc, Sound.BLOCK_FIRE_EXTINGUISH, 1, 1);
        }
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!commandSender.isOp()) return false;

        Player player = (Player) commandSender;
        Location loc = player.getLocation();

        /*
        // Spawn falling block riding chicken
        BlockData blockData = Bukkit.createBlockData(Material.BLUE_CONCRETE);
        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, blockData);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
        fallingBlock.setGlowing(true);
        fallingBlock.setInvulnerable(true);

        Entity entity = loc.getWorld().spawnEntity(loc, EntityType.CHICKEN);
        entity.addPassenger(fallingBlock);
        entity.setSilent(true);

        LivingEntity livingEntity = (LivingEntity) entity;

        PotionEffect invisibility = new PotionEffect(PotionEffectType.INVISIBILITY, 999999, 255, false, false, false);
        livingEntity.addPotionEffect(invisibility);
        */

        /*World world = loc.getWorld();

        BlockData blockData = Bukkit.createBlockData(Material.BLUE_CONCRETE);
        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(loc, blockData);
        fallingBlock.setDropItem(false);
        fallingBlock.setHurtEntities(false);
        fallingBlock.setGlowing(true);
        fallingBlock.setInvulnerable(true);

        Entity armorStand = world.spawnEntity(loc, EntityType.ARMOR_STAND);
        ((LivingEntity) armorStand).addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 999999, 0));

        armorStand.addPassenger(fallingBlock);*/

        /*for (int i = 0; i < 10; i++) {
            Entity chicken = world.spawnEntity(loc, EntityType.CHICKEN);
            ((LivingEntity) chicken).setLeashHolder(armorStand);
        }*/

        spawnChest(loc, new ArrayList<>(Bukkit.getOnlinePlayers()));

        return true;
    }
}
