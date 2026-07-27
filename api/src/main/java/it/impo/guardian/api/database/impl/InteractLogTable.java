package it.impo.guardian.api.database.impl;

import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.impl.InteractLog;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class InteractLogTable {

    public abstract void createTable() throws SQLException;

    public abstract CompletableFuture<Boolean> addLog(InteractLog log);

    public abstract CompletableFuture<Boolean> removeLog(int id);

    public abstract CompletableFuture<Boolean> removeOldLog(int days);

    public abstract CompletableFuture<Integer> countLog(BasicLocation location);

    public abstract CompletableFuture<List<InteractLog>> inspectLog(BasicLocation location, int limit, int offset);

    public abstract CompletableFuture<Integer> countLogsByPlayer(String playerName);

    public abstract CompletableFuture<List<InteractLog>> searchByPlayer(String playerName, int limit, int offset);
}

