package it.impo.protect.api.data.Action;

public enum Interaction {

    OPEN("Opened"),
    CLOSE("Closed");

    private final String label;

    Interaction(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
