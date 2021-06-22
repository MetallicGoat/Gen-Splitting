package me.metallicgoat.GenSplitter.Events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent.Handler;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.marcely.bedwars.api.game.spawner.DropType;
import me.metallicgoat.GenSplitter.Main;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class OnDeath implements Listener {
    @EventHandler
    public void onItemDrop(PlayerDeathInventoryDropEvent e) {
        Main plugin = Main.getInstance();
        e.getHandlerQueue().clear();
        Player killer = e.getPlayer().getKiller();
        if (plugin.getConfig().getBoolean("Auto-Collect.Enabled")) {
            if (killer != null && killer.getGameMode() != GameMode.SPECTATOR) {
                e.addHandlerToTop(Handler.DEFAULT_AUTO_PICKUP);
                e.addHandlerToTop(Handler.DEFAULT_KEEP_SPAWNERS);
                e.addHandlerToTop(itemDrop());
            }

            if (killer == null) {
                e.addHandlerToTop(Handler.DEFAULT_KEEP_SPAWNERS);
            }
        }
    }

    public PlayerDeathInventoryDropEvent.Handler itemDrop() {
        return new Handler() {
            public Plugin getPlugin() {
                return Main.getInstance();
            }

            @Override
            public void execute(Player player, Arena arena, Player player1, List<ItemStack> list, AtomicInteger atomicInteger) {
                Main plugin = Main.getInstance();
                for(String itemName : plugin.getDropMaterials()){
                    String name = getName(list, itemName);
                    if(name != null) {
                        String message = plugin.getConfig().getString("Auto-Collect.Message");
                        String messageFormatted = message
                                .replace("%amount%", Integer.toString(getAmount(list, itemName)))
                                .replace("%item%", name);
                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', messageFormatted));
                    }
                }
            }

            private int getAmount(List<ItemStack> list, String itemName){
                int count = 0;
                for (ItemStack item : list) {
                    if (item != null && itemName.contains(item.getType().name())) {
                        count = count + item.getAmount();
                    }
                }
                return count;
            }
            private String getName(List<ItemStack> list, String itemName){
                for (ItemStack item : list) {
                    if (item != null && itemName.contains(item.getType().name())) {
                        DropType drop = BedwarsAPI.getGameAPI().getDropTypeByDrop(item);
                        return drop.getName();
                    }
                }
                return null;
            }
        };
    }
}