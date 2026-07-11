package it.impo.protect.api.data.logs;

import it.impo.protect.api.data.BasicLocation;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;

public abstract class Logs {

    private int id;
    private final Player player;
    private final LocalDateTime date;
    private final boolean staff;
    private final BasicLocation location;

    protected Logs(Player player, LocalDateTime date, boolean staff, BasicLocation location) {
        this.player = player;
        this.date = date;
        this.staff = staff;
        this.location = location;
    }
    public Player getPlayer() {
        return player;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public boolean isStaff() {
        return staff;
    }

    public BasicLocation getLocation() {
        return location;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



}
