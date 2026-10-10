package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.levelshop.LevelShopAPI;
import de.marcely.bedwars.api.levelshop.PlayerPickupLevelItemEvent;
import de.marcely.bedwars.api.levelshop.PlayerPickupOrbEvent;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class ExpLevelSplit implements Listener {

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerPickupOrbEvent(PlayerPickupOrbEvent event) {
    if (!ConfigValue.splitterEnabled || !ConfigValue.splitLevelShopAddon || event instanceof PlayerPickupOrbEventWrapper)
      return;

    final Player player = event.getPlayer();
    final Arena arena = event.getArena();
    final Location collectLocation = player.getLocation();

    ItemSplit.getNearbyPlayers(arena, player, collectLocation, (split, splitLoc) -> {
      // ask api
      final PlayerPickupOrbEventWrapper wrapper = new PlayerPickupOrbEventWrapper(event, split);

      Bukkit.getPluginManager().callEvent(wrapper);

      if (wrapper.getLevelAmount() == 0)
        return;

      // all good, lets give it him
      final int newLevel = split.getLevel() + wrapper.getLevelAmount();

      LevelShopAPI.get().setEarnedLevel(split, splitLoc, newLevel);
    });
  }

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void PlayerPickupLevelItemEvent(PlayerPickupLevelItemEvent event) {
    if (!ConfigValue.splitterEnabled || !ConfigValue.splitLevelShopAddon || event instanceof PlayerPickupLevelItemEventWrapper)
      return;

    final Player player = event.getPlayer();
    final Arena arena = event.getArena();
    final Location collectLocation = player.getLocation();

    ItemSplit.getNearbyPlayers(arena, player, collectLocation, (split, splitLoc) -> {
      // ask api
      final PlayerPickupLevelItemEventWrapper wrapper = new PlayerPickupLevelItemEventWrapper(event, split);

      Bukkit.getPluginManager().callEvent(wrapper);

      if (wrapper.getPerItemLevelAmount() == 0)
        return;

      // all good, lets give it him
      final int newLevel = split.getLevel() + wrapper.getPerItemLevelAmount();

      LevelShopAPI.get().setEarnedLevel(split, splitLoc, newLevel);
    });
  }


  /**
   * Used to avoid an infinite loop when we simulate a pickup
   */
  private static class PlayerPickupOrbEventWrapper extends PlayerPickupOrbEvent {

    public PlayerPickupOrbEventWrapper(PlayerPickupOrbEvent wrapping, Player player) {
      super(wrapping, player);
    }
  }

  private static class PlayerPickupLevelItemEventWrapper extends PlayerPickupLevelItemEvent {

    public PlayerPickupLevelItemEventWrapper(PlayerPickupLevelItemEvent wrapping, Player player) {
      super(wrapping, player);
    }
  }
}
