package it.impo.protect.api.database.impl;

import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.impl.ItemLog;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ItemLogTable {

    /**
     * Creates the table in the database if it does not already exist.
     * Called once during plugin startup.
     *
     * @throws SQLException if the table creation query fails
     */
    public abstract void createTable() throws SQLException;

    public abstract CompletableFuture<Boolean> addLog(ItemLog log);

    public abstract CompletableFuture<Boolean> removeLog(int id);

    public abstract CompletableFuture<Boolean> removeOldLog(int days);

    public abstract CompletableFuture<Integer> countLog(BasicLocation location);

    public abstract CompletableFuture<List<ItemLog>> inspectLog(BasicLocation location, int limit, int offset);

    public abstract CompletableFuture<Integer> countLogsByPlayer(String playerName);

    public abstract CompletableFuture<List<ItemLog>> searchByPlayer(String playerName, int limit, int offset);
}

