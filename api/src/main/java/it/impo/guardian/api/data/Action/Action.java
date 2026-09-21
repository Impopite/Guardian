package it.impo.guardian.api.data.action;

/**
 * Represents the kind of block action that was logged.
 */
public enum Action {

    /** The block was placed in the world. */
    PLACE("Block placed"),
    /** The block was broken/removed. */
    BREAK("Block broken");

    private final String label;

    Action(String label) {
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