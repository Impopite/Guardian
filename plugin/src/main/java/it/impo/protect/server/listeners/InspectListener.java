package it.impo.protect.server.listeners;

import it.impo.protect.Protect;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.config.constant.LangKey;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class InspectListener implements Listener {

    private final Protect plugin;

    public InspectListener(Protect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!plugin.getProtectManager().isInspecting(player)) return;

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            plugin.getLangLoader().send(player, LangKey.NOT_VALID);
            return;
        }
        event.setCancelled(true);

        Block block = event.getClickedBlock();
        Location loc = block.getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        if (action == Action.LEFT_CLICK_BLOCK) {
            plugin.getProtectManager().showBlockLogs(player, location);
            return;
        }

        if (!(block.getState() instanceof Container)) {
            if (block.getBlockData() instanceof Openable) {
                loc = resolveOpenableBlock(block).getLocation();
                location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
                plugin.getProtectManager().showInteractLogs(player, location);
                return;
            }

            plugin.getProtectManager().showBlockLogs(player, location);
            return;
        }

        plugin.getProtectManager().showContainerLogs(player, location);
    }

    private Block resolveOpenableBlock(Block block) {
        if (!(block.getBlockData() instanceof Bisected bisected)) return block;
        return bisected.getHalf() == Bisected.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
    }
}
