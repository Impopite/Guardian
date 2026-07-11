package it.impo.defaultProject.api.database;

import java.sql.SQLException;

public abstract class DefaultTable {

    /**
     * Creates the table in the database if it does not already exist.
     * Called once during plugin startup.
     *
     * @throws SQLException if the table creation query fails
     */
    public abstract void createTable() throws SQLException;
}
