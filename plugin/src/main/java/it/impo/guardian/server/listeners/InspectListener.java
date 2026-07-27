package it.impo.guardian.server.listeners;

import it.impo.guardian.Guardian;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.utils.BlockUtils;
import it.impo.guardian.config.constant.LangKey;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.*;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;

public class InspectListener implements Listener {

    private final Guardian plugin;

    public InspectListener(Guardian plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getHand() != EquipmentSlot.HAND) return;

        Player player = event.getPlayer();
        if (!plugin.getGuardianManager().isInspecting(player)) return;

        Action action = event.getAction();
        if (action != Action.LEFT_CLICK_BLOCK && action != Action.RIGHT_CLICK_BLOCK) {
            plugin.getLangLoader().send(player, LangKey.NOT_VALID);
            return;
        }
        event.setCancelled(true);

        Block block = event.getClickedBlock();
        Location loc = block.getLocation();
        BasicLocation location = BasicLocation.from(loc);

        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);

        if (action == Action.LEFT_CLICK_BLOCK) {
            plugin.getGuardianManager().showBlockLogs(player, location);
            return;
        }

        BlockState state = block.getState();
        if (!(state instanceof Container)) {
            if (block.getBlockData() instanceof Openable) {
                loc = BlockUtils.resolveOpenableBlock(block).getLocation();
                location = BasicLocation.from(loc);
                plugin.getGuardianManager().showInteractLogs(player, location);
                return;
            }

            plugin.getGuardianManager().showBlockLogs(player, location);
            return;
        }

        if (state instanceof Chest chest) {
            Inventory holder = chest.getInventory();
            if (holder.getHolder() instanceof DoubleChest doubleChest) {
                Block left = doubleChest.getLeftSide() instanceof Container c ? c.getBlock() : block;
                loc = left.getLocation();
                location = BasicLocation.from(loc);
            }
        }

        plugin.getGuardianManager().showContainerLogs(player, location);
    }
}
