package it.impo.guardian.api.data.logs.impl;

import it.impo.guardian.api.data.action.ItemAction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A log entry for an item picked up or dropped by a player.
 */
public class ItemLog extends Logs {

    private final byte[] item;
    private final int amount;
    private final ItemAction action;

    /**
     * @param uuid       the UUID of the player that caused the action
     * @param playerName the name of the player at the time of the action
     * @param date       when the action happened
     * @param staff      whether the player had the Guardian staff permission
     * @param item       the serialized item bytes
     * @param amount     the number of affected items
     * @param action     whether the item was picked up or dropped
     * @param location   where the action happened
     */
    public ItemLog(UUID uuid, String playerName, LocalDateTime date, boolean staff, byte[] item, int amount, ItemAction action, BasicLocation location) {
        super(uuid, playerName, date, staff, location);
        this.item = item;
        this.amount = amount;
        this.action = action;
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
     * Returns whether the item was picked up or dropped.
     *
     * @return the {@link ItemAction} of this entry
     */
    public ItemAction getAction() {
        return action;
    }
}