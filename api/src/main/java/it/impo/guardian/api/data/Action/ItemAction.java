package it.impo.guardian.api.data.Action;

public enum ItemAction {

    PICKUP("Item picked up"),
    DROP("Item dropped");

    private final String label;

    ItemAction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
