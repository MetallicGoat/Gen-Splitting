package me.metallicgoat.gensplitter.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

public class TabComp implements org.bukkit.command.TabCompleter {
    List<String> arguments = new ArrayList<String>();

    public List<String> onTabComplete(CommandSender sender, Command c, String s, String[] args) {
        if (this.arguments.isEmpty() &&
                sender.hasPermission("gensplitter.admin")) {
            this.arguments.add("reload");
        }
        List<String> result = new ArrayList<String>();
        if (args.length == 1) {
            for (String a : this.arguments) {
                if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                    result.add(a);
                }
            }
            return result;
        }
        return null;
    }
}
