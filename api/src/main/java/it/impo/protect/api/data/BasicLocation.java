package it.impo.protect.api.data;

public record BasicLocation(String world, int x, int y, int z) {

    @Override
    public String toString() {
        return x + ", " + y + ", " + z;
    }
}
