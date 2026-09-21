package it.impo.guardian.api.database;

import it.impo.guardian.api.database.impl.BlockLogTable;
import it.impo.guardian.api.database.impl.ContainerLogTable;
import it.impo.guardian.api.database.impl.InteractLogTable;
import it.impo.guardian.api.database.impl.ItemLogTable;

import java.sql.SQLException;

/**
 * The database layer of Guardian, exposing the tables used to store the logs.
 *
 * <p>Obtain an instance through {@link it.impo.guardian.api.GuardianApi#getGuardianTable()}.</p>
 */
public abstract class GuardianTable {

    /**
     * Returns the table storing block logs.
     *
     * @return the {@link BlockLogTable} instance
     */
    public abstract BlockLogTable getBlockLogTable();

    /**
     * Returns the table storing container logs.
     *
     * @return the {@link ContainerLogTable} instance
     */
    public abstract ContainerLogTable getContainerLogTable();

    /**
     * Returns the table storing interaction logs.
     *
     * @return the {@link InteractLogTable} instance
     */
    public abstract InteractLogTable getInteractLogTable();

    /**
     * Returns the table storing item logs.
     *
     * @return the {@link ItemLogTable} instance
     */
    public abstract ItemLogTable getItemLogTable();

    /**
     * Creates the underlying SQL tables for every log type if they do not exist yet.
     *
     * @throws SQLException on any database access error
     */
    public abstract void createTables() throws SQLException;

    /**
     * Deletes every log older than the given number of days from all the log tables.
     *
     * @param days the minimum age in days that a log must have to be removed
     */
    public abstract void removeOldLogsAll(int days);
}