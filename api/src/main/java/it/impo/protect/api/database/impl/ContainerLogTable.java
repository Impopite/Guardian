package it.impo.protect.api.database.impl;

import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.impl.ContainerLog;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class ContainerLogTable {

    /**
     * Creates the table in the database if it does not already exist.
     * Called once during plugin startup.
     *
     * @throws SQLException if the table creation query fails
     */
    public abstract void createTable() throws SQLException;

    public abstract CompletableFuture<Boolean> addLog(ContainerLog log);

    public abstract CompletableFuture<Boolean> removeLog(int id);

    public abstract CompletableFuture<Boolean> removeOldLog(int days);

    public abstract CompletableFuture<Integer> countLog(BasicLocation location);

    public abstract CompletableFuture<List<ContainerLog>> inspectLog(BasicLocation location, int limit, int offset);

    public abstract CompletableFuture<List<ContainerLog>> rollbackLogs(BasicLocation center, int radius, LocalDateTime since);
}
