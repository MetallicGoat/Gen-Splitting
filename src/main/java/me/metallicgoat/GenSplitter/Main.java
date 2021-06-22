package me.metallicgoat.GenSplitter;

import me.metallicgoat.GenSplitter.Commands.Commands;
import me.metallicgoat.GenSplitter.Commands.TabComp;
import me.metallicgoat.GenSplitter.Events.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
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

        int pluginId = 11787;
        Metrics metrics = new Metrics(this, pluginId);

        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );

        loadConfig();
    }

    private void registerEvents() {
        PluginManager manager = this.server.getPluginManager();
        manager.registerEvents(new OnCollect(), this);
        manager.registerEvents(new OnThrow(), this);
        manager.registerEvents(new OnDrop(), this);
        manager.registerEvents(new OnDeath(), this);
    }

    private void registerCommands() {
        getCommand("gen-splitter").setExecutor(new Commands());
        getCommand("gen-splitter").setTabCompleter(new TabComp());
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

    private void loadConfig(){
        saveDefaultConfig();
        File configFile = new File(getDataFolder(), "config.yml");

        try {
            ConfigUpdater.update(this, "config.yml", configFile, Arrays.asList("Nothing", "here"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        reloadConfig();
    }

    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}