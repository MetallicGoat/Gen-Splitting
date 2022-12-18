package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class MarkThrow implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (ConfigValue.splitterEnabled && arena != null
                && ConfigValue.splitSpawners.contains(event.getItemDrop().getItemStack().getType())) {

            event.getItemDrop().setMetadata("thrown", new FixedMetadataValue(GenSplitterPlugin.getInstance(), "yes!"));
        }
    }
}