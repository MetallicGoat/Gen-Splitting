package me.metallicgoat.gensplitter;

import de.marcely.bedwars.api.BedwarsAddon;

public class GenSplitterAddon extends BedwarsAddon {

  private final GenSplitterPlugin plugin;

  public GenSplitterAddon(GenSplitterPlugin plugin) {
    super(plugin);

    this.plugin = plugin;
  }

  @Override
  public String getName() {
    return "Gen-Splitter";
  }
}
