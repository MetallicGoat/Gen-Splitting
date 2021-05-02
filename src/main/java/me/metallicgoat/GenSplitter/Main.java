package me.metallicgoat.GenSplitter;

import me.metallicgoat.GenSplitter.Commands.commands;
import me.metallicgoat.GenSplitter.Commands.tabCompleter;
import me.metallicgoat.GenSplitter.Events.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class Main extends JavaPlugin {

    private static Main instance;
    private final ConsoleCommandSender console = Bukkit.getConsoleSender();
    private final Server server = getServer();
    private final List<String> splitMaterials = getConfig().getStringList("Split-Spawners");
    private final List<String> dropMaterials = getConfig().getStringList("Auto-Collect.Message-Materials");

    public void onEnable() {
        registerEvents();
        registerCommands();
        instance = this;
        PluginDescriptionFile pdf = this.getDescription();

        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        getConfig().options().copyDefaults();
        saveDefaultConfig();
    }

    private void registerEvents() {
        PluginManager manager = this.server.getPluginManager();
        manager.registerEvents(new onCollect(), this);
        manager.registerEvents(new onThrow(), this);
        manager.registerEvents(new onDrop(), this);
        manager.registerEvents(new onDeath(), this);
    }

    private void registerCommands() {
        getCommand("gen-splitter").setExecutor(new commands());
        getCommand("gen-splitter").setTabCompleter(new tabCompleter());
    }

    public static Main getInstance() {
        return instance;
    }

    public ConsoleCommandSender getConsole() {
        return console;
    }

    public List<String> getSplitMaterials() {
        return splitMaterials;
    }

    public List<String> getDropMaterials() {
        return dropMaterials;
    }

    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}