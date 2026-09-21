package it.impo.guardian.api.manager;

import org.bukkit.entity.Player;

import java.time.Duration;

/**
 * Provides the operations that revert the changes logged by Guardian.
 *
 * <p>The rollbacks are asynchronous: the affected blocks/containers are modified on the main
 * thread once the matching log rows have been loaded. The player performing the rollback
 * receives a summary message with the result.</p>
 *
 * <p>Obtain an instance through {@link it.impo.guardian.api.GuardianApi#getRollbackManager()}.</p>
 */
public abstract class RollbackManager {

    /**
     * Rolls back the logged block changes made within the given radius and time range,
     * centered on the player's current location.
     *
     * @param player   the player used as the center of the rollback and that receives the summary
     * @param radius   the radius, in blocks, around the player to consider
     * @param duration the maximum age of the changes to roll back
     */
    public abstract void rollbackBlocks(Player player, int radius, Duration duration);

    /**
     * Rolls back the logged container changes made within the given radius and time range,
     * centered on the player's current location.
     *
     * @param player   the player used as the center of the rollback and that receives the summary
     * @param radius   the radius, in blocks, around the player to consider
     * @param duration the maximum age of the changes to roll back
     */
    public abstract void rollbackContainers(Player player, int radius, Duration duration);
}