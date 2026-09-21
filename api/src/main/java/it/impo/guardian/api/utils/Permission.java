package it.impo.guardian.api.utils;

/**
 * The permissions used by Guardian to gate its staff features.
 */
public enum Permission {
    /** Grants access to the staff oriented features of Guardian. */
    GUARDIAN_STAFF("staff"),
    /** Grants access to the inspect feature. */
    GUARDIAN_INSPECT("inspect"),
    /** Grants access to the rollback feature. */
    GUARDIAN_ROLLBACK("rollback"),
    ;

    private final String permission;

    Permission(String permission) {
        this.permission = "guardian." + permission;
    }

    /**
     * Returns the full permission node, e.g. {@code guardian.inspect}.
     *
     * @return the permission node
     */
    public String getPermission() {
        return permission;
    }
}