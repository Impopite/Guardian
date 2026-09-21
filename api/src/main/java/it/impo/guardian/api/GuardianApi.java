package it.impo.guardian.api;

import it.impo.guardian.api.database.GuardianTable;
import it.impo.guardian.api.manager.GuardianManager;
import it.impo.guardian.api.manager.RollbackManager;

/**
 * Main entry point of the Guardian API.
 *
 * <p>The Guardian plugin itself implements this interface. To obtain an instance at
 * runtime, look up the plugin and cast it, for example:</p>
 *
 * <pre>{@code
 * Plugin plugin = Bukkit.getPluginManager().getPlugin("Guardian");
 * if (plugin instanceof GuardianApi api) {
 *     GuardianManager manager = api.getGuardianManager();
 * }
 * }</pre>
 *
 * <p>The returned instances are the same ones created by the plugin at startup, so they
 * are safe to cache.</p>
 */
public interface GuardianApi {

    /**
     * Returns the database layer that exposes the tables where the logs are stored.
     *
     * @return the {@link GuardianTable} giving access to all the log tables
     */
    GuardianTable getGuardianTable();

    /**
     * Returns the manager used to log events and to handle the inspect and lookup features.
     *
     * @return the {@link GuardianManager} instance
     */
    GuardianManager getGuardianManager();

    /**
     * Returns the manager used to roll back between logged block and container changes.
     *
     * @return the {@link RollbackManager} instance
     */
    RollbackManager getRollbackManager();
}