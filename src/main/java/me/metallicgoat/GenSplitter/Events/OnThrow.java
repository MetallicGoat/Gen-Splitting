package me.metallicgoat.GenSplitter.Events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.GenSplitter.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class OnThrow implements Listener {

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Main plugin = Main.getInstance();
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        if (arena != null &&
                plugin.getSplitMaterials().contains(e.getItemDrop().getItemStack().getType().name())) {
            e.getItemDrop().setMetadata("thrown", new FixedMetadataValue(plugin, "yes!"));
        }
    }
}