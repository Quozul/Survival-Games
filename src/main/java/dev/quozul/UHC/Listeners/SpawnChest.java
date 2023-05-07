package dev.quozul.UHC.Listeners;

import dev.quozul.UHC.Events.SurvivalGameTickEvent;
import dev.quozul.UHC.Main;
import dev.quozul.UHC.SurvivalGame;
import io.papermc.paper.event.entity.EntityMoveEvent;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.title.Title;
import org.bukkit.*;
import org.bukkit.block.Barrel;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.loot.LootTables;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.metadata.Metadatable;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Transformation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

final class AirDrop {
    private final SurvivalGame game;
    private BlockDisplay display;
    private final List<LivingEntity> balloons;
    private Location location;

    AirDrop(BlockDisplay display, List<LivingEntity> ballons, Location location, SurvivalGame game) {
        this.display = display;
        this.balloons = ballons;
        this.location = location;
        this.game = game;
    }

    public BlockDisplay getDisplay() {
        return display;
    }

    public void setDisplay(BlockDisplay display) {
        this.display = display;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public List<LivingEntity> getBalloons() {
        return balloons;
    }

    public SurvivalGame getGame() {
        return game;
    }
}

final class AirDropMetadata implements MetadataValue {
    @NotNull
    private final JavaPlugin plugin;
    @NotNull
    private final AirDrop value;

    public AirDropMetadata(@NotNull AirDrop value, @NotNull JavaPlugin plugin) {
        this.value = value;
        this.plugin = plugin;
    }

    @Override
    public @NotNull AirDrop value() {
        return value;
    }

    @Override
    public int asInt() {
        throw new NullPointerException();
    }

    @Override
    public float asFloat() {
        throw new NullPointerException();
    }

    @Override
    public double asDouble() {
        throw new NullPointerException();
    }

    @Override
    public long asLong() {
        throw new NullPointerException();
    }

    @Override
    public short asShort() {
        throw new NullPointerException();
    }

    @Override
    public byte asByte() {
        throw new NullPointerException();
    }

    @Override
    public boolean asBoolean() {
        throw new NullPointerException();
    }

    @Override
    public @NotNull String asString() {
        throw new NullPointerException();
    }

    @Override
    public @NotNull Plugin getOwningPlugin() {
        return plugin;
    }

    @Override
    public void invalidate() {
        throw new NullPointerException();
    }
}

public class SpawnChest implements Listener {
    private static final List<LootTables> LootTablesList = List.of(LootTables.ABANDONED_MINESHAFT, LootTables.BURIED_TREASURE, LootTables.DESERT_PYRAMID, LootTables.END_CITY_TREASURE, LootTables.IGLOO_CHEST, LootTables.JUNGLE_TEMPLE, LootTables.JUNGLE_TEMPLE_DISPENSER, LootTables.NETHER_BRIDGE, LootTables.PILLAGER_OUTPOST, LootTables.BASTION_TREASURE, LootTables.BASTION_OTHER, LootTables.BASTION_BRIDGE, LootTables.BASTION_HOGLIN_STABLE, LootTables.ANCIENT_CITY, LootTables.ANCIENT_CITY_ICE_BOX, LootTables.RUINED_PORTAL, LootTables.SHIPWRECK_MAP, LootTables.SHIPWRECK_SUPPLY, LootTables.SHIPWRECK_TREASURE, LootTables.SIMPLE_DUNGEON, LootTables.SPAWN_BONUS_CHEST, LootTables.STRONGHOLD_CORRIDOR, LootTables.STRONGHOLD_CROSSING, LootTables.STRONGHOLD_LIBRARY, LootTables.UNDERWATER_RUIN_BIG, LootTables.UNDERWATER_RUIN_SMALL);
    private static final NamespacedKey namespace = new NamespacedKey(Main.plugin, "loot");

    private final double chestChance = Main.plugin.getConfig().getDouble("chest-chance");
    private final int chestDelay = Main.plugin.getConfig().getInt("chest-delay");
    private final int fallDuration = Main.plugin.getConfig().getInt("chest-fall-duration");

    public static void spawnChest(SurvivalGame game, Location location) {
        World world = location.getWorld();

        BlockData blockData = Bukkit.createBlockData(Material.BARREL);

        BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(location, EntityType.BLOCK_DISPLAY);
        blockDisplay.setBlock(blockData);
        blockDisplay.setTransformation(new Transformation(new Vector3f(-0.5F, -0.5F, -0.5F), new AxisAngle4f(), new Vector3f(1, 1, 1), new AxisAngle4f()));
        blockDisplay.setGlowing(true);

        AirDrop airDrop = new AirDrop(blockDisplay, new ArrayList<>(), location, game);

        LivingEntity stand = (LivingEntity) world.spawnEntity(location, EntityType.CHICKEN);
        stand.addPassenger(blockDisplay);
        stand.setInvisible(true);
        stand.setInvulnerable(true);

        for (int i = 0; i < 10; i++) {
            double x = Math.cos(i / 10. * Math.PI * 2);
            double z = Math.sin(i / 10. * Math.PI * 2);
            LivingEntity balloon = (LivingEntity) world.spawnEntity(location.clone().add(x, 5, z), EntityType.CHICKEN, true);
            balloon.setLeashHolder(stand);
            airDrop.getBalloons().add(balloon);
        }

        stand.setMetadata(namespace.getKey(), new AirDropMetadata(airDrop, Main.plugin));
        game.addChest(location);
    }

