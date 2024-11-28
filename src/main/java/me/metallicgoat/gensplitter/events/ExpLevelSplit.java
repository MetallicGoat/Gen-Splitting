package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.levelshop.Util;
import de.marcely.bedwars.levelshop.api.PlayerPickupOrbEvent;
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

public class ExpLevelSplit implements Listener {

  private static final Sound PICKUP_SOUND = Helper.get().getSoundByName("ENTITY_EXPERIENCE_ORB_PICKUP");
  private static final Sound LEVELUP_SOUND = Helper.get().getSoundByName("ENTITY_PLAYER_LEVELUP");

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerPickupOrbEvent(PlayerPickupOrbEvent event) {
    if (!ConfigValue.splitterEnabled || !ConfigValue.splitLevelShopAddon || event instanceof PlayerPickupOrbEventWrapper)
      return;

    final Player player = event.getPlayer();
    final Arena arena = event.getArena();

    final Location collectLocation = player.getLocation();

    for (Player split : arena.getPlayers()) {
      if (split == player || split.getGameMode() == GameMode.SPECTATOR || split.getWorld() != collectLocation.getWorld())
        continue;

      final Location splitLocation = split.getLocation();

      if (splitLocation.distance(collectLocation) > ConfigValue.splitRadius)
        continue;

      // ask api
      final PlayerPickupOrbEventWrapper wrapper = new PlayerPickupOrbEventWrapper(event, split);

      Bukkit.getPluginManager().callEvent(wrapper);

      if (wrapper.getLevelAmount() == 0)
        continue;

      // all good, lets give it him
      final int newLevel = split.getLevel() + wrapper.getLevelAmount();

      split.setLevel(newLevel);
      split.setExp(0);
      split.setTotalExperience(Util.getTotalExp(newLevel)); // fix desyncs

      // volume and pitch copied from server code
      if (PICKUP_SOUND != null)
        splitLocation.getWorld().playSound(splitLocation, PICKUP_SOUND, .1F, ThreadLocalRandom.current().nextFloat() * .7f + .55f);

      if (newLevel%5 == 0 && LEVELUP_SOUND != null) {
        final float f = (newLevel > 30) ? 1.0F : (newLevel / 30.0F);

        split.playSound(splitLocation, LEVELUP_SOUND, f * .75f, 1);
      }
    }
  }


  /**
   * Used to avoid an infinite loop when we simulate a pickup
   */
  private static class PlayerPickupOrbEventWrapper extends PlayerPickupOrbEvent {

    public PlayerPickupOrbEventWrapper(PlayerPickupOrbEvent wrapping, Player player) {
      super(wrapping, player);
    }
  }
}
