package it.impo.protect.server.listeners;

import it.impo.protect.Protect;
import it.impo.protect.api.data.Action.ContainerAction;
import it.impo.protect.api.data.Action.Interaction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.LoggedInteraction;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Container;
import org.bukkit.block.DoubleChest;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

public class ContainerListener implements Listener {

    private final Protect plugin;

    public ContainerListener(Protect plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory top = event.getView().getTopInventory();
        InventoryHolder holder = top.getHolder();
        if (!(holder instanceof Container) && !(holder instanceof DoubleChest)) return;

        LoggedInteraction interaction = resolveInteraction(event, player, top);
        if (interaction == null) return;

        Block containerBlock = resolveContainerBlock(holder, player);
        if (containerBlock == null) return;

        Location loc = containerBlock.getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());

        plugin.getProtectManager().saveContainerLog(player, containerBlock, interaction.item(), interaction.amount(), interaction.action(), location);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.isCancelled()) return;
        if (!(event.getWhoClicked() instanceof Player player)) return;

        Inventory top = event.getView().getTopInventory();
        InventoryHolder holder = top.getHolder();
        if (!(holder instanceof Container) && !(holder instanceof DoubleChest)) return;

        boolean touchesTop = event.getRawSlots().stream().anyMatch(slot -> slot < top.getSize());
        if (!touchesTop) return;

        ItemStack oldCursor = event.getOldCursor();
        if (oldCursor.getType().isAir()) return;

        int amount = event.getNewItems().entrySet().stream()
                .filter(entry -> entry.getKey() < top.getSize())
                .mapToInt(entry -> entry.getValue().getAmount())
                .sum();
        if (amount <= 0) return;

        Block block = holder instanceof DoubleChest doubleChest
                ? resolveClickedBlock(doubleChest, player)
                : ((Container) holder).getBlock();

        Location loc = block.getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());


        plugin.getProtectManager().saveContainerLog(player, block, oldCursor, amount, ContainerAction.ADD, location);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (event.getClickedBlock() == null) return;

        Block block = resolveOpenableBlock(event.getClickedBlock());
        if (!(block.getBlockData() instanceof Openable openable)) return;

        Location loc = block.getLocation();
        BasicLocation location = new BasicLocation(loc.getWorld().getName(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());


        Player player = event.getPlayer();
        boolean wasOpen = openable.isOpen();
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if (!(block.getBlockData() instanceof Openable updated)) return;
            if (updated.isOpen() == wasOpen) return;

            Interaction action = updated.isOpen() ? Interaction.OPEN : Interaction.CLOSE;
            plugin.getProtectManager().saveInteractLog(player, block, action, location);
        }, 1L);
    }

    private Block resolveOpenableBlock(Block block) {
        if (!(block.getBlockData() instanceof Bisected bisected)) return block;
        return bisected.getHalf() == Bisected.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
    }

    private LoggedInteraction resolveInteraction(InventoryClickEvent event, Player player, Inventory top) {
        boolean clickedInTop = event.getRawSlot() < top.getSize();
        boolean clickedInBottom = event.getRawSlot() >= top.getSize();
        boolean isShift = event.getClick() == ClickType.SHIFT_LEFT || event.getClick() == ClickType.SHIFT_RIGHT;
        boolean isRight = event.getClick() == ClickType.RIGHT;

        if (event.getClick() == ClickType.NUMBER_KEY && clickedInTop) {
            ItemStack hotbarItem = player.getInventory().getItem(event.getHotbarButton());
            if (isUsable(hotbarItem)) return new LoggedInteraction(ContainerAction.ADD, hotbarItem, hotbarItem.getAmount());

            ItemStack currentItem = event.getCurrentItem();
            if (!isUsable(currentItem)) return null;
            return new LoggedInteraction(ContainerAction.REMOVE, currentItem, currentItem.getAmount());
        }

        if (isShift && clickedInBottom) {
            ItemStack currentItem = event.getCurrentItem();
            if (!isUsable(currentItem)) return null;
            return new LoggedInteraction(ContainerAction.ADD, currentItem, currentItem.getAmount());
        }

        if (!clickedInTop) return null;

        if (isShift) {
            ItemStack currentItem = event.getCurrentItem();
            if (!isUsable(currentItem)) return null;
            return new LoggedInteraction(ContainerAction.REMOVE, currentItem, currentItem.getAmount());
        }

        if (event.getAction() == InventoryAction.COLLECT_TO_CURSOR) {
            ItemStack currentItem = event.getCurrentItem();
            if (!isUsable(currentItem)) return null;
            return new LoggedInteraction(ContainerAction.REMOVE, currentItem, currentItem.getAmount());
        }

        ItemStack cursor = event.getCursor();
        if (isUsable(cursor)) return new LoggedInteraction(ContainerAction.ADD, cursor, isRight ? 1 : cursor.getAmount());

        ItemStack currentItem = event.getCurrentItem();
        if (!isUsable(currentItem)) return null;
        return new LoggedInteraction(ContainerAction.REMOVE, currentItem, isRight ? (int) Math.ceil(currentItem.getAmount() / 2.0) : currentItem.getAmount());
    }

    private boolean isUsable(ItemStack itemStack) {
        return itemStack != null && !itemStack.getType().isAir();
    }

    private Block resolveContainerBlock(InventoryHolder holder, Player player) {
        if (holder instanceof DoubleChest doubleChest) {
            return resolveClickedBlock(doubleChest, player);
        }
        if (holder instanceof Container container) {
            return container.getBlock();
        }
        return null;
    }

    private Block resolveClickedBlock(DoubleChest doubleChest, Player player) {
        Block left = doubleChest.getLeftSide()  instanceof Container c ? c.getBlock() : null;
        Block right = doubleChest.getRightSide() instanceof Container c ? c.getBlock() : null;

        if (left == null) return right;
        if (right == null) return left;

        double distLeft = left.getLocation().distanceSquared(player.getLocation());
        double distRight = right.getLocation().distanceSquared(player.getLocation());

        return distLeft <= distRight ? left : right;
    }
}
