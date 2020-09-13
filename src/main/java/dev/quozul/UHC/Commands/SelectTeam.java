package dev.quozul.UHC.Commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.Banner;
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

import java.util.*;

public class SelectTeam implements Listener, CommandExecutor {

    private static Map<ChatColor, DyeColor> ChatToDye = new HashMap<>();

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

    private ItemStack createGuiItem(String name, Material mat, int amount, List<String> lore) {
        final ItemStack i = new ItemStack(mat, Math.max(amount, 1));
        final ItemMeta iMeta = i.getItemMeta();

        iMeta.setDisplayName(name);
        iMeta.setLore(lore);
        i.setItemMeta(iMeta);

        return i;
    }

    private void createnopen(Player player) {
        final Inventory inv = Bukkit.createInventory(null, 18, "Choisir une équipe");

        Set<Team> teams = player.getScoreboard().getTeams();
        List<Team> teamList = new ArrayList<>(teams);

        for (Team team : teamList) {
            int index = teamList.indexOf(team);

            Material banner = Material.getMaterial(ChatToDye.get(team.getColor()) + "_BANNER");
            if (banner == null) banner = Material.WHITE_BANNER;

            inv.setItem(index,
                    this.createGuiItem(
                            team.getColor() + "§l" + team.getDisplayName(),
                            banner, team.getEntries().size(),
                            Collections.singletonList(team.getName())
                    )
            );
        }

        player.openInventory(inv);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (e.getView().getTitle().equals("Choisir une équipe")) {
            Player player = (Player) e.getWhoClicked();

            if (e.getCurrentItem() != null) {
                String teamName = e.getCurrentItem().getItemMeta().getLore().get(0);

                Team team = player.getScoreboard().getTeam(teamName);
                if (team != null) {
                    team.addEntry(player.getName());
                    player.sendMessage("§7Equipe " + team.getColor() + team.getDisplayName() + "§r§7 rejoint !");
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
