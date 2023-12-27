package me.metallicgoat.gensplitter.config;

import de.marcely.bedwars.tools.Helper;
import de.marcely.bedwars.tools.YamlConfigurationDescriptor;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Config {

  public static final String ADDON_VERSION = GenSplitterPlugin.getInstance().getDescription().getVersion();
  public static String CURRENT_CONFIG_VERSION = null;

  public static File getFile() {
    return new File(GenSplitterPlugin.getInstance().getAddon().getDataFolder(), "config.yml");
  }

  public static void load() {
    synchronized (Config.class) {
      try {
        loadUnchecked();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  public static void loadUnchecked() throws Exception {
    final File file = getFile();

    if (!file.exists()) {
      save();
      return;
    }

    // load it
    final FileConfiguration config = new YamlConfiguration();

    try {
      config.load(file);
    } catch (Exception e) {
      e.printStackTrace();
    }

    ConfigValue.splitterEnabled = config.getBoolean("Gen-Splitter");

    ConfigValue.splitSpawners = parseMaterialList(config.getStringList("Split-Spawners"));

    ConfigValue.splitRadius = config.getDouble("Split-Radius");

    ConfigValue.antiVoidDrops = config.getBoolean("Anti-Void-Drops");

    ConfigValue.autoCollectEnabled = config.getBoolean("Auto-Collect.Enabled");
    ConfigValue.autoCollectPercentKept = config.getInt("Auto-Collect.Percentage-Kept");
    ConfigValue.autoCollectMessageMaterials = parseMaterialList(config.getStringList("Auto-Collect.Message-Materials"));
    ConfigValue.autoCollectMessage = config.getString("Auto-Collect.Message");


    // auto update file if newer version
    {
      CURRENT_CONFIG_VERSION = config.getString("file-version");

      if (CURRENT_CONFIG_VERSION == null || !CURRENT_CONFIG_VERSION.equals(ADDON_VERSION)) {
        loadOldConfigs(config);
        save();
      }
    }
  }

  private static void save() throws Exception {
    final YamlConfigurationDescriptor config = new YamlConfigurationDescriptor();

    config.addComment("Used for auto-updating the config file. Ignore it");
    config.set("file-version", ADDON_VERSION);

    config.addEmptyLine();

    config.addComment("Whether or not gen-splitting is enabled");
    config.addComment("MAKE SURE MBedwars smart-item-sharing is DISABLED");
    config.set("Gen-Splitter", ConfigValue.splitterEnabled);

    config.addEmptyLine();

    config.addComment("Gens that can split");
    config.addComment("https://helpch.at/docs/1.8/org/bukkit/Material.html");
    config.set("Split-Spawners", buildMaterialNameList(ConfigValue.splitSpawners));

    config.addEmptyLine();

    config.addComment("How close players have to be together in order for items to split");
    config.addComment("1.5 - 2.5 is recommended");
    config.set("Split-Radius", ConfigValue.splitRadius);

    config.addEmptyLine();

    config.addComment("Prevents people from dropping items in the void when falling to their death");
    config.addComment("Basically useless is Auto-Collect is disabled");
    config.set("Anti-Void-Drops", ConfigValue.antiVoidDrops);

    config.addEmptyLine();

    config.addComment("Overrides MBedwars auto-pickup system");
    config.addComment("The killer only receives this message if a victim has items");
    config.addComment("Only Items that the victim had will be included in the list");
    config.addComment("So it will not say Iron +0 or whatever");
    config.set("Auto-Collect.Enabled", ConfigValue.autoCollectEnabled);
    config.set("Auto-Collect.Percentage-Kept", ConfigValue.autoCollectPercentKept);
    config.set("Auto-Collect.Message-Materials", buildMaterialNameList(ConfigValue.autoCollectMessageMaterials));
    config.set("Auto-Collect.Message", ConfigValue.autoCollectMessage);

    config.save(getFile());
  }

  public static void loadOldConfigs(FileConfiguration config) {
    // Nothing here yet :)
  }

  private static List<String> buildMaterialNameList(List<Material> materials) {
    final List<String> materialNames = new ArrayList<>();

    for (Material material : materials)
      materialNames.add(material.name());

    return materialNames;
  }

  private static List<Material> parseMaterialList(List<String> stringMaterials) {
    final List<Material> materialList = new ArrayList<>();

    for (String material : stringMaterials) {

      final Material parsed = Helper.get().getMaterialByName(material);

      if (parsed != null)
        materialList.add(parsed);
    }

    return materialList;
  }
}
