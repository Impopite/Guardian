package it.impo.guardian.manager;

import it.impo.guardian.Guardian;
import it.impo.guardian.api.data.action.Action;
import it.impo.guardian.api.data.action.ContainerAction;
import it.impo.guardian.api.data.action.Interaction;
import it.impo.guardian.api.data.action.ItemAction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.ContainerType;
import it.impo.guardian.api.data.logs.Logs;
import it.impo.guardian.api.data.logs.impl.BlockLog;
import it.impo.guardian.api.data.logs.impl.ContainerLog;
import it.impo.guardian.api.data.logs.impl.InteractLog;
import it.impo.guardian.api.data.logs.impl.ItemLog;
import it.impo.guardian.api.database.GuardianTable;
import it.impo.guardian.api.manager.GuardianManager;
import it.impo.guardian.api.utils.ItemSerializer;
import it.impo.guardian.api.utils.LogUtils;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.constant.ConfigKey;
import it.impo.guardian.config.constant.LangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class BaseGuardianManager extends GuardianManager {

    private final Guardian plugin;
    private final Set<UUID> inspectors;
    private final int pageSize;

    public BaseGuardianManager(Guardian plugin) {
        this.plugin = plugin;
        inspectors = ConcurrentHashMap.newKeySet();
        this.pageSize = plugin.getConfigLoader().get(ConfigKey.PAGE_SIZE, 10);
    }

    @Override
    public void saveLogs(Logs logs) {
        if (logs == null) return;
        GuardianTable table = plugin.getGuardianTable();

        CompletableFuture<Boolean> future = switch (logs) {
            case BlockLog blockLog -> table.getBlockLogTable().addLog(blockLog);
            case ContainerLog containerLog -> table.getContainerLogTable().addLog(containerLog);
            case ItemLog itemLog -> table.getItemLogTable().addLog(itemLog);
            case InteractLog interactLog -> table.getInteractLogTable().addLog(interactLog);
            default -> null;
        };

        if (future != null) {
            handleResult(future, "save " + logs.getClass().getSimpleName());
        }
    }

    @Override
    public void saveBlockLog(Player player, Block block, Action action, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.GUARDIAN_STAFF.getPermission());
        BlockLog blockLog = new BlockLog(player.getUniqueId(), player.getName(), location, LocalDateTime.now(), staff, block.getType().toString(), block.getBlockData().getAsString(), action);

        saveLogs(blockLog);
    }

    @Override
    public void saveContainerLog(Player player, Block block, ItemStack itemStack, int amount, ContainerAction action, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.GUARDIAN_STAFF.getPermission());
        ContainerLog containerLog = new ContainerLog(player.getUniqueId(), player.getName(), ItemSerializer.safeItemToBytes(itemStack, plugin), LocalDateTime.now(), amount, staff, action, location, getContainerType(block));

        saveLogs(containerLog);
    }

    @Override
    public void saveItemLog(Player player, ItemStack itemStack, int amount, ItemAction action, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.GUARDIAN_STAFF.getPermission());
        ItemLog itemLog = new ItemLog(player.getUniqueId(), player.getName(), LocalDateTime.now(), staff, ItemSerializer.safeItemToBytes(itemStack, plugin), amount, action, location);

        saveLogs(itemLog);
    }

    @Override
    public void saveInteractLog(Player player, Block block, Interaction interaction, BasicLocation location) {
        boolean staff = player.hasPermission(Permission.GUARDIAN_STAFF.getPermission());
        InteractLog interactLog = new InteractLog(player.getUniqueId(), player.getName(), location, LocalDateTime.now(), staff, block.getType().toString(), interaction);

        saveLogs(interactLog);
    }

    @Override
    public boolean toggleInspect(Player player) {
        UUID uuid = player.getUniqueId();

        if (inspectors.remove(uuid)) return false;
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
        if (player == null) return false;
        return inspectors.contains(player.getUniqueId());
    }

    private void showBlockLogs(Player player, BasicLocation location, int page) {
        showLogs(player, location, page,
                loc -> plugin.getGuardianTable().getBlockLogTable().countLog(loc),
                (loc, offset) -> plugin.getGuardianTable().getBlockLogTable().inspectLog(loc, pageSize, offset),
                (p, pg) -> showBlockLogs(p, location, pg));
    }

    private void showContainerLogs(Player player, BasicLocation location, int page) {
        showLogs(player, location, page,
                loc -> plugin.getGuardianTable().getContainerLogTable().countLog(loc),
                (loc, offset) -> plugin.getGuardianTable().getContainerLogTable().inspectLog(loc, pageSize, offset),
                (p, pg) -> showContainerLogs(p, location, pg));
    }

    private void showInteractLogs(Player player, BasicLocation location, int page) {
        showLogs(player, location, page,
                loc -> plugin.getGuardianTable().getInteractLogTable().countLog(loc),
                (loc, offset) -> plugin.getGuardianTable().getInteractLogTable().inspectLog(loc, pageSize, offset),
                (p, pg) -> showInteractLogs(p, location, pg));
    }

    private <T extends Logs> void showLogs(Player player, BasicLocation location, int page, Function<BasicLocation, CompletableFuture<Integer>> countFn, BiFunction<BasicLocation, Integer, CompletableFuture<List<T>>> fetchFn, BiConsumer<Player, Integer> selfCall) {
        int pageNumber = Math.max(page, 1);

        countFn.apply(location).thenCompose(total -> {
            if (total == 0) {
                plugin.getLangLoader().send(player, LangKey.NO_INTERACTION);
                return CompletableFuture.completedFuture(null);
            }

            int maxPage = Math.max(1, (int) Math.ceil((double) total / pageSize));
            int clampedPage = Math.min(pageNumber, maxPage);
            int offset = (clampedPage - 1) * pageSize;

            return fetchFn.apply(location, offset)
                    .thenAccept(results -> sendInspect(player,
                            results.stream()
                                    .map(log -> LogUtils.formatLogs(log, player, plugin))
                                    .collect(Collectors.toList()),
                            clampedPage,
                            maxPage,
                            clicked -> selfCall.accept(clicked, clampedPage - 1),
                            clicked -> selfCall.accept(clicked, clampedPage + 1)));
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

    @Override
    public void showPlayerLogs(Player sender, String playerName, int page) {
        int pageNumber = Math.max(page, 1);
        GuardianTable table = plugin.getGuardianTable();

        CompletableFuture<Integer> blockCount = table.getBlockLogTable().countLogsByPlayer(playerName);
        CompletableFuture<Integer> containerCount = table.getContainerLogTable().countLogsByPlayer(playerName);
        CompletableFuture<Integer> itemCount = table.getItemLogTable().countLogsByPlayer(playerName);
        CompletableFuture<Integer> interactCount = table.getInteractLogTable().countLogsByPlayer(playerName);

        CompletableFuture.allOf(blockCount, containerCount, itemCount, interactCount).thenRun(() -> {
            int total = blockCount.join() + containerCount.join() + itemCount.join() + interactCount.join();

            if (total == 0) {
                plugin.getLangLoader().send(sender, LangKey.LOOKUP_NO_LOGS);
                return;
            }

            plugin.getLangLoader().send(sender, LangKey.LOOKUP_HEADER, Placeholder.parsed("player", playerName));

            int maxPage = Math.max(1, (int) Math.ceil((double) total / pageSize));
            int clampedPage = Math.min(pageNumber, maxPage);
            int offset = (clampedPage - 1) * pageSize;

            CompletableFuture<List<BlockLog>> blockLogs = table.getBlockLogTable().searchByPlayer(playerName, total, 0);
            CompletableFuture<List<ContainerLog>> containerLogs = table.getContainerLogTable().searchByPlayer(playerName, total, 0);
            CompletableFuture<List<ItemLog>> itemLogs = table.getItemLogTable().searchByPlayer(playerName, total, 0);
            CompletableFuture<List<InteractLog>> interactLogs = table.getInteractLogTable().searchByPlayer(playerName, total, 0);

            CompletableFuture.allOf(blockLogs, containerLogs, itemLogs, interactLogs).thenRun(() -> {
                List<Logs> allLogs = new ArrayList<>();
                allLogs.addAll(blockLogs.join());
                allLogs.addAll(containerLogs.join());
                allLogs.addAll(itemLogs.join());
                allLogs.addAll(interactLogs.join());

                allLogs.sort((a, b) -> b.getDate().compareTo(a.getDate()));

                int start = Math.min(offset, allLogs.size());
                int end = Math.min(offset + pageSize, allLogs.size());
                List<Logs> pageLogs = allLogs.subList(start, end);

                sendInspect(sender,
                        pageLogs.stream()
                                .map(log -> LogUtils.formatHistory(log, sender, plugin))
                                .collect(Collectors.toList()),
                        clampedPage,
                        maxPage,
                        clicked -> showPlayerLogs(clicked, playerName, clampedPage - 1),
                        clicked -> showPlayerLogs(clicked, playerName, clampedPage + 1));
            });
        });
    }

    @Override
    public void showPlayerStats(Player sender, String playerName) {
        GuardianTable table = plugin.getGuardianTable();

        CompletableFuture<Integer> blockCount = table.getBlockLogTable().countLogsByPlayer(playerName);
        CompletableFuture<Integer> containerCount = table.getContainerLogTable().countLogsByPlayer(playerName);
        CompletableFuture<Integer> itemCount = table.getItemLogTable().countLogsByPlayer(playerName);
        CompletableFuture<Integer> interactCount = table.getInteractLogTable().countLogsByPlayer(playerName);

        CompletableFuture.allOf(blockCount, containerCount, itemCount, interactCount).thenRun(() -> {
            int blocks = blockCount.join();
            int containers = containerCount.join();
            int items = itemCount.join();
            int interacts = interactCount.join();

            if (blocks + containers + items + interacts == 0) {
                plugin.getLangLoader().sendRaw(sender, LangKey.STATS_NO_LOGS);
                return;
            }

            plugin.getLangLoader().sendRaw(sender, LangKey.STATS_HEADER, Placeholder.parsed("player", playerName));
            plugin.getLangLoader().sendRaw(sender, LangKey.STATS_BLOCKS, Placeholder.parsed("count", String.valueOf(blocks)));
            plugin.getLangLoader().sendRaw(sender, LangKey.STATS_CONTAINERS, Placeholder.parsed("count", String.valueOf(containers)));
            plugin.getLangLoader().sendRaw(sender, LangKey.STATS_ITEMS, Placeholder.parsed("count", String.valueOf(items)));
            plugin.getLangLoader().sendRaw(sender, LangKey.STATS_INTERACTS, Placeholder.parsed("count", String.valueOf(interacts)));

            table.getBlockLogTable().countLogsByPlayer(playerName).thenAccept(total -> {
                if (total > 0) {
                    table.getBlockLogTable().searchByPlayer(playerName, 1, total - 1).thenAccept(oldest -> {
                        if (!oldest.isEmpty()) {
                            plugin.getLangLoader().sendRaw(sender, LangKey.STATS_FIRST_SEEN, Placeholder.parsed("date", oldest.getFirst().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))));
                        }
                    });
                }
            });
            table.getBlockLogTable().searchByPlayer(playerName, 1, 0).thenAccept(newest -> {
                if (!newest.isEmpty()) {
                    plugin.getLangLoader().sendRaw(sender, LangKey.STATS_LAST_SEEN, Placeholder.parsed("date", newest.getFirst().getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))));
                }
            });
        });
    }

    @Override
    public void purgeOldLogs(int days) {
        GuardianTable table = plugin.getGuardianTable();
        handleResult(table.getBlockLogTable().removeOldLog(days), "purge block log");
        handleResult(table.getContainerLogTable().removeOldLog(days), "purge container log");
        handleResult(table.getItemLogTable().removeOldLog(days), "purge item log");
        handleResult(table.getInteractLogTable().removeOldLog(days), "purge interact log");
    }

    private void handleResult(CompletableFuture<Boolean> future, String context) {
        future.whenComplete((success, throwable) -> {
            if (throwable != null) {
                plugin.getLogger().log(Level.SEVERE, "[Guardian] Unexpected error during " + context, throwable);
            } else if (!success) {
                plugin.getLogger().warning("[Guardian] Operation failed: " + context);
            }
        });
    }
}
