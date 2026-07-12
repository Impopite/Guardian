package it.impo.protect.api.database.impl;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public abstract class ContainerLogTable {

    /**
     * Creates the table in the database if it does not already exist.
     * Called once during plugin startup.
     *
     * @throws SQLException if the table creation query fails
     */
    public abstract void createTable() throws SQLException;

    public abstract CompletableFuture<Boolean> addLog(UUID userUuid, String username, String world, int x, int y, int z, String containerType, byte[] item, int amount, String action, boolean staff);

    public abstract CompletableFuture<Boolean> removeLog(int id);

    public abstract CompletableFuture<Boolean> removeOldLog(int days);

    public abstract CompletableFuture<Integer> countLog(String world, int x, int y, int z);
}
