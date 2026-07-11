package it.impo.protect.api.data.Action;

public enum ItemAction {

    PICKUP("Item picked up", "Item picked up"),
    DROP("Item dropped", "Item dropped");

    private final String description;
    private final String displayName;

    ItemAction(String description, String displayName) {
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
