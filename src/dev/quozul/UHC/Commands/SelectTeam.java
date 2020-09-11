package dev.quozul.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Team;

public class SelectTeam implements Listener, CommandExecutor {

    private ItemStack createGuiItem(String name, Material mat, int amount) {
        final ItemStack i = new ItemStack(mat, Math.max(amount, 1));
        final ItemMeta iMeta = i.getItemMeta();

        iMeta.setDisplayName(name);
        i.setItemMeta(iMeta);

        return i;
    }

    private void createnopen(Player player) {
        final Inventory inv = Bukkit.createInventory(null, 18, "Choisir une équipe");

        inv.setItem(0, this.createGuiItem("§0§lNoir", Material.BLACK_WOOL, 1));
        inv.setItem(1, this.createGuiItem("§1§lBleu foncé", Material.BLUE_WOOL, 1));
        inv.setItem(2, this.createGuiItem("§2§lVert foncé", Material.GREEN_WOOL, 1));
        inv.setItem(3, this.createGuiItem("§3§lBleu ciel", Material.LIGHT_BLUE_WOOL, 1));
        inv.setItem(4, this.createGuiItem("§4§lMarron", Material.BROWN_WOOL, 1));
        inv.setItem(5, this.createGuiItem("§5§lViolet", Material.PURPLE_WOOL, 1));
        inv.setItem(6, this.createGuiItem("§6§lOr", Material.ORANGE_WOOL, 1));
        inv.setItem(7, this.createGuiItem("§7§lGris", Material.LIGHT_GRAY_WOOL, 1));
        inv.setItem(8, this.createGuiItem("§8§lGris foncé", Material.GRAY_WOOL, 1));
        inv.setItem(9, this.createGuiItem("§9§lBleu clair", Material.BLUE_WOOL, 1));
        inv.setItem(10, this.createGuiItem("§a§lVert clair", Material.LIME_WOOL, 1));
        inv.setItem(11, this.createGuiItem("§b§lCyan", Material.CYAN_WOOL, 1));
        inv.setItem(12, this.createGuiItem("§c§lRouge", Material.RED_WOOL, 1));
        inv.setItem(13, this.createGuiItem("§d§lMagenta", Material.MAGENTA_WOOL, 1));
        inv.setItem(14, this.createGuiItem("§e§lJaune", Material.YELLOW_WOOL, 1));
        inv.setItem(15, this.createGuiItem("§f§lBlanc", Material.WHITE_WOOL, 1));

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("Choisir une équipe")) {
            Player player = (Player) e.getWhoClicked();

            if (e.getCurrentItem() != null) {
                String itemName = e.getCurrentItem().getItemMeta().getDisplayName();
                String teamName = itemName.split("§l")[1];

                Team team = player.getScoreboard().getTeam(teamName);
                if (team != null) {
                    player.getScoreboard().getTeam(teamName).addEntry(player.getName());
                    player.sendMessage("§7Equipe " + itemName + "§r§7 rejoint !");
                    e.getView().close();
                } else
                    player.sendMessage("Une erreur est survenue lors de l'ajout à une équipe !");
            }
        }
    }

    /*@EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        if (e.getView().getTitle().equals("Choisir une équipe")) {
            Player player = (Player) e.getPlayer();

            int inTeams = 0;

            for (Team team : player.getScoreboard().getTeams()) {
                if (team.hasEntry(player.getName())) {
                    inTeams++;
                }
            }

            Bukkit.broadcastMessage(String.valueOf(inTeams));
            if (inTeams == 0)
                Bukkit.getServer().getScheduler().runTaskLater(Main.plugin, () -> {
                    createnopen(player);
                }, 1);
        }
    }*/

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        createnopen(e.getPlayer());
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        createnopen((Player) commandSender);

        return true;
    }

}
