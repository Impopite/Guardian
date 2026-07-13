package it.impo.protect.api.data.logs.impl;

import it.impo.protect.api.data.Action.ContainerAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.ContainerType;
import it.impo.protect.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

public class ContainerLog extends Logs {

    private final byte[] item;
    private final int amount;
    private final ContainerAction action;
    private final ContainerType containerType;

    public ContainerLog(UUID uuid, String playerName, byte[] item, LocalDateTime date, int amount, boolean staff, ContainerAction action, BasicLocation location, ContainerType containerType) {
        super(uuid, playerName, date, staff, location);
        this.item = item;
        this.amount = amount;
        this.action = action;
        this.containerType = containerType;
    }

    public byte[] getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public ContainerAction getAction() {
        return action;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

}
