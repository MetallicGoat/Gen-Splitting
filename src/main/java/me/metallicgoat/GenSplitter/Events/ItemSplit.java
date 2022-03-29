package me.metallicgoat.gensplitter.Events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.Util.Config.ConfigValue;
import me.metallicgoat.gensplitter.Util.XSeries.XSound;
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
        final GenSplitterPlugin plugin = GenSplitterPlugin.getInstance();
        final Player p = e.getPlayer();
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        final boolean enabled = plugin.getConfig().getBoolean("Gen-Splitter");
        final double sr = plugin.getConfig().getDouble("Split-Radius");
        if (arena != null) {
            //Split's if item has NOT already been thrown
            if (!e.isCancelled() &&
                    ConfigValue.splitSpawners.contains(e.getItem().getItemStack().getType()) &&
                    !e.getItem().hasMetadata("thrown") &&
                    enabled
            ) {

                //clone item
                ItemStack material = new ItemStack(e.getItem().getItemStack().getType());
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
                        if(loc.distanceSquared(split.getLocation()) < sr*sr){

                            split.getInventory().addItem(material);
                            XSound.matchXSound(Sound.ENTITY_ITEM_PICKUP).play(split.getLocation());
                        }
                    }
                }
            }
        }
    }
}