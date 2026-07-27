package it.impo.guardian.api.utils;

public enum Permission {
    GUARDIAN_STAFF("staff"),
    GUARDIAN_INSPECT("inspect"),
    GUARDIAN_ROLLBACK("rollback"),
    ;

    private final String permission;

    Permission(String permission) {
        this.permission = "guardian." + permission;
    }

    public String getPermission() {
        return permission;
    }
}
