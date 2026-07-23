package it.impo.protect.database.impl;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.protect.api.data.Action.Interaction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.impl.InteractLog;
import it.impo.protect.api.database.impl.InteractLogTable;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class BaseInteractLogTable extends InteractLogTable {

    private final HikariDataSource dataSource;

    @Language("SQL")
    private static final String CREATE_INTERACT_LOG_TABLE = """
        CREATE TABLE IF NOT EXISTS protect_interact_log (
            id         INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
            user_uuid  VARCHAR(36)  NOT NULL,
            username   VARCHAR(16)  NOT NULL,
            world      VARCHAR(32)  NOT NULL,
            x          INT          NOT NULL,
            y          INT          NOT NULL,
            z          INT          NOT NULL,
            block_type VARCHAR(64)  NOT NULL,
            action     VARCHAR(8)   NOT NULL,
            staff      TINYINT(1)   NOT NULL DEFAULT 0,
            date       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
        
            INDEX idx_interact_location_date (world, x, y, z, date),
            INDEX idx_interact_user_date     (user_uuid, username, date),
            INDEX idx_interact_date          (date)
        );
        """;

    @Language("SQL")
    private static final String ADD_INTERACT_LOG = """
        INSERT INTO protect_interact_log (user_uuid, username, world, x, y, z, block_type, action, staff)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    @Language("SQL")
    private static final String REMOVE_INTERACT_LOG = "DELETE FROM protect_interact_log WHERE id = ?";

    @Language("SQL")
    private static final String REMOVE_OLD_INTERACT_LOG = "DELETE FROM protect_interact_log WHERE date < (NOW() - INTERVAL ? DAY)";

    @Language("SQL")
    private static final String COUNT_INTERACT_LOG = "SELECT COUNT(*) FROM protect_interact_log WHERE world = ? AND x = ? AND y = ? AND z = ?";

    @Language("SQL")
    private static final String INSPECT_INTERACT_LOGS = """
        SELECT id, user_uuid, username, world, x, y, z, block_type, action, staff, date
        FROM protect_interact_log
        WHERE world = ? AND x = ? AND y = ? AND z = ?
        ORDER BY date DESC
        LIMIT ? OFFSET ?
        """;

    @Language("SQL")
    private static final String COUNT_INTERACT_LOG_BY_PLAYER = "SELECT COUNT(*) FROM protect_interact_log WHERE username = ?";

    @Language("SQL")
    private static final String SEARCH_INTERACT_LOG_BY_PLAYER = """
        SELECT id, user_uuid, username, world, x, y, z, block_type, action, staff, date
        FROM protect_interact_log
        WHERE username = ?
        ORDER BY date DESC
        LIMIT ? OFFSET ?
        """;

    public BaseInteractLogTable(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createTable() throws SQLException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(CREATE_INTERACT_LOG_TABLE)) {
            ps.execute();
        }
    }

    @Override
    public CompletableFuture<Boolean> addLog(InteractLog log) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(ADD_INTERACT_LOG)) {
                ps.setObject(1, log.getUuid());
                ps.setString(2, log.getPlayerName());
                ps.setString(3, log.getLocation().world());
                ps.setInt(4, log.getLocation().x());
                ps.setInt(5, log.getLocation().y());
                ps.setInt(6, log.getLocation().z());
                ps.setString(7, log.getBlockType());
                ps.setString(8, log.getAction().toString());
                ps.setBoolean(9, log.isStaff());
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> removeLog(int id) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(REMOVE_INTERACT_LOG)) {
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> removeOldLog(int days) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(REMOVE_OLD_INTERACT_LOG)) {
                ps.setInt(1, days);
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Integer> countLog(BasicLocation location) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(COUNT_INTERACT_LOG)) {
                ps.setString(1, location.world());
                ps.setInt(2, location.x());
                ps.setInt(3, location.y());
                ps.setInt(4, location.z());
                try (var rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<List<InteractLog>> inspectLog(BasicLocation location, int limit, int offset) {
        return supplyAsync(() -> {
            List<InteractLog> logs = new ArrayList<>();
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(INSPECT_INTERACT_LOGS)) {
                ps.setString(1, location.world());
                ps.setInt(2, location.x());
                ps.setInt(3, location.y());
                ps.setInt(4, location.z());
                ps.setInt(5, limit);
                ps.setInt(6, offset);
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("user_uuid"));

                        BasicLocation logLocation = new BasicLocation(
                                rs.getString("world"),
                                rs.getInt("x"),
                                rs.getInt("y"),
                                rs.getInt("z")
                        );

                        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                        Interaction action = Interaction.valueOf(rs.getString("action"));

                        InteractLog log = new InteractLog(
                                uuid,
                                rs.getString("username"),
                                logLocation,
                                date,
                                rs.getBoolean("staff"),
                                rs.getString("block_type"),
                                action
                        );
                        log.setId(rs.getInt("id"));
                        logs.add(log);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return logs;
        });
    }

    @Override
    public CompletableFuture<Integer> countLogsByPlayer(String playerName) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(COUNT_INTERACT_LOG_BY_PLAYER)) {
                ps.setString(1, playerName);
                try (var rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }

    @Override
    public CompletableFuture<List<InteractLog>> searchByPlayer(String playerName, int limit, int offset) {
        return supplyAsync(() -> {
            List<InteractLog> logs = new ArrayList<>();
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(SEARCH_INTERACT_LOG_BY_PLAYER)) {
                ps.setString(1, playerName);
                ps.setInt(2, limit);
                ps.setInt(3, offset);
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        UUID uuid = UUID.fromString(rs.getString("user_uuid"));

                        BasicLocation logLocation = new BasicLocation(
                                rs.getString("world"),
                                rs.getInt("x"),
                                rs.getInt("y"),
                                rs.getInt("z")
                        );

                        LocalDateTime date = rs.getTimestamp("date").toLocalDateTime();
                        Interaction action = Interaction.valueOf(rs.getString("action"));

                        InteractLog log = new InteractLog(
                                uuid,
                                rs.getString("username"),
                                logLocation,
                                date,
                                rs.getBoolean("staff"),
                                rs.getString("block_type"),
                                action
                        );
                        log.setId(rs.getInt("id"));
                        logs.add(log);
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return logs;
        });
    }
}