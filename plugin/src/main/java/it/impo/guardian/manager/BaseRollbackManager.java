package it.impo.guardian.manager;

import it.impo.guardian.Guardian;
import it.impo.guardian.api.data.Action.Action;
import it.impo.guardian.api.data.Action.ContainerAction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.RollbackSummary;
import it.impo.guardian.api.data.logs.impl.BlockLog;
import it.impo.guardian.api.data.logs.impl.ContainerLog;
import it.impo.guardian.api.manager.RollbackManager;
import it.impo.guardian.api.utils.ItemSerializer;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class BaseRollbackManager extends RollbackManager {

    private final Guardian plugin;

    public BaseRollbackManager(Guardian plugin) {
        this.plugin = plugin;
    }

    @Override
    public void rollbackBlocks(Player player, int radius, Duration duration) {
        BasicLocation center = new BasicLocation(
                player.getLocation().getWorld().getName(),
                player.getLocation().getBlockX(),
                player.getLocation().getBlockY(),
                player.getLocation().getBlockZ()
        );
        LocalDateTime since = LocalDateTime.now().minus(duration);

        plugin.getGuardianTable().getBlockLogTable().rollbackLogs(center, radius, since).thenAccept(logs ->
                Bukkit.getScheduler().runTask(plugin, () -> {
                    List<BlockLog> filteredLogs = filterByRadius(center, radius, logs);
                    if (filteredLogs.isEmpty()) {
                        plugin.getLangLoader().send(player, LangKey.ROLLBACK_NO_BLOCKS);
                        return;
                    }

                    AtomicInteger applied = new AtomicInteger();
                    AtomicInteger skipped = new AtomicInteger();

                    for (BlockLog log : sortBlockRollbackLogs(filteredLogs)) {
                        World world = Bukkit.getWorld(log.getLocation().world());
                        if (world == null) {
                            skipped.incrementAndGet();
                            continue;
                        }

                        Block block = world.getBlockAt(log.getLocation().x(), log.getLocation().y(), log.getLocation().z());
                        if (!rollbackBlock(block, log)) {
                            skipped.incrementAndGet();
                            continue;
                        }

                        applied.incrementAndGet();
                    }

                    sendBlockSummary(player, new RollbackSummary(filteredLogs.size(), applied.get(), skipped.get(), 0));
                })
        );
    }

    @Override
    public void rollbackContainers(Player player, int radius, Duration duration) {
        BasicLocation center = new BasicLocation(
                player.getLocation().getWorld().getName(),
                player.getLocation().getBlockX(),
                player.getLocation().getBlockY(),
                player.getLocation().getBlockZ()
        );
        LocalDateTime since = LocalDateTime.now().minus(duration);

        plugin.getGuardianTable().getContainerLogTable().rollbackLogs(center, radius, since).thenAccept(logs ->
                Bukkit.getScheduler().runTask(plugin, () -> {
                    List<ContainerLog> filteredLogs = filterByRadius(center, radius, logs);
                    if (filteredLogs.isEmpty()) {
                        plugin.getLangLoader().send(player, LangKey.ROLLBACK_NO_CONTAINERS);
                        return;
                    }

                    AtomicInteger applied = new AtomicInteger();
                    AtomicInteger skipped = new AtomicInteger();
                    AtomicInteger partial = new AtomicInteger();

                    for (ContainerLog log : filteredLogs) {
                        Container container = resolveContainer(log.getLocation());
                        if (container == null) {
                            skipped.incrementAndGet();
                            continue;
                        }

                        ItemStack baseItem = ItemSerializer.safeItemFromBytes(log.getItem(), plugin);
                        if (baseItem == null || baseItem.getType().isAir()) {
                            skipped.incrementAndGet();
                            continue;
                        }

                        int affectedAmount;
                        if (log.getAction() == ContainerAction.ADD) {
                            affectedAmount = removeMatchingItems(container.getInventory(), baseItem, log.getAmount());
                        } else {
                            affectedAmount = addMatchingItems(container.getInventory(), baseItem, log.getAmount());
                        }

                        if (affectedAmount <= 0) {
                            skipped.incrementAndGet();
                            continue;
                        }

                        if (affectedAmount < log.getAmount()) partial.incrementAndGet();
                        applied.incrementAndGet();
                        container.update(true, false);
                    }

                    sendContainerSummary(player, new RollbackSummary(filteredLogs.size(), applied.get(), skipped.get(), partial.get()));
                })
        );
    }

    private void sendBlockSummary(Player player, RollbackSummary summary) {
        LangLoader lang = plugin.getLangLoader();
        if (summary.skipped() <= 0) {
            lang.send(player, LangKey.ROLLBACK_BLOCKS_SUCCESS,
                    Placeholder.parsed("applied", String.valueOf(summary.applied())),
                    Placeholder.parsed("total", String.valueOf(summary.total())));
        } else {
            lang.send(player, LangKey.ROLLBACK_BLOCKS_SUCCESS_SKIPPED,
                    Placeholder.parsed("applied", String.valueOf(summary.applied())),
                    Placeholder.parsed("skipped", String.valueOf(summary.skipped())));
        }
    }

    private void sendContainerSummary(Player player, RollbackSummary summary) {
        LangLoader lang = plugin.getLangLoader();
        if (summary.skipped() <= 0 && summary.partial() <= 0) {
            lang.send(player, LangKey.ROLLBACK_CONTAINERS_SUCCESS,
                    Placeholder.parsed("applied", String.valueOf(summary.applied())),
                    Placeholder.parsed("total", String.valueOf(summary.total())));
        } else if (summary.skipped() <= 0) {
            lang.send(player, LangKey.ROLLBACK_CONTAINERS_SUCCESS_PARTIAL,
                    Placeholder.parsed("applied", String.valueOf(summary.applied())),
                    Placeholder.parsed("partial", String.valueOf(summary.partial())));
        } else if (summary.partial() <= 0) {
            lang.send(player, LangKey.ROLLBACK_CONTAINERS_SUCCESS_SKIPPED,
                    Placeholder.parsed("applied", String.valueOf(summary.applied())),
                    Placeholder.parsed("skipped", String.valueOf(summary.skipped())));
        } else {
            lang.send(player, LangKey.ROLLBACK_CONTAINERS_SUCCESS_ALL,
                    Placeholder.parsed("applied", String.valueOf(summary.applied())),
                    Placeholder.parsed("skipped", String.valueOf(summary.skipped())),
                    Placeholder.parsed("partial", String.valueOf(summary.partial())));
        }
    }

    private <T> List<T> filterByRadius(BasicLocation center, int radius, List<T> logs) {
        double maxDistanceSquared = radius * radius;
        return logs.stream()
                .filter(log -> {
                    BasicLocation loc = getLocation(log);
                    if (loc == null || !loc.world().equals(center.world())) return false;
                    double dx = loc.x() - center.x();
                    double dy = loc.y() - center.y();
                    double dz = loc.z() - center.z();
                    return (dx * dx + dy * dy + dz * dz) <= maxDistanceSquared;
                })
                .toList();
    }

    private BasicLocation getLocation(Object log) {
        if (log instanceof BlockLog blockLog) return blockLog.getLocation();
        if (log instanceof ContainerLog containerLog) return containerLog.getLocation();
        return null;
    }

    private List<BlockLog> sortBlockRollbackLogs(List<BlockLog> logs) {
        return logs.stream()
                .sorted(this::compareBlockRollbackLogs)
                .toList();
    }

    private int compareBlockRollbackLogs(BlockLog first, BlockLog second) {
        int actionComparison = Integer.compare(actionPriority(first), actionPriority(second));
        if (actionComparison != 0) return actionComparison;

        if (first.getAction() == Action.PLACE && second.getAction() == Action.PLACE) {
            int supportComparison = Integer.compare(removePriority(first), removePriority(second));
            if (supportComparison != 0) return supportComparison;
            return Integer.compare(second.getLocation().y(), first.getLocation().y());
        }

        if (first.getAction() == Action.BREAK && second.getAction() == Action.BREAK) {
            int supportComparison = Integer.compare(restorePriority(first), restorePriority(second));
            if (supportComparison != 0) return supportComparison;
            return Integer.compare(first.getLocation().y(), second.getLocation().y());
        }

        return 0;
    }

    private int actionPriority(BlockLog log) {
        return log.getAction() == Action.PLACE ? 0 : 1;
    }

    private int restorePriority(BlockLog log) {
        Material material = targetMaterial(log);
        return material != null && material.isSolid() ? 0 : 1;
    }

    private int removePriority(BlockLog log) {
        Material material = targetMaterial(log);
        return material != null && material.isSolid() ? 1 : 0;
    }

    private Material targetMaterial(BlockLog log) {
        String blockData = log.getBlockData();
        if (blockData != null && !blockData.isBlank())
            return Bukkit.createBlockData(stripBlockData(blockData)).getMaterial();
        return Material.matchMaterial(log.getBlockType());
    }

    private boolean rollbackBlock(Block block, BlockLog log) {
        if (log.getAction() == Action.PLACE) {
            block.setType(Material.AIR, false);
            return true;
        }

        String blockData = log.getBlockData();
        if (blockData != null && !blockData.isBlank()) {
            BlockData data = Bukkit.createBlockData(stripBlockData(blockData));
            block.setBlockData(data, false);
            return true;
        }

        Material targetType = Material.matchMaterial(log.getBlockType());
        if (targetType == null) return false;

        block.setType(targetType, false);
        return true;
    }

    private String stripBlockData(String raw) {
        if (raw.startsWith("CraftBlockData{") && raw.endsWith("}")) {
            return raw.substring(15, raw.length() - 1);
        }
        return raw;
    }

    private Container resolveContainer(BasicLocation location) {
        World world = Bukkit.getWorld(location.world());
        if (world == null) return null;

        Block block = world.getBlockAt(location.x(), location.y(), location.z());
        return block.getState() instanceof Container container ? container : null;
    }

    private int addMatchingItems(Inventory inventory, ItemStack item, int amount) {
        int added = 0;
        int remaining = amount;
        int maxStackSize = Math.max(1, item.getMaxStackSize());

        while (remaining > 0) {
            int chunk = Math.min(remaining, maxStackSize);
            ItemStack clone = item.clone();
            clone.setAmount(chunk);

            Map<Integer, ItemStack> leftover = inventory.addItem(clone);
            int leftoverAmount = leftover.values().stream().mapToInt(ItemStack::getAmount).sum();
            int inserted = chunk - leftoverAmount;
            added += inserted;
            remaining -= inserted;

            if (leftoverAmount > 0) break;
        }

        return added;
    }

    private int removeMatchingItems(Inventory inventory, ItemStack item, int amount) {
        int removed = 0;
        int remaining = amount;

        for (int slot = 0; slot < inventory.getSize() && remaining > 0; slot++) {
            ItemStack current = inventory.getItem(slot);
            if (current == null || current.getType().isAir() || !current.isSimilar(item)) continue;

            int toRemove = Math.min(remaining, current.getAmount());
            int left = current.getAmount() - toRemove;

            if (left <= 0) {
                inventory.setItem(slot, null);
            } else {
                ItemStack clone = current.clone();
                clone.setAmount(left);
                inventory.setItem(slot, clone);
            }

            removed += toRemove;
            remaining -= toRemove;
        }

        return removed;
    }
}
