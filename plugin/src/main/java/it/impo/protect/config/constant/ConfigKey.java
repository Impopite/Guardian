package it.impo.protect.config.constant;

public enum ConfigKey {

    DATABASE_HOST("database.host"),
    DATABASE_NAME("database.name"),
    DATABASE_USERNAME("database.username"),
    DATABASE_PASSWORD("database.password"),
    DATABASE_PORT("database.port"),
    DATABASE_SSL("database.ssl"),

    PAGE_NUMBER("inspect.page-number"),
    PAGE_SIZE("inspect.page-size"),

    CLEANUP_INTERVAL_HOURS("cleanup.interval-hours"),
    CLEANUP_RETENTION_DAYS("cleanup.retention-days"),

    LANG_FILE("generic.lang");

    private final String path;

    ConfigKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
