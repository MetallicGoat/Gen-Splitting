package me.metallicgoat.gensplitter.events;

import de.marcely.bedwars.api.BedwarsAPI;
import de.marcely.bedwars.api.arena.Arena;
import me.metallicgoat.gensplitter.config.ConfigValue;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;

public class VoidDrops implements Listener {

    private static final double FALLING_VELOCITY = -0.1D;
    private static final int VOID_CHECK_DEPTH = 5;

    @EventHandler
    public void onVoidDrop(PlayerDropItemEvent event) {
        if (!ConfigValue.antiVoidDrops)
            return;

        if (isFallingIntoVoid(event.getPlayer()))
            event.setCancelled(true);
    }

    @EventHandler
    public void onInventoryInteract(InventoryClickEvent event) {
        if (!ConfigValue.antiVoidDrops)
            return;

        final Player clicker = (Player) event.getWhoClicked();

        if (isFallingIntoVoid(clicker)) {
            clicker.closeInventory();
            event.setCancelled(true);
        }
    }

    public boolean isFallingIntoVoid(Player player) {
        final Arena arena = BedwarsAPI.getGameAPI().getArenaByPlayer(player);

        if (arena == null)
            return false;

        return isFalling(player) && isVoidBelow(player);
    }

    private boolean isFalling(Player player) {
        if (player.isFlying() || ((Entity) player).isOnGround())
            return false;

        return player.getVelocity().getY() < FALLING_VELOCITY || player.getFallDistance() > 0F;
    }

    private boolean isVoidBelow(Player player) {
        final Location currLoc = player.getLocation().clone().subtract(0, 0.1D, 0);

        for (int pos = 0; pos < VOID_CHECK_DEPTH; pos++) {
            if (currLoc.getBlock().getType() != Material.AIR)
                return false;

            currLoc.subtract(0, 1, 0);
        }

        return true;
    }
}