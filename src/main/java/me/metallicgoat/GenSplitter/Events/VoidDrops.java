package me.metallicgoat.gensplitter.Events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.Util.Config.ConfigValue;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.List;

public class VoidDrops implements Listener {
    @EventHandler
    public void onVoidDrop(PlayerDropItemEvent e) {
        final Player player = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if(arena != null && ConfigValue.antiVoidDrops){
            List<Block> blocks = new ArrayList<>();
            blocks.add(player.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock());

            for(int i = 1; i <= 4; ++i) {
                blocks.add(player.getLocation().clone().subtract(0.0D, i, 0.0D).getBlock());
            }

            if (blocks.stream().allMatch((b) -> b.getType().equals(Material.AIR))) {
                e.setCancelled(true);
            }
        }
    }
}
