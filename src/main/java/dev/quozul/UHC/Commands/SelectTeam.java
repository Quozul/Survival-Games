package dev.quozul.UHC.Commands;

import dev.quozul.UHC.Main;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scoreboard.Team;
import org.jetbrains.annotations.NotNull;

import java.util.*;

class SelectTeamHolder implements InventoryHolder {
    private Inventory inventory;

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }
}

public class SelectTeam implements Listener {

    private static Map<ChatColor, DyeColor> ChatToDye = new HashMap<>();
    private final static @NotNull NamespacedKey namespace = new NamespacedKey(Main.plugin, "data_container");

    public SelectTeam() {
        ChatToDye.put(ChatColor.AQUA, DyeColor.CYAN);
        ChatToDye.put(ChatColor.BLACK, DyeColor.BLACK);
        ChatToDye.put(ChatColor.BLUE, DyeColor.LIGHT_BLUE);
        ChatToDye.put(ChatColor.DARK_AQUA, DyeColor.MAGENTA);
        ChatToDye.put(ChatColor.DARK_BLUE, DyeColor.BLUE);
        ChatToDye.put(ChatColor.DARK_GRAY, DyeColor.GRAY);
        ChatToDye.put(ChatColor.DARK_GREEN, DyeColor.GREEN);
        ChatToDye.put(ChatColor.DARK_PURPLE, DyeColor.PURPLE);
        ChatToDye.put(ChatColor.DARK_RED, DyeColor.BROWN);
        ChatToDye.put(ChatColor.GOLD, DyeColor.ORANGE);
        ChatToDye.put(ChatColor.GRAY, DyeColor.LIGHT_GRAY);
        ChatToDye.put(ChatColor.GREEN, DyeColor.LIME);
        ChatToDye.put(ChatColor.LIGHT_PURPLE, DyeColor.PINK);
        ChatToDye.put(ChatColor.RED, DyeColor.RED);
        ChatToDye.put(ChatColor.WHITE, DyeColor.WHITE);
        ChatToDye.put(ChatColor.YELLOW, DyeColor.YELLOW);
    }

    private static ItemStack createGuiItem(Component name, Material material, int amount, Team team) {
        final ItemStack item = new ItemStack(material, Math.max(amount, 1));

        item.editMeta(meta -> {
            meta.displayName(name);
            meta.lore(List.of(
                    Component.text(amount)
                            .appendSpace()
                            .append(Component.text("joueurs dans cette équipe."))
                            .color(NamedTextColor.GRAY)
                            .decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE),
                    Component.text("Clic-gauche pour rejoindre")
            ));
            meta.getPersistentDataContainer().set(namespace, PersistentDataType.STRING, team.getName());
        });

        return item;
    }

    public static void createAndOpen(Player player) {
        SelectTeamHolder holder = new SelectTeamHolder();
        Inventory inventory = Bukkit.createInventory(holder, 18, Component.text("Choisir une équipe"));
        holder.setInventory(inventory);

        Set<Team> teams = player.getScoreboard().getTeams();
        List<Team> teamList = new ArrayList<>(teams);

        for (Team team : teamList) {
            int index = teamList.indexOf(team);

            // TODO: Get closest banner's color from team's color
            Material banner = Material.getMaterial(ChatToDye.get(team.getColor()) + "_BANNER");
            if (banner == null) banner = Material.WHITE_BANNER;

            inventory.setItem(index, createGuiItem(team.displayName(), banner, team.getEntries().size(), team));
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory().getHolder() instanceof SelectTeamHolder) {
            Player player = (Player) event.getWhoClicked();

            if (event.getCurrentItem() != null) {
                String teamName = event.getCurrentItem()
                        .getItemMeta()
                        .getPersistentDataContainer()
                        .get(namespace, PersistentDataType.STRING);

                Team team = player.getScoreboard().getTeam(teamName);

                if (team != null) {
                    team.addEntry(player.getName());
                    player.sendMessage("§7Equipe " + team.getColor() + team.getDisplayName() + "§r§7 rejoint !");
                    event.getView().close();
                } else
                    player.sendMessage("Une erreur est survenue lors de l'ajout à une équipe !");
            }
        }
    }

}
