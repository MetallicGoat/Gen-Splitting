package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent.Handler;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.util.config.ConfigValue;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class AutoCollect implements Listener {
    @EventHandler
    public void deathItemDrop(PlayerDeathInventoryDropEvent event) {
        if (ConfigValue.autoCollectEnabled) {
            event.getHandlerQueue().clear();
            final Player killer = event.getPlayer().getKiller();

            if (killer != null && killer.getGameMode() != GameMode.SPECTATOR) {
                event.addHandlerToTop(Handler.DEFAULT_AUTO_PICKUP);
                event.addHandlerToTop(Handler.DEFAULT_KEEP_SPAWNERS);
                event.addHandlerToTop(itemDrop());
            }

            if (killer == null) {
                event.addHandlerToTop(Handler.DEFAULT_KEEP_SPAWNERS);
            }
        }
    }

    public PlayerDeathInventoryDropEvent.Handler itemDrop() {
        return new Handler() {
            public Plugin getPlugin() {
                return GenSplitterPlugin.getInstance();
            }

            @Override
            public void execute(Player player, Arena arena, Player player1, List<ItemStack> list, AtomicInteger atomicInteger) {

                final double percentageKept = 0.01 * ConfigValue.autoCollectPercentKept;

                list.forEach(itemStack -> {
                    final int amountToGive = (int) Math.ceil(itemStack.getAmount() * percentageKept);
                    itemStack.setAmount(amountToGive);
                });

                for(Material item : ConfigValue.autoCollectMessageMaterials){
                    final String name = getName(list, item);

                    if(name != null && player1 != null) {
                        final String message = Message.build(ConfigValue.autoCollectMessage)
                                .placeholder("amount", getAmount(list, item))
                                .placeholder("item", name)
                                .done();

                        player1.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
                    }
                }
            }

            private int getAmount(List<ItemStack> list, Material item){
                int count = 0;
                for (ItemStack itemStack : list) {
                    if (itemStack != null && item == itemStack.getType()) {
                        count += itemStack.getAmount();
                    }
                }
                return count;
            }

            private String getName(List<ItemStack> list, Material item){
                for (ItemStack itemStack : list) {
                    if (itemStack != null && item == itemStack.getType()) {
                        final DropType drop = BedwarsAPI.getGameAPI().getDropTypeByDrop(itemStack);

                        if(drop != null)
                            return drop.getName();
                    }
                }
                return null;
            }
        };
    }
}