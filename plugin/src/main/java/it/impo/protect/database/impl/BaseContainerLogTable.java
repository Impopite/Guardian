package it.impo.protect.database.impl;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.protect.api.data.Action.ContainerAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.ContainerType;
import it.impo.protect.api.data.logs.impl.ContainerLog;
import it.impo.protect.api.database.impl.ContainerLogTable;
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

public class BaseContainerLogTable extends ContainerLogTable {

    private final HikariDataSource dataSource;

    @Language("SQL")
    private static final String CREATE_CONTAINER_LOG_TABLE = """
        CREATE TABLE IF NOT EXISTS protect_container_log (
            id             INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
            user_uuid      VARCHAR(36)  NOT NULL,
            username       VARCHAR(16)  NOT NULL,
            world          VARCHAR(32)  NOT NULL,
            x              INT          NOT NULL,
            y              INT          NOT NULL,
            z              INT          NOT NULL,
            container_type VARCHAR(16)  NOT NULL,
            item           LONGBLOB     NOT NULL,
            amount         INT          NOT NULL,
            action         VARCHAR(8)   NOT NULL,
            staff          TINYINT(1)   NOT NULL DEFAULT 0,
            date           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
        
            INDEX idx_container_location_date (world, x, y, z, date),
            INDEX idx_container_user_date     (user_uuid, username, date),
            INDEX idx_container_date          (date)
        );
        """;

    @Language("SQL")
    private static final String ADD_CONTAINER_LOG = """
        INSERT INTO protect_container_log (user_uuid, username, world, x, y, z, container_type, item, amount, action, staff)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    @Language("SQL")
    private static final String REMOVE_CONTAINER_LOG = "DELETE FROM protect_container_log WHERE id = ?";

    @Language("SQL")
    private static final String REMOVE_OLD_CONTAINER_LOG = "DELETE FROM protect_container_log WHERE date < (NOW() - INTERVAL ? DAY)";

    @Language("SQL")
    private static final String COUNT_CONTAINER_LOG = "SELECT COUNT(*) FROM protect_container_log WHERE world = ? AND x = ? AND y = ? AND z = ?";

    @Language("SQL")
    private static final String INSPECT_CONTAINER_LOGS = """
        SELECT id, user_uuid, username, world, x, y, z, container_type, item, amount, action, staff, date
        FROM protect_container_log
        WHERE world = ? AND x = ? AND y = ? AND z = ?
        ORDER BY date DESC
        LIMIT ? OFFSET ?
        """;

    public BaseContainerLogTable(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createTable() throws SQLException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(CREATE_CONTAINER_LOG_TABLE)) {
            ps.execute();
        }
    }

    @Override
    public CompletableFuture<Boolean> addLog(ContainerLog log) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(ADD_CONTAINER_LOG)) {
                ps.setString(1, log.getUuid().toString());
                ps.setString(2, log.getPlayerName());
                ps.setString(3, log.getLocation().world());
                ps.setInt(4, log.getLocation().x());
                ps.setInt(5, log.getLocation().y());
                ps.setInt(6, log.getLocation().z());
                ps.setString(7, log.getContainerType().name());
                ps.setBytes(8, log.getItem());
                ps.setInt(9, log.getAmount());
                ps.setString(10, log.getAction().name());
                ps.setBoolean(11, log.isStaff());
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
                 PreparedStatement ps = c.prepareStatement(REMOVE_CONTAINER_LOG)) {
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
                 PreparedStatement ps = c.prepareStatement(REMOVE_OLD_CONTAINER_LOG)) {
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
                 PreparedStatement ps = c.prepareStatement(COUNT_CONTAINER_LOG)) {
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
    public CompletableFuture<List<ContainerLog>> inspectLog(BasicLocation location, int limit, int offset) {
        return supplyAsync(() -> {
            List<ContainerLog> logs = new ArrayList<>();
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(INSPECT_CONTAINER_LOGS)) {
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
                        ContainerAction action = ContainerAction.valueOf(rs.getString("action"));
                        ContainerType containerType = ContainerType.valueOf(rs.getString("container_type"));

                        ContainerLog log = new ContainerLog(
                                uuid,
                                rs.getString("username"),
                                rs.getBytes("item"),
                                date,
                                rs.getInt("amount"),
                                rs.getBoolean("staff"),
                                action,
                                logLocation,
                                containerType
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