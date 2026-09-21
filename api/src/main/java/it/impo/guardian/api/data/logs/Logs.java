package it.impo.guardian.api.data.logs;

import it.impo.guardian.api.data.BasicLocation;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class shared by every type of log stored by Guardian.
 *
 * <p>Concrete subtypes add the type specific data (block, item, container, interaction).</p>
 */
public abstract class Logs {

    private int id;
    private final UUID uuid;
    private final String playerName;
    private final LocalDateTime date;
    private final boolean staff;
    private final BasicLocation location;

    /**
     * @param uuid       the UUID of the player that triggered the log
     * @param playerName the name of the player at the time of the action
     * @param date       the moment the action happened
     * @param staff      whether the player had the Guardian staff permission when it happened
     * @param location   where the action happened
     */
    protected Logs(UUID uuid, String playerName, LocalDateTime date, boolean staff, BasicLocation location) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.date = date;
        this.staff = staff;
        this.location = location;
    }

    /**
     * Returns the UUID of the player that caused the log entry.
     *
     * @return the player UUID
     */
    public UUID getUuid() {
        return uuid;
    }

    /**
     * Returns the name of the player at the time of the action.
     *
     * @return the player name
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Returns when the action happened.
     *
     * @return the timestamp of the log entry
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Returns whether the player had the Guardian staff permission while performing the action.
     *
     * @return {@code true} if the player was staff, {@code false} otherwise
     */
    public boolean isStaff() {
        return staff;
    }

    /**
     * Returns the location where the action happened.
     *
     * @return the {@link BasicLocation} of the log entry
     */
    public BasicLocation getLocation() {
        return location;
    }

    /**
     * Returns the database identifier of this log entry.
     *
     * @return the log id, or {@code 0} if it has not been persisted yet
     */
    public int getId() {
        return id;
    }

    /**
     * Sets the database identifier assigned to this log entry.
     *
     * @param id the log id
     */
    public void setId(int id) {
        this.id = id;
    }
}