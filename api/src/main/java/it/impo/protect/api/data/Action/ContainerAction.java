package it.impo.protect.api.data.Action;

public enum ContainerAction {

    ADD("Oggetto aggiunto al contenitore", "Oggetto inserito"),
    REMOVE("Oggetto rimosso dal contenitore", "Oggetto rimosso");

    private final String description;
    private final String displayName;

    ContainerAction(String description, String displayName) {
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