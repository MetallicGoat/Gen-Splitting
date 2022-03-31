package me.metallicgoat.gensplitter.Commands;

import de.marcely.bedwars.api.command.CommandHandler;
import de.marcely.bedwars.api.command.SubCommand;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import org.bukkit.plugin.Plugin;

public abstract class BaseCommandHandler implements CommandHandler {

    protected final GenSplitterPlugin plugin;

    public BaseCommandHandler(GenSplitterPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Plugin getPlugin() {
        return this.plugin;
    }

    @Override
    public void onRegister(SubCommand cmd) {
    }
}