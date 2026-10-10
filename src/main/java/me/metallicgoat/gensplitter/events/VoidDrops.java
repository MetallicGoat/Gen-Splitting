package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.arena.ArenaStatus;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
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
    if (player.getGameMode() != GameMode.SURVIVAL || player.isFlying())
      return false;

    final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

    if (arena == null || arena.getStatus() != ArenaStatus.RUNNING)
      return false;

    final Location[] locs = getPlayerBoundaryBasedLocations(player);
    final World world = player.getWorld();

    // check if there's even any block at x/z (fast check)
    {
      final int minHeight = Helper.get().getMinHeight(world);
      boolean allAir = true;

      for (int i=0; i<locs.length; i++) {
        final Location loc = locs[i];

        if (world.getHighestBlockYAt(loc) > minHeight)
          allAir = false;
        else // make processing faster in case we pass next step
          locs[i] = null;
      }

      if (allAir)
        return true;
    }

    // check blocks below (slowish)
    {
      for (Location loc : locs) {
        if (loc == null)
          continue;

        for (int i = 0; i < 5; i++) {
          if (loc.getBlock().getType().isSolid())
            return false;

          loc.subtract(0, 1, 0);
        }

        if (loc.getBlock().getType().isSolid())
          return false;
      }
    }

    return true;
  }

  private static Location[] getPlayerBoundaryBasedLocations(Player player) {
    final Location loc = player.getLocation(); // center
    final double widthHalf = 0.6D /* from minecraft.wiki */ / 2D;
    final int minX = (int) Math.floor(loc.getX() - widthHalf);
    final int minZ = (int) Math.floor(loc.getZ() - widthHalf);
    final int maxX = (int) Math.floor(loc.getX() + widthHalf);
    final int maxZ = (int) Math.floor(loc.getZ() + widthHalf);
    final int count = (maxX - minX + 1) * (maxZ - minZ + 1);
    final Location[] locations = new Location[count];
    int i = 0;

    for (int x = minX; x <= maxX; x++) {
      for (int z = minZ; z <= maxZ; z++) {
        locations[i++] = new Location(loc.getWorld(), x, loc.getY(), z);
      }
    }

    return locations;
  }
}