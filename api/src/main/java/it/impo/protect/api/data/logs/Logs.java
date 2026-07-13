package it.impo.protect.api.data.logs;

import it.impo.protect.api.data.BasicLocation;

import java.time.LocalDateTime;
import java.util.UUID;

public abstract class Logs {

    private int id;
    private final UUID uuid;
    private final String playerName;
    private final LocalDateTime date;
    private final boolean staff;
    private final BasicLocation location;

    protected Logs(UUID uuid, String playerName, LocalDateTime date, boolean staff, BasicLocation location) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.date = date;
        this.staff = staff;
        this.location = location;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPlayerName() {
        return playerName;
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
