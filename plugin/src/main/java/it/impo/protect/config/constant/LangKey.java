package it.impo.protect.config.constant;

public enum LangKey {

    NO_PERMISSION("message.no-permission"),
    UNKNOWN_COMMAND("message.unknown-command"),
    CONSOLE_CANT_DO("message.console-cant-do-command"),
    PLAYER_NOT_FOUND("message.player-not-found"),

    NO_INTERACTION("inspect.no-interaction"),
    NOT_VALID("inspect.not-valid"),

    INSPECT_ENABLED("inspect.enabled"),
    INSPECT_DISABLED("inspect.disabled"),
    INSPECT_COMMAND_USAGE("command-usage.inspect"),

    ROLLBACK_COMMAND_USAGE("command-usage.rollback"),
    ROLLBACK_INVALID_TIME("rollback.invalid-time"),
    ROLLBACK_NO_BLOCKS("rollback.no-blocks"),
    ROLLBACK_NO_CONTAINERS("rollback.no-containers"),
    ROLLBACK_BLOCKS_SUCCESS("rollback.blocks-success"),
    ROLLBACK_BLOCKS_SUCCESS_SKIPPED("rollback.blocks-success-skipped"),
    ROLLBACK_CONTAINERS_SUCCESS("rollback.containers-success"),
    ROLLBACK_CONTAINERS_SUCCESS_SKIPPED("rollback.containers-success-skipped"),
    ROLLBACK_CONTAINERS_SUCCESS_PARTIAL("rollback.containers-success-partial"),
    ROLLBACK_CONTAINERS_SUCCESS_ALL("rollback.containers-success-all"),

    RELOAD_COMMAND_USAGE("command-usage.reload"),
    RELOAD_SUCCESS("reload.success"),

    LOOKUP_COMMAND_USAGE("command-usage.lookup"),
    LOOKUP_NO_LOGS("lookup.no-logs"),
    LOOKUP_HEADER("lookup.header"),

    STATS_COMMAND_USAGE("command-usage.stats"),
    STATS_NO_LOGS("stats.no-logs"),
    STATS_HEADER("stats.header"),
    STATS_BLOCKS("stats.blocks"),
    STATS_CONTAINERS("stats.containers"),
    STATS_ITEMS("stats.items"),
    STATS_INTERACTS("stats.interacts"),
    STATS_FIRST_SEEN("stats.first-seen"),
    STATS_LAST_SEEN("stats.last-seen"),

    PREFIX("prefix");

    private final String path;

    LangKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
