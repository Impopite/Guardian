package it.impo.guardian.server.listeners;

import it.impo.guardian.Guardian;
import it.impo.guardian.api.data.action.ItemAction;
import it.impo.guardian.api.data.BasicLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ItemListener implements Listener {

    private final Guardian plugin;

    public ItemListener(Guardian plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onItemPickup(EntityPickupItemEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player player)) return;
        ItemStack itemStack = event.getItem().getItemStack();
        if (itemStack.getType().isAir()) return;

        Location loc = event.getItem().getLocation();
        BasicLocation location = BasicLocation.from(loc);

        plugin.getGuardianManager().saveItemLog(player, itemStack, itemStack.getAmount(), ItemAction.PICKUP, location);
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        if (event.isCancelled()) return;
        ItemStack itemStack = event.getItemDrop().getItemStack();
        if (itemStack.getType().isAir()) return;

        Location loc = event.getItemDrop().getLocation();
        BasicLocation location = BasicLocation.from(loc);

        plugin.getGuardianManager().saveItemLog(event.getPlayer(), itemStack, itemStack.getAmount(), ItemAction.DROP, location);
    }
}
