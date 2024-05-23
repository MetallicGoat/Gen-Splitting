package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class VoidDrops implements Listener {

  @EventHandler
  public void onVoidDrop(PlayerDropItemEvent event) {
    if (!ConfigValue.antiVoidDrops)
      return;

    if (isInArenaVoid(event.getPlayer()))
      event.setCancelled(true);
  }

  @EventHandler
  public void onInventoryInteract(InventoryClickEvent event) {
    if (!ConfigValue.antiVoidDrops)
      return;

    final Player clicker = (Player) event.getWhoClicked();

    if (isInArenaVoid(clicker)) {
      clicker.closeInventory();
      event.setCancelled(true);
    }
  }

  public boolean isInArenaVoid(Player player) {
    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null)
      return false;

    final Location currLoc = player.getLocation().clone().subtract(0, 0.1, 0);
    int pos = 0;

    while (pos < 5) {
      if (currLoc.getBlock().getType() != Material.AIR)
        return false;

      pos++;
      currLoc.subtract(0, 1, 0);
    }

    return true;
  }
}
