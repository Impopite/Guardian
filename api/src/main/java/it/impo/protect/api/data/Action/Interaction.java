package it.impo.protect.api.data.Action;

public enum Interaction {

    OPEN("Opened", "Opened"),
    CLOSE("Closed", "Closed");

    private final String description;
    private final String displayName;

    Interaction(String description, String displayName) {
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
