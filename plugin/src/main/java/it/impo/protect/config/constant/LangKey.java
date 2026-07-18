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

    PREFIX("prefix");

    private final String path;

    LangKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