    @EventHandler
    void onGameTicks(SurvivalGameTickEvent event) {
        // Spawn chest every minutes
        if (event.getGame().getGameTime() % (chestDelay * 20) == 0 && Math.random() < chestChance) {
            World world = event.getGame().getWorld();
            double size = world.getWorldBorder().getSize();
            Location location = new Location(world, (Math.random() * size - size / 2) * 0.75, 0, (Math.random() * size - size / 2) * 0.75);
            location.setY(world.getHighestBlockYAt(location) + fallDuration * 2);

            spawnChest(event.getGame(), location);

            // Announce
            Component mainTitle = Component.text("Coffre apparu", NamedTextColor.GOLD);
            Title title = Title.title(mainTitle, Component.empty());

            event.getGame().showTitle(title);
            event.getGame().playSound(Sound.sound(Key.key("minecraft:entity.player.levelup"), Sound.Source.MASTER, 1, 1));

            for (Player player : event.getGame().getPlayers()) {
                // TODO: Use MiniMessages
                location.setY(player.getLocation().getY()); // To calculate the distance without taking Y into account

                Component component = Component.text("Coffre apparu aux coordonnées : ")
                        .append(Component.text(Math.round(location.getX())))
                        .append(Component.text("/"))
                        .append(Component.text(Math.round(location.getZ())))
                        .append(Component.text(" (à "))
                        .append(Component.text(Math.round(location.distance(player.getLocation()))))
                        .append(Component.text(" blocs d'ici)"))
                        .color(NamedTextColor.GRAY);
                player.sendMessage(component);
            }
        }
    }

    @EventHandler
    void onChestBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        if (block.getType() == Material.BARREL) {
            Barrel state = (Barrel) block.getState();

            AirDropMetadata airDropMetadata = getAirDrop(state);
            if (airDropMetadata != null) {
                event.setCancelled(true);
            }
        }
    }

    private @Nullable AirDropMetadata getAirDrop(Metadatable entity) {
        for (MetadataValue value : entity.getMetadata(namespace.getKey())) {
            if (value instanceof AirDropMetadata) {
                return (AirDropMetadata) value;
            }
        }
        return null;
    }

    @EventHandler
    void onChestLand(EntityMoveEvent event) {
        Entity entity = event.getEntity();

        AirDropMetadata airDropMetadata = getAirDrop(entity);
        if (airDropMetadata != null && (entity.isOnGround() || entity.isInWater())) {
            // Remove falling entity
            for (Entity passenger : entity.getPassengers()) {
                passenger.remove();
            }

            for (LivingEntity balloon : airDropMetadata.value().getBalloons()) {
                balloon.setLeashHolder(null);
            }

            entity.remove();
            World world = entity.getWorld();

            // Set block
            Block block = world.getBlockAt(entity.getLocation());
            block.setType(Material.BARREL);

            // Block display for glowing
            BlockData blockData = Bukkit.createBlockData(Material.BARREL);

            BlockDisplay blockDisplay = (BlockDisplay) world.spawnEntity(block.getLocation(), EntityType.BLOCK_DISPLAY);
            blockDisplay.setBlock(blockData);
            blockDisplay.setTransformation(new Transformation(new Vector3f(0.05F, 0.05F, 0.05F), new AxisAngle4f(), new Vector3f(0.9F, 0.9F, 0.9F), new AxisAngle4f()));
            blockDisplay.setGlowing(true);

            // Set random loot table
            Barrel barrel = (Barrel) block.getState();

            airDropMetadata.value().setDisplay(blockDisplay);
            airDropMetadata.value().setLocation(block.getLocation());
            barrel.setMetadata(namespace.getKey(), airDropMetadata);

            LootTables lootTables = LootTablesList.get(new Random().nextInt(LootTablesList.size()));
            barrel.setLootTable(lootTables.getLootTable());
            barrel.customName(Component.text(lootTables.name()));
            barrel.update();
        }
    }

    @EventHandler
    void onInventoryClose(InventoryCloseEvent event) {
        Inventory inventory = event.getInventory();
        InventoryHolder holder = inventory.getHolder();

        if (holder instanceof BlockInventoryHolder && inventory.isEmpty()) {
            Block block = ((BlockInventoryHolder) holder).getBlock();

            if (block.getType() == Material.BARREL) {
                Barrel state = (Barrel) block.getState();
                AirDropMetadata airDropMetadata = getAirDrop(state);

                if (airDropMetadata != null) {
                    block.getWorld().setType(block.getLocation(), Material.AIR);
                    airDropMetadata.value().getDisplay().remove();
                    // TODO: Add break animation
                    // TODO: Remove chest from game's map
                }
            }
        }
    }
}
