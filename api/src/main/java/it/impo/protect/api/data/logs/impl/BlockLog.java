package it.impo.protect.api.data.logs.impl;

import it.impo.protect.api.data.Action.Action;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.Logs;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class BlockLog extends Logs {

    private final String blockType;
    private final String blockData;
    private final Action action;

    public BlockLog(Player player, BasicLocation location, LocalDateTime date, boolean staff, String blockType, String blockData, Action action) {
        super(player, date, staff, location);
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
