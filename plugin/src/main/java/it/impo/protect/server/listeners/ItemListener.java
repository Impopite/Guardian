package it.impo.protect.server.listeners;

import it.impo.protect.Protect;
import it.impo.protect.api.data.Action.ItemAction;
import it.impo.protect.api.data.BasicLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    private final Protect plugin;

    public ItemListener(Protect plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack itemStack =  event.getItem().getItemStack();
        if (itemStack.getType().isAir()) return;

        Location loc = event.getItem().getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        plugin.getProtectManager().saveItemLog(player, itemStack, itemStack.getAmount(), ItemAction.PICKUP, location);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemStack =  event.getItemDrop().getItemStack();
        if (itemStack.getType().isAir()) return;

        Location loc = event.getItemDrop().getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        plugin.getProtectManager().saveItemLog(event.getPlayer(), itemStack, itemStack.getAmount(), ItemAction.DROP, location);
    }


}
