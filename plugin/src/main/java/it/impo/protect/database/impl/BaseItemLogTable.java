package it.impo.protect.database.impl;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.protect.api.data.Action.ItemAction;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.impl.ItemLog;
import it.impo.protect.api.database.impl.ItemLogTable;
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

public class BaseItemLogTable extends ItemLogTable {

    private final HikariDataSource dataSource;

    @Language("SQL")
    private static final String CREATE_ITEM_LOG_TABLE = """
        CREATE TABLE IF NOT EXISTS protect_item_log (
            id        INT          NOT NULL AUTO_INCREMENT PRIMARY KEY,
            user_uuid VARCHAR(36)  NOT NULL,
            username  VARCHAR(16)  NOT NULL,
            world     VARCHAR(32)  NOT NULL,
            x         INT          NOT NULL,
            y         INT          NOT NULL,
            z         INT          NOT NULL,
            item      LONGBLOB     NOT NULL,
            amount    INT          NOT NULL,
            action    VARCHAR(8)   NOT NULL,
            staff     TINYINT(1)   NOT NULL DEFAULT 0,
            date      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
        
            INDEX idx_item_location_date (world, x, y, z, date),
            INDEX idx_item_user_date     (user_uuid, username, date),
            INDEX idx_item_date          (date)
        );
        """;

    @Language("SQL")
    private static final String ADD_ITEM_LOG = """
        INSERT INTO protect_item_log (user_uuid, username, world, x, y, z, item, amount, action, staff)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    @Language("SQL")
    private static final String REMOVE_ITEM_LOG = "DELETE FROM protect_item_log WHERE id = ?";

    @Language("SQL")
    private static final String REMOVE_OLD_ITEM_LOG = "DELETE FROM protect_item_log WHERE date < (NOW() - INTERVAL ? DAY)";

    @Language("SQL")
    private static final String COUNT_ITEM_LOG = "SELECT COUNT(*) FROM protect_item_log WHERE world = ? AND x = ? AND y = ? AND z = ?";

    @Language("SQL")
    private static final String INSPECT_ITEM_LOGS = """
        SELECT id, user_uuid, username, world, x, y, z, item, amount, action, staff, date
        FROM protect_item_log
        WHERE world = ? AND x = ? AND y = ? AND z = ?
        ORDER BY date DESC
        LIMIT ? OFFSET ?
        """;

    public BaseItemLogTable(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createTable() throws SQLException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(CREATE_ITEM_LOG_TABLE)) {
            ps.execute();
        }
    }

    @Override
    public CompletableFuture<Boolean> addLog(ItemLog log) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(ADD_ITEM_LOG)) {
                ps.setObject(1, log.getUuid());
                ps.setString(2, log.getPlayerName());
                ps.setString(3, log.getLocation().world());
                ps.setInt(4, log.getLocation().x());
                ps.setInt(5, log.getLocation().y());
                ps.setInt(6, log.getLocation().z());
                ps.setBytes(7, log.getItem());
                ps.setInt(8, log.getAmount());
                ps.setString(9, log.getAction().toString());
                ps.setBoolean(10, log.isStaff());
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
                 PreparedStatement ps = c.prepareStatement(REMOVE_ITEM_LOG)) {
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
                 PreparedStatement ps = c.prepareStatement(REMOVE_OLD_ITEM_LOG)) {
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
                 PreparedStatement ps = c.prepareStatement(COUNT_ITEM_LOG)) {
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
    public CompletableFuture<List<ItemLog>> inspectLog(BasicLocation location, int limit, int offset) {
        return supplyAsync(() -> {
            List<ItemLog> logs = new ArrayList<>();
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(INSPECT_ITEM_LOGS)) {
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
                        ItemAction action = ItemAction.valueOf(rs.getString("action"));

                        ItemLog log = new ItemLog(
                                uuid,
                                rs.getString("username"),
                                date,
                                rs.getBoolean("staff"),
                                rs.getBytes("item"),
                                rs.getInt("amount"),
                                action,
                                logLocation
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