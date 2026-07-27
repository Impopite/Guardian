package it.impo.guardian.api.data.Action;

public enum Action {

    PLACE("Block placed"),
    BREAK("Block broken");

    private final String label;

    Action(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
