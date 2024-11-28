package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerPickupDropEvent;
import de.marcely.bedwars.tools.Helper;
import java.util.concurrent.ThreadLocalRandom;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class ItemSplit implements Listener {

  private static final Sound PICKUP_SOUND = Helper.get().getSoundByName("ENTITY_ITEM_PICKUP");

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerPickupDropEvent(PlayerPickupDropEvent event) {
    if (!event.isFromSpawner() || !ConfigValue.splitterEnabled || event instanceof PlayerPickupDropEventWrapper)
      return;

    final ItemStack pickedUpStack = event.getItem().getItemStack();

    if (!ConfigValue.splitSpawners.contains(pickedUpStack.getType()))
      return;

    final Player player = event.getPlayer();
    final Arena arena = event.getArena();

    // give item to all players
    final Location collectLocation = player.getLocation();

    for (Player split : arena.getPlayers()) {
      if (split == player || split.getGameMode() == GameMode.SPECTATOR || split.getWorld() != collectLocation.getWorld())
        continue;

      final Location splitLocation = split.getLocation();

      if (splitLocation.distance(collectLocation) > ConfigValue.splitRadius)
        continue;

      // ask api
      final PlayerPickupDropEventWrapper wrapper = new PlayerPickupDropEventWrapper(event, split);

      Bukkit.getPluginManager().callEvent(wrapper);

      if (wrapper.isCancelled())
        continue;

      // all good, lets give it him
      split.getInventory().addItem(pickedUpStack);

      if (PICKUP_SOUND != null) // volume and pitch copied from server code
        collectLocation.getWorld().playSound(collectLocation, PICKUP_SOUND, .1F, ThreadLocalRandom.current().nextFloat() * 1.4f + 1.3f);
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