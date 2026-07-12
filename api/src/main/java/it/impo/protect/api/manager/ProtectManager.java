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

import java.io.IOException;

public abstract class ProtectManager {

    public abstract void saveLogs(Logs logs);

    public abstract void saveBlockLog(Player player, boolean staff, Block block, Action action, BasicLocation location);

    public abstract void saveContainerLog(Player player, boolean staff, Block block, ItemStack itemStack, int amount, ContainerAction action, BasicLocation location) throws IOException;

    public abstract void saveItemLog(Player player, boolean staff, ItemStack itemStack, int amount, ItemAction action, BasicLocation location) throws IOException;

    public abstract void saveInteractLog(Player player, boolean staff, Block block, Interaction interaction, BasicLocation location);

    public abstract void toggleInspect(Player player);

    public abstract void showBlockLogs(Player player, BasicLocation location);

    public abstract void showContainerLogs(Player player, BasicLocation location);

    public abstract void showInteractLogs(Player player, BasicLocation location);

    public abstract boolean isInspecting(Player player);

}
