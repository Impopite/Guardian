package it.impo.guardian.api.data.logs.impl;

import it.impo.guardian.api.data.action.Interaction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A log entry for a block that was opened or closed by a player.
 */
public class InteractLog extends Logs {

    private final String blockType;
    private final Interaction action;

    /**
     * @param uuid       the UUID of the player that caused the interaction
     * @param playerName the name of the player at the time of the interaction
     * @param location   where the block is
     * @param date       when the interaction happened
     * @param staff      whether the player had the Guardian staff permission
     * @param blockType  the material name of the block
     * @param action     whether the block was opened or closed
     */
    public InteractLog(UUID uuid, String playerName, BasicLocation location, LocalDateTime date, boolean staff, String blockType, Interaction action) {
        super(uuid, playerName, date, staff, location);
        this.blockType = blockType;
        this.action = action;
    }

    /**
     * Returns the material name of the interacted block.
     *
     * @return the block type
     */
    public String getBlockType() {
        return blockType;
    }

    /**
     * Returns whether the block was opened or closed.
     *
     * @return the {@link Interaction} of this entry
     */
    public Interaction getAction() {
        return action;
    }
}