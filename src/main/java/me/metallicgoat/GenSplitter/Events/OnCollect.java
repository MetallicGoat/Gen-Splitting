package me.metallicgoat.GenSplitter.Events;

import java.util.List;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.GenSplitter.Main;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class OnCollect implements Listener {

    @EventHandler
    public void onPickup(PlayerPickupItemEvent e) {
        Main plugin = Main.getInstance();
        Player p = e.getPlayer();
        Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(p);
        boolean enabled = plugin.getConfig().getBoolean("Gen-Splitter");
        double sr = plugin.getConfig().getDouble("Split-Radius");
        if (arena != null) {
            if (!e.isCancelled() &&
                    plugin.getSplitMaterials().contains(e.getItem().getItemStack().getType().name()) &&
                    !e.getItem().hasMetadata("thrown") &&
                    enabled
            ) {
                Location loc = p.getLocation();
                List<Entity> nearbyEntities = (List<Entity>) loc.getWorld().getNearbyEntities(loc, sr, sr, sr);
                for (Entity entity : loc.getWorld().getEntities()) {
                    if (entity instanceof Player &&
                            nearbyEntities.contains(entity)) {
                        Player split = (Player) entity;
                        if (split.getGameMode() != GameMode.SPECTATOR){
                            if (split.getUniqueId() != p.getUniqueId()) {
                                ItemStack material = new ItemStack(e.getItem().getItemStack().getType());
                                material.setAmount(e.getItem().getItemStack().getAmount());

                                ItemMeta im = e.getItem().getItemStack().getItemMeta();
                                im.setLore(null);
                                material.setItemMeta(im);

                                split.getInventory().addItem(material);
                                if (Bukkit.getServer().getClass().getPackage().getName().contains("v1_8")) {
                                    split.playSound(split.getLocation(), Sound.valueOf("ITEM_PICKUP"), 0.8F, 1.0F);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}