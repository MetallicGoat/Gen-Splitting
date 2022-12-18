package me.metallicgoat.gensplitter.util.config;

import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.util.config.updater.ConfigUpdater;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Config {

    public static File getFile(){
        return new File(GenSplitterPlugin.getInstance().getAddon().getDataFolder(), "config.yml");
    }

    public static void save(){
        GenSplitterPlugin.getInstance().getAddon().getDataFolder().mkdirs();

        synchronized(Config.class){
            try{
                saveUnchecked();
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private static void saveUnchecked() throws IOException {
        final GenSplitterPlugin plugin = GenSplitterPlugin.getInstance();

        final File file = getFile();

        if(!file.exists())
            plugin.copyResource("config.yml", file);

        try {
            ConfigUpdater.update(plugin, "config.yml", file, Collections.emptyList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        load();
    }

    public static FileConfiguration getConfig(){
        final FileConfiguration config = new YamlConfiguration();

        try{
            config.load(getFile());
        }catch(Exception e){
            e.printStackTrace();
        }

        return config;
    }

    public static void load(){
        final FileConfiguration mainConfig = getConfig();

        ConfigValue.splitterEnabled = mainConfig.getBoolean("Gen-Splitter");

        ConfigValue.splitSpawners = parseMaterialList(mainConfig.getStringList("Split-Spawners"));

        ConfigValue.splitRadius = mainConfig.getDouble("Split-Radius");

        ConfigValue.antiVoidDrops = mainConfig.getBoolean("Anti-Void-Drops");

        ConfigValue.autoCollectEnabled = mainConfig.getBoolean("Auto-Collect.Enabled");
        ConfigValue.autoCollectPercentKept = mainConfig.getInt("Auto-Collect.Percentage-Kept");
        ConfigValue.autoCollectMessageMaterials = parseMaterialList(mainConfig.getStringList("Auto-Collect.Message-Materials"));
        ConfigValue.autoCollectMessage = mainConfig.getString("Auto-Collect.Message");

    }

    private static List<Material> parseMaterialList (List<String> stringMaterials){
        final List<Material> materialList = new ArrayList<>();

        for(String material : stringMaterials){

            final Material parsed = Helper.get().getMaterialByName(material);

            if(parsed != null)
                materialList.add(parsed);
        }

        return materialList;
    }
}
