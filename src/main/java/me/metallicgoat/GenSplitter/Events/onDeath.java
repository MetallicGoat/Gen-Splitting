package me.metallicgoat.GenSplitter.Events;

import de.marcely.bedwars.api.Arena;
import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.DropType;
import de.marcely.bedwars.api.event.PlayerDeathInventoryDropEvent;
import de.marcely.bedwars.libraries.org.jetbrains.annotations.Nullable;
import me.metallicgoat.GenSplitter.Main;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class onDeath implements Listener {

    @EventHandler
    public void onItemDrop(PlayerDeathInventoryDropEvent e){
        Main plugin = Main.getInstance();
        e.getHandlerQueue().clear();
        Player killer = e.getPlayer().getKiller();
        if(plugin.getConfig().getBoolean("Auto-Collect.Enabled")) {
            if(killer != null && killer.getGameMode() != GameMode.SPECTATOR) {
                e.addHandlerToTop(PlayerDeathInventoryDropEvent.Handler.DEFAULT_AUTO_PICKUP);
                e.addHandlerToTop(PlayerDeathInventoryDropEvent.Handler.DEFAULT_KEEP_SPAWNERS);
                e.addHandlerToTop(itemDrop());
            }else{
                System.out.println("test");
                e.addHandlerToBottom(PlayerDeathInventoryDropEvent.Handler.DEFAULT_KEEP_SPAWNERS);
            }
        }
    }

    public PlayerDeathInventoryDropEvent.Handler itemDrop() {
        return new PlayerDeathInventoryDropEvent.Handler() {
            @Override
            public Plugin getPlugin() {
                return Main.getInstance();
            }
            @Override
            public void execute(Player player, Arena arena, @Nullable Player player1, List<ItemStack> list, AtomicInteger atomicInteger) {
                Main plugin = Main.getInstance();
                for(String itemName : plugin.getDropMaterials()){
                    String name = getName(list, itemName);
                    if(name != null) {
                        String message = plugin.getConfig().getString("Auto-Collect.Message");
                        String messageFormatted = message.replace("%color%", getColor(list, itemName))
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
                        DropType drop = BedwarsAPI.getDropType(item);
                        return drop.getName();
                    }
                }
                return null;
            }
            private String getColor(List<ItemStack> list, String itemName){
                for (ItemStack item : list) {
                    if (item != null && itemName.contains(item.getType().name())) {
                        DropType drop = BedwarsAPI.getDropType(item);
                        return ("&" + drop.getChatColor().getChar());
                    }
                }
                return "";
            }
        };
    }
}