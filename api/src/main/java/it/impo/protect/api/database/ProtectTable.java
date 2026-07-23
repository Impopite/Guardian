package it.impo.protect.api.database;

import it.impo.protect.api.database.impl.BlockLogTable;
import it.impo.protect.api.database.impl.ContainerLogTable;
import it.impo.protect.api.database.impl.InteractLogTable;
import it.impo.protect.api.database.impl.ItemLogTable;

import java.sql.SQLException;

public abstract class ProtectTable {

    public abstract BlockLogTable getBlockLogTable();

    public abstract ContainerLogTable getContainerLogTable();

    public abstract InteractLogTable getInteractLogTable();

    public abstract ItemLogTable getItemLogTable();

    /**
     * Creates the table in the database if it does not already exist.
     * Called once during plugin startup.
     *
     * @throws SQLException if the table creation query fails
     */
    public abstract void createTables() throws SQLException;

    /**
     * Deletes logs older than the specified number of days from all tables.
     */
    public abstract void removeOldLogsAll(int days);
}
