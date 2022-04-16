package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.util.config.ConfigValue;
import me.metallicgoat.gensplitter.util.XSeries.XSound;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ItemSplit implements Listener {

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);

        if (arena != null) {
            //Split's if item has NOT already been thrown
            if (ConfigValue.splitterEnabled && !e.isCancelled() &&
                    ConfigValue.splitSpawners.contains(e.getItem().getItemStack().getType()) &&
                    !e.getItem().hasMetadata("thrown")
            ) {

                //clone item
                final ItemStack material = new ItemStack(e.getItem().getItemStack().getType());
                material.setAmount(e.getItem().getItemStack().getAmount());
                final ItemMeta im = e.getItem().getItemStack().getItemMeta();

                if(im == null)
                    return;

                im.setLore(null);
                material.setItemMeta(im);

                // Give Item
                final Location loc = p.getLocation();
                //For all players
                for(Player split:arena.getPlayers()){
                    //If player to split with is not player who collected item

                    if(split != p && split.getGameMode() != GameMode.SPECTATOR){
                        //If player is in range
                        if(loc.distance(split.getLocation()) <= ConfigValue.splitRadius){
                            split.getInventory().addItem(material);
                            XSound.matchXSound(Sound.ENTITY_ITEM_PICKUP).play(split.getLocation());
                        }
                    }
                }
            }
        }
    }
}