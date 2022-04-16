package me.metallicgoat.gensplitter.commands;

import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.util.config.Config;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class ReloadCommand extends BaseCommandHandler {

    public ReloadCommand(GenSplitterPlugin plugin){
        super(plugin);
    }

    @Override
    public void onFire(CommandSender sender, String fullUsage, String[] args){
        Config.load();
        Message.build("[Gen-Splitter] Config reloaded!").send(sender);
    }

    @Override
    public @Nullable List<String> onAutocomplete(CommandSender sender, String[] args){
        return Collections.emptyList();
    }
}
