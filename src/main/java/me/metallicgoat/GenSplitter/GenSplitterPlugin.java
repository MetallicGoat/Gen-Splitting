package me.metallicgoat.gensplitter;

import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.gensplitter.Commands.Commands;
import me.metallicgoat.gensplitter.Commands.TabComp;
import me.metallicgoat.gensplitter.Events.*;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class GenSplitterPlugin extends JavaPlugin {

    private static GenSplitterAddon addon;
    private static GenSplitterPlugin instance;
    private final Server server = getServer();

    public void onEnable() {
        registerEvents();
        //registerCommands();
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

        registerAddon();
    }

    private void registerEvents() {
        PluginManager manager = this.server.getPluginManager();
        manager.registerEvents(new ItemSplit(), this);
        manager.registerEvents(new MarkThrow(), this);
        manager.registerEvents(new VoidDrops(), this);
        manager.registerEvents(new AutoCollect(), this);
    }

    private void registerCommands() {
        getCommand("gen-splitter").setExecutor(new Commands());
        getCommand("gen-splitter").setTabCompleter(new TabComp());
    }

    public static GenSplitterPlugin getInstance() {
        return instance;
    }

    public static GenSplitterAddon getAddon() {
        return addon;
    }

    private boolean registerAddon(){
        addon = new GenSplitterAddon(this);

        if(!addon.register()){
            getLogger().warning("It seems like this addon has already been loaded. Please delete duplicates and try again.");
            Bukkit.getPluginManager().disablePlugin(this);

            return false;
        }

        return true;
    }

    public boolean copyResource(String internalPath, File out) throws IOException {
        if(!out.exists() || out.length() == 0){
            try(InputStream is = getResource(internalPath)){
                if(is == null){
                    getLogger().warning("Your plugin seems to be broken (Failed to find internal file " + internalPath + ")");
                    return false;
                }

                out.createNewFile();

                try(FileOutputStream os = new FileOutputStream(out)){
                    Helper.get().copy(is, os);
                }

                return true;
            }
        }

        return false;
    }

    private void log(String ...args) {
        for(String s : args)
            getLogger().info(s);
    }
}