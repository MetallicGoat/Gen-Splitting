package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.GameAPI;
import de.marcely.bedwars.api.arena.Arena;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent;
import de.marcely.bedwars.api.event.player.PlayerDeathInventoryDropEvent.Handler;
import de.marcely.bedwars.api.game.spawner.DropType;
import de.marcely.bedwars.api.message.Message;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import me.metallicgoat.gensplitter.GenSplitterPlugin;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.apache.commons.lang.mutable.MutableInt;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class AutoCollect implements Listener {

  @EventHandler
  public void deathItemDrop(PlayerDeathInventoryDropEvent event) {
    if (!ConfigValue.autoCollectEnabled)
      return;

    // avoid wasting time if all are being removed regardless
    final List<Handler> handlers = event.getHandlerQueue();

    if (!handlers.isEmpty() && handlers.get(0) == Handler.DEFAULT_REMOVE_ALL)
      return;

    // first handler or after items have been filtered (KEEP_SPAWNERS)
    int index = 0;

    {
      final int filterIndex = handlers.indexOf(Handler.DEFAULT_KEEP_SPAWNERS);

      if (filterIndex != -1)
        index = filterIndex+1;
    }

    handlers.add(index, itemDrop());
  }

  public PlayerDeathInventoryDropEvent.Handler itemDrop() {
    return new Handler() {
      public Plugin getPlugin() {
        return GenSplitterPlugin.getInstance();
      }

      @Override
      public void execute(Player victim, Arena arena, Player killer, List<ItemStack> items, AtomicInteger atomicInteger) {
        if (killer == null)
          return;

        final double percentageKept = 0.01 * ConfigValue.autoCollectPercentKept;
        final Map<String, MutableInt> givenItems = new HashMap<>();

        for (ItemStack is : items) {
          if (percentageKept != 1)
            is.setAmount((int) Math.ceil((double) is.getAmount() * percentageKept));

          if (!ConfigValue.autoCollectMessageMaterials.contains(is.getType()))
            return;

          // sum up same item types
          givenItems.computeIfAbsent(getItemName(killer, is), g0 -> new MutableInt())
              .add(is.getAmount());
        }

        // send messages
        for (Map.Entry<String, MutableInt> e : givenItems.entrySet()) {
          final String message = Message.build(ConfigValue.autoCollectMessage)
              .placeholder("amount", e.getValue().getValue())
              .placeholder("item", e.getKey())
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