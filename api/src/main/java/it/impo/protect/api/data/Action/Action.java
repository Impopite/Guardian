package it.impo.protect.api.data.Action;

public enum Action {

    PLACE("Block placed", "Block placed"),
    BREAK("Block breaked", "Block breaked");

    private final String description;
    private final String displayName;

    Action(String description, String displayName) {
        this.description = description;
        this.displayName = displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getDisplayName() {
        return displayName;
    }
}
