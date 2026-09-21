package it.impo.guardian.api.manager;

import it.impo.guardian.api.data.action.Action;
import it.impo.guardian.api.data.action.ContainerAction;
import it.impo.guardian.api.data.action.Interaction;
import it.impo.guardian.api.data.action.ItemAction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.Logs;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Provides the operations behind the logging and inspection features of Guardian.
 *
 * <p>All the {@code saveXxxLog} methods build the corresponding log entry and store it
 * asynchronously; failures are logged by the plugin and never throw.</p>
 *
 * <p>Obtain an instance through {@link it.impo.guardian.api.GuardianApi#getGuardianManager()}.</p>
 */
public abstract class GuardianManager {

    /**
     * Saves a generic log to its correct table, based on its concrete type.
     *
     * @param logs the log to save; ignored if {@code null}
     */
    public abstract void saveLogs(Logs logs);

    /**
     * Builds and saves a {@link it.impo.guardian.api.data.logs.impl.BlockLog} for a block
     * placed or broken by a player.
     *
     * @param player   the player responsible for the action
     * @param block    the block involved
     * @param action   whether the block was placed or broken
     * @param location the location where the block is situated
     */
    public abstract void saveBlockLog(Player player, Block block, Action action, BasicLocation location);

    /**
     * Builds and saves a {@link it.impo.guardian.api.data.logs.impl.ContainerLog} for an item
     * added to or removed from a container.
     *
     * @param player    the player responsible for the action
     * @param block     the container block involved
     * @param itemStack the affected item
     * @param amount    the number of affected items
     * @param action    whether the item was added or removed
     * @param location  the location of the container
     */
    public abstract void saveContainerLog(Player player, Block block, ItemStack itemStack, int amount, ContainerAction action, BasicLocation location);

    /**
     * Builds and saves an {@link it.impo.guardian.api.data.logs.impl.ItemLog} for an item
     * picked up or dropped by a player.
     *
     * @param player    the player responsible for the action
     * @param itemStack the affected item
     * @param amount    the number of affected items
     * @param action    whether the item was picked up or dropped
     * @param location  the location where the action happened
     */
    public abstract void saveItemLog(Player player, ItemStack itemStack, int amount, ItemAction action, BasicLocation location);

    /**
     * Builds and saves an {@link it.impo.guardian.api.data.logs.impl.InteractLog} for a block
     * opened or closed by a player.
     *
     * @param player      the player responsible for the interaction
     * @param block       the block interacted with
     * @param interaction whether the block was opened or closed
     * @param location    the location of the block
     */
    public abstract void saveInteractLog(Player player, Block block, Interaction interaction, BasicLocation location);

    /**
     * Toggles the inspect mode for a player.
     *
     * @param player the player to toggle
     * @return {@code true} if the player is now in inspect mode, {@code false} if it was switched off
     */
    public abstract boolean toggleInspect(Player player);

    /**
     * Shows the player the block logs recorded at the given location, starting from the
     * first page. Messages use the paginated inspect layout.
     *
     * @param player   the player that will receive the logs
     * @param location the location to inspect
     */
    public abstract void showBlockLogs(Player player, BasicLocation location);

    /**
     * Shows the player the container logs recorded at the given location, starting from the
     * first page. Messages use the paginated inspect layout.
     *
     * @param player   the player that will receive the logs
     * @param location the location to inspect
     */
    public abstract void showContainerLogs(Player player, BasicLocation location);

    /**
     * Shows the player the interaction logs recorded at the given location, starting from the
     * first page. Messages use the paginated inspect layout.
     *
     * @param player   the player that will receive the logs
     * @param location the location to inspect
     */
    public abstract void showInteractLogs(Player player, BasicLocation location);

    /**
     * Checks whether a player is currently in inspect mode.
     *
     * @param player the player to check
     * @return {@code true} if the player is inspecting, {@code false} otherwise or if the player is {@code null}
     */
    public abstract boolean isInspecting(Player player);

    /**
     * Shows the given player the paginated history of every log recorded for a player name.
     *
     * @param sender     the player that will receive the history
     * @param playerName the name of the player whose logs are searched
     * @param page       the page to show (clamped to a valid page; values below 1 act as 1)
     */
    public abstract void showPlayerLogs(Player sender, String playerName, int page);

    /**
     * Shows the given player a stats summary (count and first/last seen) for a player name.
     *
     * @param sender     the player that will receive the stats
     * @param playerName the name of the player whose stats are computed
     */
    public abstract void showPlayerStats(Player sender, String playerName);

    /**
     * Deletes every log older than the given number of days from all the log tables.
     *
     * @param days the minimum age in days that a log must have to be removed
     */
    public abstract void purgeOldLogs(int days);
}