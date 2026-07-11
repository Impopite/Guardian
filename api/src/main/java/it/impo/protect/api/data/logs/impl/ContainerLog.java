package it.impo.protect.api.data.logs.impl;

import it.impo.protect.api.data.Action.ContainerAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.ContainerType;
import it.impo.protect.api.data.logs.Logs;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class ContainerLog extends Logs {

    private final byte[] item;
    private final int amount;
    private final ContainerAction action;
    private final ContainerType containerType;

    public ContainerLog(Player player, byte[] item, LocalDateTime date, int amount, boolean staff, String action, BasicLocation location, ContainerType containerType) {
        super(player, date, staff, location);
        this.item = item;
        this.amount = amount;
        this.action = ContainerAction.valueOf(action.toUpperCase());
        this.containerType = containerType;
    }

    public ContainerLog(Player player, byte[] item, LocalDateTime date, int amount, boolean staff, ContainerAction action, BasicLocation location, ContainerType containerType) {
        super(player, date, staff, location);
        this.item = item;
        this.amount = amount;
        this.action = action;
        this.containerType = containerType;
    }

}
