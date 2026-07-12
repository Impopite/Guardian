package it.impo.protect.api.data.logs.impl;

import it.impo.protect.api.data.Action.ItemAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.Logs;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public class ItemLog extends Logs {

    private final byte[] item;
    private final int amount;
    private final ItemAction action;

    public ItemLog(Player player, LocalDateTime date, boolean staff, byte[] item, int amount, ItemAction action, BasicLocation location) {
        super(player, date, staff, location);
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
