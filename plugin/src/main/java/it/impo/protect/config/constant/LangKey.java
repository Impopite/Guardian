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

    PREFIX("prefix");

    private final String path;

    LangKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
