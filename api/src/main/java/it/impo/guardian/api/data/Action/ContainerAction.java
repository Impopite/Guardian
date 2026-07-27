package it.impo.guardian.api.data.action;

public enum ContainerAction {

    ADD("Item added from container"),
    REMOVE("Item removed from container");

    private final String label;

    ContainerAction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
