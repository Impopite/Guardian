package it.impo.protect.api.data.logs.impl;

import it.impo.protect.api.data.Action.Interaction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.Logs;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class InteractLog extends Logs {

    private final String blockType;
    private final Interaction action;


    public InteractLog(Player player, BasicLocation location, LocalDateTime date, boolean staff, String blockType, String action) {
        super(player, date, staff, location);
        this.blockType = blockType;
        this.action = Interaction.valueOf(action.toUpperCase());
    }

    public InteractLog(Player player, BasicLocation location, LocalDateTime date, boolean staff, String blockType, Interaction action) {
        super(player, date, staff, location);
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
