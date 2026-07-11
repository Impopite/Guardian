package it.impo.protect.api.database;

import java.sql.SQLException;

public abstract class ProtectTable {

    /**
     * Creates the table in the database if it does not already exist.
     * Called once during plugin startup.
     *
     * @throws SQLException if the table creation query fails
     */
    public abstract void createTable() throws SQLException;
}
