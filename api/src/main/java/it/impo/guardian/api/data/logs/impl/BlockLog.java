package it.impo.guardian.api.data.logs.impl;

import it.impo.guardian.api.data.action.Action;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

public class BlockLog extends Logs {

    private final String blockType;
    private final String blockData;
    private final Action action;

    public BlockLog(UUID uuid, String playerName, BasicLocation location, LocalDateTime date, boolean staff, String blockType, String blockData, Action action) {
        super(uuid, playerName, date, staff, location);
        this.blockType = blockType;
        this.blockData = blockData;
        this.action = action;
    }

    public String getBlockType() {
        return blockType;
    }

    public String getBlockData() {
        return blockData;
    }

    public Action getAction() {
        return action;
    }
}
