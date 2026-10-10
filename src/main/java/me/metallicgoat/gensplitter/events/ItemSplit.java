package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerPickupDropEvent;
import de.marcely.bedwars.tools.Helper;
import java.util.function.BiConsumer;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ItemSplit implements Listener {

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerPickupDropEvent(PlayerPickupDropEvent event) {
    if (!event.isFromSpawner() || !ConfigValue.splitterEnabled || event instanceof PlayerPickupDropEventWrapper)
      return;

    final ItemStack pickedUpStack = event.getItem().getItemStack();

    if (!ConfigValue.splitSpawners.contains(pickedUpStack.getType()))
      return;

    final Player player = event.getPlayer();
    final Arena arena = event.getArena();
    final Location collectLocation = player.getLocation();

    getNearbyPlayers(arena, player, collectLocation, (split, splitLoc) -> {
      // ask api
      final PlayerPickupDropEventWrapper wrapper = new PlayerPickupDropEventWrapper(event, split);

      Bukkit.getPluginManager().callEvent(wrapper);

      if (wrapper.isCancelled())
        return;

      // all good, lets give it him
      split.getInventory().addItem(pickedUpStack);
      Helper.get().playPickupItemSound(splitLoc);
    });
  }

  public static void getNearbyPlayers(Arena arena, Player player, Location collectLocation, BiConsumer<Player, Location> callback) {
    for (Player split : arena.getPlayers()) {
      if (split == player ||
          GameAPI.get().getSpectatorByPlayer(split) != null ||
          split.getWorld() != collectLocation.getWorld() ||
          arena.getPlayerTeam(player) != arena.getPlayerTeam(split))
        continue;

      final Location splitLocation = split.getLocation();

      if (splitLocation.distance(collectLocation) > ConfigValue.splitRadius)
        continue;

      callback.accept(split, splitLocation);
    }
  }


  /**
   * Used to avoid an infinite loop when we simulate a pickup
   */
  private static class PlayerPickupDropEventWrapper extends PlayerPickupDropEvent {

    public PlayerPickupDropEventWrapper(PlayerPickupDropEvent wrapping, Player player) {
      super(player, wrapping.getArena(), wrapping.getDropType(), wrapping.getItem(), wrapping.isFromSpawner());
    }
  }
}