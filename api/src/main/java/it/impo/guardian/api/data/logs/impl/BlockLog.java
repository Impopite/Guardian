package it.impo.guardian.api.data.logs.impl;

import it.impo.guardian.api.data.action.Action;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * A log entry for a block that was placed or broken.
 */
public class BlockLog extends Logs {

    private final String blockType;
    private final String blockData;
    private final Action action;

    /**
     * @param uuid       the UUID of the player that caused the action
     * @param playerName the name of the player at the time of the action
     * @param location   where the block is
     * @param date       when the action happened
     * @param staff      whether the player had the Guardian staff permission
     * @param blockType  the material name of the block
     * @param blockData  the serialized block data (may be {@code null})
     * @param action     whether the block was placed or broken
     */
    public BlockLog(UUID uuid, String playerName, BasicLocation location, LocalDateTime date, boolean staff, String blockType, String blockData, Action action) {
        super(uuid, playerName, date, staff, location);
        this.blockType = blockType;
        this.blockData = blockData;
        this.action = action;
    }

    /**
     * Returns the material name of the block.
     *
     * @return the block type
     */
    public String getBlockType() {
        return blockType;
    }

    /**
     * Returns the serialized block data (state) recorded for the block.
     *
     * @return the block data, possibly {@code null}
     */
    public String getBlockData() {
        return blockData;
    }

    /**
     * Returns whether the block was placed or broken.
     *
     * @return the {@link Action} of this entry
     */
    public Action getAction() {
        return action;
    }
}