package dev.quozul.UHC;

import co.aikar.commands.PaperCommandManager;
import dev.quozul.UHC.Commands.*;
import dev.quozul.UHC.Listeners.*;
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
        manager.registerCommand(new StartCommand());
        manager.registerCommand(new RegenWorlds());
        manager.registerCommand(new TeamCommand(manager));

        manager.registerCommand(new RoomCommand(manager));
        manager.registerCommand(new PartyCommand());

        // Register events
        getServer().getPluginManager().registerEvents(new SelectTeam(), this);
        getServer().getPluginManager().registerEvents(new GameListeners(), this);
        getServer().getPluginManager().registerEvents(new GameStart(), this);
        getServer().getPluginManager().registerEvents(new GameTick(), this);
        getServer().getPluginManager().registerEvents(new GameEnd(), this);
        getServer().getPluginManager().registerEvents(new GameBossBars(), this);
        getServer().getPluginManager().registerEvents(new SpawnChest(), this);

        plugin.saveDefaultConfig();

        RegenWorlds.generateWorlds();
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

}
