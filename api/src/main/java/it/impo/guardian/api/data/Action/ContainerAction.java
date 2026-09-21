package it.impo.guardian.api.data.action;

/**
 * Represents the kind of container action that was logged.
 */
public enum ContainerAction {

    /** An item was added to the container. */
    ADD("Item added from container"),
    /** An item was removed from the container. */
    REMOVE("Item removed from container");

    private final String label;

    ContainerAction(String label) {
        this.label = label;
    }

    /**
     * Returns a human readable label describing the action.
     *
     * @return the label of this action
     */
    public String getLabel() {
        return label;
    }
}