package it.impo.guardian.api.data;

import org.bukkit.Location;
import org.jspecify.annotations.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A lightweight, serializable representation of a block coordinate in a world.
 *
 * <p>Unlike {@link Location}, this record depends neither on region files nor on a loaded
 * world instance, so it can be safely stored and reused in the database layer.</p>
 *
 * @param world the name of the world
 * @param x     the block x coordinate
 * @param y     the block y coordinate
 * @param z     the block z coordinate
 */
public record BasicLocation(String world, int x, int y, int z) {

    /**
     * Converts a Bukkit {@link Location} into a {@link BasicLocation} using its block coordinates.
     *
     * @param location the location to convert; its world must not be {@code null}
     * @return a new {@link BasicLocation} with the same world and block coordinates
     */
    public static BasicLocation from(Location location) {
        return new BasicLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    /**
     * Reads a {@link BasicLocation} from the current row of a {@link ResultSet}.
     *
     * <p>The result set must contain the columns {@code world}, {@code x}, {@code y} and
     * {@code z}.</p>
     *
     * @param rs the result set positioned on the desired row
     * @return a new {@link BasicLocation} built from the row values
     * @throws SQLException on any database access error
     */
    public static BasicLocation from(ResultSet rs) throws SQLException {
        return new BasicLocation(rs.getString("world"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
    }

    @Override
    public @NonNull String toString() {
        return x + ", " + y + ", " + z;
    }
}