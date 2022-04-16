package me.metallicgoat.gensplitter;

import de.marcely.bedwars.api.BedwarsAddon;
import de.marcely.bedwars.api.command.CommandHandler;
import de.marcely.bedwars.api.command.CommandsCollection;
import de.marcely.bedwars.api.command.SubCommand;
import me.metallicgoat.gensplitter.commands.ReloadCommand;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class GenSplitterAddon extends BedwarsAddon {

    private final GenSplitterPlugin plugin;

    public GenSplitterAddon(GenSplitterPlugin plugin) {
        super(plugin);

        this.plugin = plugin;
    }

    @Override
    public String getName(){
        return "Gen-Splitter";
    }
    
    void registerCommands(){
        addCommand(
                getCommandsRoot(),
                "reload",
                false,
                "",
                new ReloadCommand(this.plugin),
                "rl"
        );
    }

    private void addCommand(CommandsCollection parent, String name, boolean onlyPlayers, String usage, CommandHandler handler, String... aliases){
        addCommand(parent, null, name, onlyPlayers, usage, handler, aliases);
    }

    private void addCommand(CommandsCollection parent, @Nullable Consumer<SubCommand> callback, String name, boolean onlyPlayers, String usage, CommandHandler handler, String... aliases){
        final SubCommand cmd = parent.addCommand(name);

        if(cmd == null)
            return;

        cmd.setOnlyForPlayers(onlyPlayers);
        cmd.setUsage(usage);
        cmd.setHandler(handler);
        cmd.setAliases(aliases);

        if(callback != null)
            callback.accept(cmd);
    }
}
