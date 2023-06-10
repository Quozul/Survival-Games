package dev.quozul.UHC;

import co.aikar.commands.PaperCommandManager;
import dev.quozul.UHC.Commands.PartyCommand;
import dev.quozul.UHC.Commands.RoomCommand;
import dev.quozul.UHC.Listeners.*;
import dev.quozul.minigame.LobbyListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;

public class Main extends JavaPlugin {

    public static Main plugin;

    @Override
    public void onEnable() {
        plugin = this;

        // Init command manager
        PaperCommandManager manager = new PaperCommandManager(this);
        manager.enableUnstableAPI("brigadier");
        manager.enableUnstableAPI("help");
        manager.getLocales().setDefaultLocale(Locale.FRENCH);

        // Register commands
        manager.registerCommand(new PartyCommand(manager));
        manager.registerCommand(new RoomCommand(manager));

        // Register events
        getServer().getPluginManager().registerEvents(new LobbyListener(), this);

        getServer().getPluginManager().registerEvents(new GameListeners(), this);
        getServer().getPluginManager().registerEvents(new GameStart(), this);
        getServer().getPluginManager().registerEvents(new GameEnd(), this);
        getServer().getPluginManager().registerEvents(new GameBossBars(), this);
        getServer().getPluginManager().registerEvents(new SpawnChest(), this);

        plugin.saveDefaultConfig();
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

}
