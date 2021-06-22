package me.metallicgoat.GenSplitter.Events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.GenSplitter.Main;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.List;

public class OnDrop implements Listener {
    @EventHandler
    public void onVoidDrop(PlayerDropItemEvent e) {
        Main plugin = Main.getInstance();
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        boolean enabled = plugin.getConfig().getBoolean("Anti-Void-Drops");
        if(arena != null && enabled){
            List<Block> blocks = new ArrayList();
            blocks.add(p.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock());

            for(int i = 1; i <= 4; ++i) {
                blocks.add(p.getLocation().clone().subtract(0.0D, i, 0.0D).getBlock());
            }

            if (blocks.stream().allMatch((b) -> b.getType().equals(Material.AIR))) {
                e.setCancelled(true);
            }
        }
    }
}
