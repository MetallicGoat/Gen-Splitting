package me.metallicgoat.GenSplitter.Events;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.GenSplitter.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class onThrow implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Main plugin = Main.getInstance();
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getArena(p);
        if (arena != null &&
                plugin.getSplitMaterials().contains(e.getItemDrop().getItemStack().getType().name())) {
            e.getItemDrop().setMetadata("thrown", new FixedMetadataValue(plugin, "yes!"));
        }
    }
}