package it.impo.protect.api.utils;

public enum Permission {
    PROTECT_STAFF("staff"),
    PROTECT_INSPECT("inspect"),
    ;

    private final String permission;

    Permission(String permission) {
        this.permission = "protect." + permission;
    }

    public String getPermission() {
        return permission;
    }
}
