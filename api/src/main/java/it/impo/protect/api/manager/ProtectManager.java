package it.impo.protect.api.manager;

import it.impo.protect.api.data.Action.Action;
import it.impo.protect.api.data.Action.ContainerAction;
import it.impo.protect.api.data.Action.Interaction;
import it.impo.protect.api.data.Action.ItemAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.Logs;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class ProtectManager {

    public abstract void saveLogs(Logs logs);

    public abstract void saveBlockLog(Player player, Block block, Action action, BasicLocation location);

    public abstract void saveContainerLog(Player player, Block block, ItemStack itemStack, int amount, ContainerAction action, BasicLocation location);

    public abstract void saveItemLog(Player player, ItemStack itemStack, int amount, ItemAction action, BasicLocation location);

    public abstract void saveInteractLog(Player player, Block block, Interaction interaction, BasicLocation location);

    public abstract boolean toggleInspect(Player player);

    public abstract void showBlockLogs(Player player, BasicLocation location);

    public abstract void showContainerLogs(Player player, BasicLocation location);

    public abstract void showInteractLogs(Player player, BasicLocation location);

    public abstract boolean isInspecting(Player player);

}
