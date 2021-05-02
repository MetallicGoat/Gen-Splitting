package me.metallicgoat.GenSplitter.Events;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import me.metallicgoat.GenSplitter.Main;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

public class onDrop implements Listener {
    @EventHandler
    public void onVoidDrop(PlayerDropItemEvent e) {
        Main plugin = Main.getInstance();
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getArena(p);
        boolean enabled = plugin.getConfig().getBoolean("Anti-Void-Drops");
        if(arena != null && enabled){
            if(p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.AIR) {
                e.setCancelled(true);
            }
        }
    }
}
