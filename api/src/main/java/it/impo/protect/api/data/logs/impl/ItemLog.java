package it.impo.protect.api.data.logs.impl;

import it.impo.protect.api.data.Action.ItemAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.Logs;

import java.time.LocalDateTime;
import java.util.UUID;

public class ItemLog extends Logs {

    private final byte[] item;
    private final int amount;
    private final ItemAction action;

    public ItemLog(UUID uuid,String playerName, LocalDateTime date, boolean staff, byte[] item, int amount, ItemAction action, BasicLocation location) {
        super(uuid, playerName, date, staff, location);
        this.item = item;
        this.amount = amount;
        this.action = action;
    }

    public byte[] getItem() {
        return item;
    }

    public int getAmount() {
        return amount;
    }

    public ItemAction getAction() {
        return action;
    }

}
