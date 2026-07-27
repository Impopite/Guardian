package it.impo.guardian.api.data.logs.impl;

import it.impo.guardian.api.data.Action.Interaction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

public class InteractLog extends Logs {

    private final String blockType;
    private final Interaction action;

    public InteractLog(UUID uuid, String playerName, BasicLocation location, LocalDateTime date, boolean staff, String blockType, Interaction action) {
        super(uuid, playerName, date, staff, location);
        this.blockType = blockType;
        this.action = action;
    }

    public String getBlockType() {
        return blockType;
    }

    public Interaction getAction() {
        return action;
    }
}
