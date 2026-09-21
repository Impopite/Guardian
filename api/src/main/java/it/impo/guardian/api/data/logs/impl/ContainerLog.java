package it.impo.guardian.api.data.logs.impl;

import it.impo.guardian.api.data.action.ContainerAction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.ContainerType;
import it.impo.guardian.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A log entry for an item added to or removed from a container.
 */
public class ContainerLog extends Logs {

    private final byte[] item;
    private final int amount;
    private final ContainerAction action;
    private final ContainerType containerType;

    /**
     * @param uuid          the UUID of the player that caused the action
     * @param playerName    the name of the player at the time of the action
     * @param item          the serialized item bytes
     * @param date          when the action happened
     * @param amount        the number of affected items
     * @param staff         whether the player had the Guardian staff permission
     * @param action        whether the item was added or removed
     * @param location      where the container is
     * @param containerType the type of container involved
     */
    public ContainerLog(UUID uuid, String playerName, byte[] item, LocalDateTime date, int amount, boolean staff, ContainerAction action, BasicLocation location, ContainerType containerType) {
        super(uuid, playerName, date, staff, location);
        this.item = item;
        this.amount = amount;
        this.action = action;
        this.containerType = containerType;
    }

    /**
     * Returns the serialized item bytes.
     *
     * @return the item bytes, may be empty if the item was {@code null}
     */
    public byte[] getItem() {
        return item;
    }

    /**
     * Returns the number of affected items.
     *
     * @return the item amount
     */
    public int getAmount() {
        return amount;
    }

    /**
     * Returns whether the item was added or removed.
     *
     * @return the {@link ContainerAction} of this entry
     */
    public ContainerAction getAction() {
        return action;
    }

    /**
     * Returns the type of container involved.
     *
     * @return the {@link ContainerType} of this entry
     */
    public ContainerType getContainerType() {
        return containerType;
    }
}