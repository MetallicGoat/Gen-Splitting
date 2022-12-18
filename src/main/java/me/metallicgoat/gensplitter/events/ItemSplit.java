package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.tools.Helper;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemSplit implements Listener {

    @EventHandler
    public void onPickup(PlayerPickupItemEvent event) {
        final Player player = event.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);
        final ItemStack pickedUpStack = event.getItem().getItemStack();

        if (arena == null)
            return;

        // Split's if item has NOT already been thrown
        if (ConfigValue.splitterEnabled && !event.isCancelled() &&
                ConfigValue.splitSpawners.contains(pickedUpStack.getType()) &&
                !event.getItem().hasMetadata("thrown")) {

            // clone item
            final ItemStack clonedStack = pickedUpStack.clone();

            // Give Item
            final Location collectLocation = player.getLocation();
            final Sound sound = Helper.get().getSoundByName("ENTITY_ITEM_PICKUP");

            // For all players
            for (Player split : arena.getPlayers()) {
                // If player to split with is not player who collected item
                final Location splitLocation = split.getLocation();

                if (split != player && split.getGameMode() != GameMode.SPECTATOR && splitLocation.getWorld() == collectLocation.getWorld()) {
                    // If player is in range
                    if (collectLocation.distance(splitLocation) <= ConfigValue.splitRadius) {
                        split.getInventory().addItem(clonedStack);

                        if (sound != null)
                            collectLocation.getWorld().playSound(collectLocation, sound, 1, 1);
                    }
                }
            }
        }
    }
}