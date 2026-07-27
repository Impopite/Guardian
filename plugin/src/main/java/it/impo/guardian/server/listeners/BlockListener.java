package it.impo.guardian.server.listeners;

import it.impo.guardian.Guardian;
import it.impo.guardian.api.data.action.Action;
import it.impo.guardian.api.data.BasicLocation;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

public class BlockListener implements Listener {

    private final Guardian plugin;

    public BlockListener(Guardian plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || event.getBlock().getType().isAir()) return;
        Location loc = event.getBlock().getLocation();
        BasicLocation location = BasicLocation.from(loc);

        plugin.getGuardianManager().saveBlockLog(event.getPlayer(), event.getBlock(), Action.PLACE, location);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || event.getBlock().getType().isAir()) return;
        Location loc = event.getBlock().getLocation();
        BasicLocation location = BasicLocation.from(loc);

        plugin.getGuardianManager().saveBlockLog(event.getPlayer(), event.getBlock(), Action.BREAK, location);
    }
}
