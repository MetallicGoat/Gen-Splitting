package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.util.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class MarkThrow implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);

        if (ConfigValue.splitterEnabled && arena != null
                && ConfigValue.splitSpawners.contains(e.getItemDrop().getItemStack().getType())) {

            e.getItemDrop().setMetadata("thrown", new FixedMetadataValue(GenSplitterPlugin.getInstance(), "yes!"));
        }
    }
}