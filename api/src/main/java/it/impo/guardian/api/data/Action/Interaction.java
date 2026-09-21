package it.impo.guardian.api.data.action;

/**
 * Represents the kind of block interaction that was logged.
 */
public enum Interaction {

    /** The block was opened, e.g. a door or a trapdoor. */
    OPEN("Opened"),
    /** The block was closed. */
    CLOSE("Closed");

    private final String label;

    Interaction(String label) {
        this.label = label;
    }

    /**
     * Returns a human readable label describing the interaction.
     *
     * @return the label of this interaction
     */
    public String getLabel() {
        return label;
    }
}