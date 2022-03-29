package me.metallicgoat.gensplitter.Commands;

import me.metallicgoat.gensplitter.GenSplitterPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    //TODO move to addon commands

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        GenSplitterPlugin plugin = GenSplitterPlugin.getInstance();
        if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("gensplitter.admin")) {
                    plugin.reloadConfig();
                    sender.sendMessage(ChatColor.GREEN + "[Gen-Splitter] Config reloaded!");
                    return true;
                }
            } else {
                plugin.reloadConfig();
                plugin.getLogger().info("[Gen-Splitter] Config reloaded!");
                return true;
            }
        }
        return false;
    }
}