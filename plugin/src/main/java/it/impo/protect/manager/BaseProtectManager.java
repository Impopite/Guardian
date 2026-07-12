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
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.ConfigKey;
import it.impo.protect.config.constant.LangKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class BaseProtectManager extends ProtectManager {

    private final Protect plugin;
    private final Set<UUID> inspectors;
    private final LangLoader lang;

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
    public void saveBlockLog(Player player, boolean staff, Block block, Action action, BasicLocation location) {
        BlockLog blockLog = new BlockLog(player, location, LocalDateTime.now(), staff, block.getType().toString(), block.getBlockData().toString() ,action);

        saveLogs(blockLog);
    }

    @Override
    public void saveContainerLog(Player player, boolean staff, Block block, ItemStack itemStack, int amount, ContainerAction action, BasicLocation location) throws IOException {
        ContainerLog containerLog = new ContainerLog(player, ItemSerializer.itemToBytes(itemStack), LocalDateTime.now(), amount, staff, action, location, getContainerType(block));

        saveLogs(containerLog);
    }

    @Override
    public void saveItemLog(Player player, boolean staff, ItemStack itemStack, int amount, ItemAction action, BasicLocation location) throws IOException {
        ItemLog itemLog = new ItemLog(player, LocalDateTime.now(), staff, ItemSerializer.itemToBytes(itemStack), amount, action, location);

        saveLogs(itemLog);
    }

    @Override
    public void saveInteractLog(Player player, boolean staff, Block block, Interaction interaction, BasicLocation location) {
        InteractLog interactLog = new InteractLog(player, location, LocalDateTime.now(), staff, block.getType().toString(), interaction);

        saveLogs(interactLog);
    }

    // TODO -> see if @return boolean is better for inspect command
    @Override
    public void toggleInspect(Player player) {
        UUID uuid = player.getUniqueId();

        if(inspectors.remove(uuid)) return;
        inspectors.add(uuid);
    }

    // TODO -> finish
    @Override
    public void showBlockLogs(Player player, BasicLocation location) {
        ProtectTable table = plugin.getProtectTable();

        table.getBlockLogTable().countLog(location).thenCompose(log -> {
            if(log == 0){
                lang.send(player, LangKey.NO_INTERACTION);
                return CompletableFuture.completedFuture(null);
            }

            int pageNumber = ConfigKey.PAGE_NUMBER.ordinal();
            int pageSize = ConfigKey.PAGE_SIZE.ordinal();

            return null;
        });

        /*
        ProtectTable table = module.getTable();
        table.countInspectBlockLogs(loc).thenCompose(total -> {
            if (total == 0) {
                player.sendMessage(ChatUtils.error("Nessuna interazione", "Non ci sono interazioni registrate per questo blocco."));
                return CompletableFuture.completedFuture(null);
            }

            int maxPage = Math.max(1, (int) Math.ceil((double) total / INSPECT_PAGE_SIZE));
            int clampedPage = Math.min(pageNumber, maxPage);
            int offset = (clampedPage - 1) * INSPECT_PAGE_SIZE;

            return table.inspectBlockLogs(loc, INSPECT_PAGE_SIZE, offset)
                    .thenAccept(results -> sendInspect(player,
                            results.stream()
                                    .map(log -> ProtectLogUtils.formatInspectBlockComponent(log, module.getFurnitureModule()))
                                    .collect(Collectors.toList()),
                            clampedPage,
                            maxPage,
                            clicked -> showBlockLogs(clicked, loc, clampedPage - 1),
                            clicked -> showBlockLogs(clicked, loc, clampedPage + 1)));
        });
        */
    }

    @Override
    public void showContainerLogs(Player player, BasicLocation location) {

    }

    @Override
    public void showInteractLogs(Player player, BasicLocation location) {

    }

    @Override
    public boolean isInspecting(Player player) {
        if(player == null) return false;
        return inspectors.contains(player.getUniqueId());
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
