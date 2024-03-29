package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

import java.util.ArrayList;
import java.util.List;

public class VoidDrops implements Listener {

  @EventHandler
  public void onVoidDrop(PlayerDropItemEvent event) {
    if (isInArenaVoid(event.getPlayer()))
      event.setCancelled(true);
  }

  @EventHandler
  public void onInventoryInteract(InventoryClickEvent event) {
    if (isInArenaVoid((Player) event.getWhoClicked())) {
      event.getWhoClicked().closeInventory();
      event.setCancelled(true);
    }
  }

  public boolean isInArenaVoid(Player player) {
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena != null && ConfigValue.antiVoidDrops) {
      final List<Block> blocks = new ArrayList<>();
      blocks.add(player.getLocation().clone().subtract(0.0D, 0.1D, 0.0D).getBlock());

      for (int i = 1; i <= 4; ++i)
        blocks.add(player.getLocation().clone().subtract(0.0D, i, 0.0D).getBlock());

      return blocks.stream().allMatch((b) -> b.getType().equals(Material.AIR));
    }

    return false;
  }
}
