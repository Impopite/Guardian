package it.impo.guardian.api.data.action;

/**
 * Represents the kind of item action that was logged.
 */
public enum ItemAction {

    /** The item was picked up by the player. */
    PICKUP("Item picked up"),
    /** The item was dropped by the player. */
    DROP("Item dropped");

    private final String label;

    ItemAction(String label) {
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