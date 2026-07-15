package it.impo.protect.manager;

import it.impo.protect.Protect;
import it.impo.protect.api.data.Action.Action;
import it.impo.protect.api.data.Action.ContainerAction;
import it.impo.protect.api.data.Action.Interaction;
import it.impo.protect.api.data.Action.ItemAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.ContainerType;
import it.impo.protect.api.data.logs.Logs;
import it.impo.protect.api.data.logs.impl.BlockLog;
import it.impo.protect.api.data.logs.impl.ContainerLog;
import it.impo.protect.api.data.logs.impl.InteractLog;
import it.impo.protect.api.data.logs.impl.ItemLog;
import it.impo.protect.api.database.ProtectTable;
import it.impo.protect.api.manager.ProtectManager;
import it.impo.protect.api.utils.ItemSerializer;
import it.impo.protect.api.utils.LogUtils;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.ConfigKey;
import it.impo.protect.config.constant.LangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BaseProtectManager extends ProtectManager {

    private final Protect plugin;
    private final Set<UUID> inspectors;
    private final LangLoader lang;

    private static final int PAGE_SIZE = ConfigKey.PAGE_SIZE.ordinal();
    private static final int PAGE_NUMBER = ConfigKey.PAGE_NUMBER.ordinal();

    public BaseProtectManager(Protect plugin) {
        this.plugin = plugin;
        inspectors = ConcurrentHashMap.newKeySet();
        this.lang = plugin.getLangLoader();
    }

    @Override
    public void saveLogs(Logs logs) {
        if(logs == null) return;
        ProtectTable table = plugin.getProtectTable();

        switch(logs){
            case BlockLog blockLog -> table.getBlockLogTable().addLog(blockLog);
            case ContainerLog containerLog -> table.getContainerLogTable().addLog(containerLog);
            case ItemLog itemLog -> table.getItemLogTable().addLog(itemLog);
            case InteractLog interactLog -> table.getInteractLogTable().addLog(interactLog);
            default -> {}
        }
    }

    @Override
    public void saveBlockLog(Player player, Block block, Action action, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.PROTECT_STAFF.getPermission());
        BlockLog blockLog = new BlockLog(player.getUniqueId(), player.getName(), location, LocalDateTime.now(), staff, block.getType().toString(), block.getBlockData().toString() ,action);

        saveLogs(blockLog);
    }

    @Override
    public void saveContainerLog(Player player, Block block, ItemStack itemStack, int amount, ContainerAction action, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.PROTECT_STAFF.getPermission());
        ContainerLog containerLog = new ContainerLog(player.getUniqueId(), player.getName(), ItemSerializer.safeItemToBytes(itemStack, plugin), LocalDateTime.now(), amount, staff, action, location, getContainerType(block));

        saveLogs(containerLog);
    }

    @Override
    public void saveItemLog(Player player, ItemStack itemStack, int amount, ItemAction action, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.PROTECT_STAFF.getPermission());
        ItemLog itemLog = new ItemLog(player.getUniqueId(), player.getName(), LocalDateTime.now(), staff, ItemSerializer.safeItemToBytes(itemStack, plugin), amount, action, location);

        saveLogs(itemLog);
    }

    @Override
    public void saveInteractLog(Player player, Block block, Interaction interaction, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.PROTECT_STAFF.getPermission());
        InteractLog interactLog = new InteractLog(player.getUniqueId(), player.getName(), location, LocalDateTime.now(), staff, block.getType().toString(), interaction);

        saveLogs(interactLog);
    }

    @Override
    public boolean toggleInspect(Player player) {
        UUID uuid = player.getUniqueId();

        if(inspectors.remove(uuid)) return false;
        inspectors.add(uuid);

        return true;
    }

    @Override
    public void showBlockLogs(Player player, BasicLocation location) {
        showBlockLogs(player, location, 1);
    }

    @Override
    public void showContainerLogs(Player player, BasicLocation location) {
        showContainerLogs(player, location, 1);
    }

    @Override
    public void showInteractLogs(Player player, BasicLocation location) {
        showInteractLogs(player, location, 1);
    }

    @Override
    public boolean isInspecting(Player player) {
        if(player == null) return false;
        return inspectors.contains(player.getUniqueId());
    }

    private void showBlockLogs(Player player, BasicLocation location, int page) {
        ProtectTable table = plugin.getProtectTable();

        table.getBlockLogTable().countLog(location).thenCompose(total -> {
            if (total == 0) {
                lang.send(player, LangKey.NO_INTERACTION);
                return CompletableFuture.completedFuture(null);
            }

            int maxPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            int clampedPage = Math.min(PAGE_NUMBER, maxPage);
            int offset = (clampedPage - 1) * PAGE_SIZE;

            return table.getBlockLogTable().inspectLog(location, PAGE_SIZE, offset)
                    .thenAccept(results -> sendInspect(player,
                            results.stream()
                                    .map(log -> LogUtils.formatLogs(log, plugin))
                                    .collect(Collectors.toList()),
                            clampedPage,
                            maxPage,
                            clicked -> showBlockLogs(clicked, location, clampedPage - 1),
                            clicked -> showBlockLogs(clicked, location, clampedPage + 1)));
        });
    }

    private void showContainerLogs(Player player, BasicLocation location, int page) {
        ProtectTable table = plugin.getProtectTable();

        table.getContainerLogTable().countLog(location).thenCompose(total -> {
            if (total == 0) {
                lang.send(player, LangKey.NO_INTERACTION);
                return CompletableFuture.completedFuture(null);
            }

            int maxPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            int clampedPage = Math.min(PAGE_NUMBER, maxPage);
            int offset = (clampedPage - 1) * PAGE_SIZE;

            return table.getContainerLogTable().inspectLog(location, PAGE_SIZE, offset)
                    .thenAccept(results -> sendInspect(player,
                            results.stream()
                                    .map(log -> LogUtils.formatLogs(log, plugin))
                                    .collect(Collectors.toList()),
                            clampedPage,
                            maxPage,
                            clicked -> showContainerLogs(clicked, location, clampedPage - 1),
                            clicked -> showContainerLogs(clicked, location, clampedPage + 1)));
        });
    }

    private void showInteractLogs(Player player, BasicLocation location, int page) {
        ProtectTable table = plugin.getProtectTable();

        table.getInteractLogTable().countLog(location).thenCompose(total -> {
            if (total == 0) {
                lang.send(player, LangKey.NO_INTERACTION);
                return CompletableFuture.completedFuture(null);
            }

            int maxPage = Math.max(1, (int) Math.ceil((double) total / PAGE_SIZE));
            int clampedPage = Math.min(PAGE_NUMBER, maxPage);
            int offset = (clampedPage - 1) * PAGE_SIZE;

            return table.getInteractLogTable().inspectLog(location, PAGE_SIZE, offset)
                    .thenAccept(results -> sendInspect(player,
                            results.stream()
                                    .map(log -> LogUtils.formatLogs(log, plugin))
                                    .collect(Collectors.toList()),
                            clampedPage,
                            maxPage,
                            clicked -> showInteractLogs(clicked, location, clampedPage - 1),
                            clicked -> showInteractLogs(clicked, location, clampedPage + 1)));
        });
    }

    private void sendInspect(Player player, List<Component> lines, int page, int maxPage, Consumer<Player> onPrevious, Consumer<Player> onNext) {
        lines.forEach(player::sendMessage);

        Component prev = Component.text("« Previous")
                .color(page > 1 ? NamedTextColor.GREEN : NamedTextColor.GRAY)
                .clickEvent(page > 1 ? ClickEvent.callback(audience -> onPrevious.accept(player)) : null);

        Component next = Component.text("Next »")
                .color(page < maxPage ? NamedTextColor.GREEN : NamedTextColor.GRAY)
                .clickEvent(page < maxPage ? ClickEvent.callback(audience -> onNext.accept(player)) : null);

        Component footer = Component.text("Page " + page + "/" + maxPage + "  ")
                .color(NamedTextColor.GRAY)
                .append(prev)
                .append(Component.text("  "))
                .append(next);

        player.sendMessage(footer);
    }

    private ContainerType getContainerType(Block block) {
        return switch (block.getType()) {
            case CHEST -> ContainerType.CHEST;
            case TRAPPED_CHEST -> ContainerType.TRAPPED_CHEST;
            case BARREL -> ContainerType.BARREL;
            case HOPPER -> ContainerType.HOPPER;
            case DISPENSER -> ContainerType.DISPENSER;
            case DROPPER -> ContainerType.DROPPER;
            default -> ContainerType.UNKNOWN;
        };
    }
}
