package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent.Handler;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.message.Message;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
      public void execute(Player victim, Arena arena, Player killer, List<ItemStack> items, AtomicInteger atomicInteger) {
        final double percentageKept = 0.01 * ConfigValue.autoCollectPercentKept;

        for (ItemStack is : items) {
          if (percentageKept != 1)
            is.setAmount((int) Math.ceil((double) is.getAmount() * percentageKept));

          if (killer == null || !ConfigValue.autoCollectMessageMaterials.contains(is.getType()))
            return;

          getItemName(killer, is);

          final String message = Message.build(ConfigValue.autoCollectMessage)
              .placeholder("amount", is.getAmount())
              .placeholder("item", getItemName(killer, is))
              .done();

          killer.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
        }
      }

      private String getItemName(Player player, ItemStack itemStack) {
        final DropType dropType = GameAPI.get().getDropTypeByDrop(itemStack);

        if (dropType != null)
          return dropType.getName(player);

        return itemStack.getItemMeta().getDisplayName();
      }
    };
  }
}