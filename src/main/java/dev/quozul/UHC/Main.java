package dev.quozul.UHC;

import co.aikar.commands.PaperCommandManager;
import dev.quozul.UHC.Commands.PartyCommand;
import dev.quozul.UHC.Commands.RegenWorlds;
import dev.quozul.UHC.Commands.SelectTeam;
import dev.quozul.UHC.Commands.StartCommand;
import dev.quozul.UHC.Listeners.*;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
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
        manager.registerCommand(new PartyCommand(manager));

        // Register events
        getServer().getPluginManager().registerEvents(new SelectTeam(), this);
        getServer().getPluginManager().registerEvents(new GameListeners(), this);
        getServer().getPluginManager().registerEvents(new GameStart(), this);
        getServer().getPluginManager().registerEvents(new GameTick(), this);
        getServer().getPluginManager().registerEvents(new GameEnd(), this);
        getServer().getPluginManager().registerEvents(new GameBossBars(), this);

        SpawnChest spawnChest = new SpawnChest();
        manager.registerCommand(spawnChest);
        getServer().getPluginManager().registerEvents(spawnChest, this);

        plugin.saveDefaultConfig();

        try {
            RegenWorlds.generateWorlds();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        plugin = null;
    }

}
