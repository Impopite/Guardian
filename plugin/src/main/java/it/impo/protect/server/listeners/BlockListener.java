package it.impo.protect.server.listeners;

import it.impo.protect.Protect;
import it.impo.protect.api.data.Action.Action;
import it.impo.protect.api.data.BasicLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final Protect plugin;

    public BlockListener(Protect plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || event.getBlock().getType().isAir()) return;
        Location loc = event.getBlock().getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        plugin.getProtectManager().saveBlockLog(event.getPlayer(), event.getBlock(), Action.PLACE, location);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getBlock().getType().isAir()) return;
        Location loc = event.getBlock().getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        plugin.getProtectManager().saveBlockLog(event.getPlayer(), event.getBlock(), Action.BREAK, location);
    }
}
