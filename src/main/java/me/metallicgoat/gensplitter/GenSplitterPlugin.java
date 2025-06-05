package me.metallicgoat.gensplitter;

import de.marcely.bedwars.api.configuration.ConfigurationAPI;
import me.metallicgoat.gensplitter.config.Config;
import me.metallicgoat.gensplitter.config.ConfigValue;
import me.metallicgoat.gensplitter.events.AutoCollect;
import me.metallicgoat.gensplitter.events.ExpLevelSplit;
import me.metallicgoat.gensplitter.events.ItemSplit;
import me.metallicgoat.gensplitter.events.VoidDrops;
import me.metallicgoat.gensplitter.util.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class GenSplitterPlugin extends JavaPlugin {

  private static final int MBEDWARS_API_NUM = 203;
  private static final String MBEDWARS_API_NAME = "5.5.3";
  private static GenSplitterPlugin instance;
  private final Server server = getServer();
  private GenSplitterAddon addon;

  public static GenSplitterPlugin getInstance() {
    return instance;
  }

  public void onEnable() {
    int pluginId = 11787;
    new Metrics(this, pluginId);

    if (!validateMBedwars()) return;
    if (!registerAddon()) return;

    instance = this;
    Config.load();
    registerEvents();

    PluginDescriptionFile pdf = this.getDescription();
    log(
        "------------------------------",
        pdf.getName() + " For MBedwars",
        "By: " + pdf.getAuthors(),
        "Version: " + pdf.getVersion(),
        "------------------------------"
    );

    validateConfiguration();
  }

  private void validateConfiguration() {
    if (ConfigValue.splitterEnabled) {
      final Boolean smartSharing = (Boolean) ConfigurationAPI.get().getValue("smart-item-sharing-enabled");

      if (smartSharing.equals(true)) {
        ConfigurationAPI.get().setValue("smart-item-sharing-enabled", false);
        getLogger().warning("Smart item sharing (config \"smart-item-sharing-enabled\" in MBedwars' config.yml) has been disabled to prevent conflicts with GenSplitter.");
      }
    }
  }

  private void registerEvents() {
    PluginManager manager = this.server.getPluginManager();
    manager.registerEvents(new ItemSplit(), this);
    manager.registerEvents(new VoidDrops(), this);
    manager.registerEvents(new AutoCollect(), this);

    if (Bukkit.getPluginManager().getPlugin("MBedwars-LevelShop") != null) {
      manager.registerEvents(new ExpLevelSplit(), this);

      log("Hooked into \"MBedwars-LevelShop\"");
    }
  }

  public GenSplitterAddon getAddon() {
    return this.addon;
  }

  private boolean validateMBedwars() {
    try {
      final Class<?> apiClass = Class.forName("de.marcely.bedwars.api.BedwarsAPI");
      final int apiVersion = (int) apiClass.getMethod("getAPIVersion").invoke(null);

      if (apiVersion < MBEDWARS_API_NUM)
        throw new IllegalStateException();
    } catch (Exception e) {
      getLogger().warning("Sorry, your installed version of MBedwars is not supported. Please install at least v" + MBEDWARS_API_NAME);
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }

  private boolean registerAddon() {
    addon = new GenSplitterAddon(this);

    if (!addon.register()) {
      getLogger().warning("It seems like this addon has already been loaded. Please delete duplicates and try again.");
      Bukkit.getPluginManager().disablePlugin(this);

      return false;
    }

    return true;
  }

  private void log(String... args) {
    for (String s : args)
      getLogger().info(s);
  }
}