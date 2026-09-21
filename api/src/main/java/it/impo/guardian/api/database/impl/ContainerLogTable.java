package it.impo.guardian.api.database.impl;

import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.impl.ContainerLog;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Data access object for the container log table.
 *
 * <p>All the operations that touch the database return a {@link CompletableFuture} and are
 * executed on a dedicated executor, so they are safe to call from any thread.</p>
 */
public abstract class ContainerLogTable {

    /**
     * Creates the underlying SQL table if it does not exist yet.
     *
     * @throws SQLException on any database access error
     */
    public abstract void createTable() throws SQLException;

    /**
     * Inserts a container log entry.
     *
     * @param log the log to insert
     * @return a future completing with {@code true} if the row was inserted
     */
    public abstract CompletableFuture<Boolean> addLog(ContainerLog log);

    /**
     * Deletes a container log entry by its database id.
     *
     * @param id the id of the log to remove
     * @return a future completing with {@code true} if a row was deleted
     */
    public abstract CompletableFuture<Boolean> removeLog(int id);

    /**
     * Deletes every container log older than the given number of days.
     *
     * @param days the minimum age in days that a log must have to be removed
     * @return a future completing with {@code true} if any row was deleted
     */
    public abstract CompletableFuture<Boolean> removeOldLog(int days);

    /**
     * Counts the container logs recorded at a location.
     *
     * @param location the location to count
     * @return a future completing with the number of matching logs
     */
    public abstract CompletableFuture<Integer> countLog(BasicLocation location);

    /**
     * Returns a page of container logs recorded at a location, ordered from the most recent.
     *
     * @param location the location to inspect
     * @param limit    the maximum number of logs to return
     * @param offset   the number of logs to skip
     * @return a future completing with the matching logs
     */
    public abstract CompletableFuture<List<ContainerLog>> inspectLog(BasicLocation location, int limit, int offset);

    /**
     * Returns the container logs recorded within a cubic radius around a center and not older
     * than a given date, ordered from the most recent. Used to feed rollback operations.
     *
     * @param center the center of the search
     * @param radius the radius, in blocks, around the center
     * @param since  the earliest date to consider
     * @return a future completing with the matching logs
     */
    public abstract CompletableFuture<List<ContainerLog>> rollbackLogs(BasicLocation center, int radius, LocalDateTime since);

    /**
     * Counts the container logs recorded for a player name.
     *
     * @param playerName the name of the player
     * @return a future completing with the number of matching logs
     */
    public abstract CompletableFuture<Integer> countLogsByPlayer(String playerName);

    /**
     * Returns a page of container logs recorded for a player name, ordered from the most recent.
     *
     * @param playerName the name of the player
     * @param limit      the maximum number of logs to return
     * @param offset     the number of logs to skip
     * @return a future completing with the matching logs
     */
    public abstract CompletableFuture<List<ContainerLog>> searchByPlayer(String playerName, int limit, int offset);
}