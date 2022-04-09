package me.metallicgoat.gensplitter;

import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.gensplitter.Events.*;
import me.metallicgoat.gensplitter.Util.Config.Config;
import me.metallicgoat.gensplitter.Util.Metrics;
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

    private static final byte MBEDWARS_API_NUM = 10;
    private static final String MBEDWARS_API_NAME = "5.0.9";

    private GenSplitterAddon addon;
    private static GenSplitterPlugin instance;
    private final Server server = getServer();

    public void onEnable() {
        int pluginId = 11787;
        new Metrics(this, pluginId);

        if(!validateMBedwars()) return;
        if(!registerAddon()) return;

        instance = this;
        Config.save();
        registerEvents();

        this.addon.registerCommands();

        PluginDescriptionFile pdf = this.getDescription();
        log(
                "------------------------------",
                pdf.getName() + " For MBedwars",
                "By: " + pdf.getAuthors(),
                "Version: " + pdf.getVersion(),
                "------------------------------"
        );
    }

    private void registerEvents() {
        PluginManager manager = this.server.getPluginManager();
        manager.registerEvents(new ItemSplit(), this);
        manager.registerEvents(new MarkThrow(), this);
        manager.registerEvents(new VoidDrops(), this);
        manager.registerEvents(new AutoCollect(), this);
    }

    public static GenSplitterPlugin getInstance() {
        return instance;
    }

    public GenSplitterAddon getAddon() {
        return this.addon;
    }

    private boolean validateMBedwars(){
        try{
            final Class<?> apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
            final int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

            if(apiVersion < MBEDWARS_API_NUM)
                throw new IllegalStateException();
        }catch(Exception e){
            getLogger().warning("Sorry, your installed version of MBedwars is not supported. Please install at least v" + MBEDWARS_API_NAME);
            Bukkit.getPluginManager().disablePlugin(this);

            return false;
        }

        return true;
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