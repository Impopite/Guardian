package it.impo.guardian.api.data;

import org.bukkit.Location;
import org.jspecify.annotations.NonNull;

import java.sql.ResultSet;
import java.sql.SQLException;

public record BasicLocation(String world, int x, int y, int z) {

    public static BasicLocation from(Location location) {
        return new BasicLocation(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public static BasicLocation from(ResultSet rs) throws SQLException {
        return new BasicLocation(rs.getString("world"), rs.getInt("x"), rs.getInt("y"), rs.getInt("z"));
    }

    @Override
    public @NonNull String toString() {
        return x + ", " + y + ", " + z;
    }
}
