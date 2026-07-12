package it.impo.protect.database.impl;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.protect.api.database.impl.InteractLogTable;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
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
    public CompletableFuture<Boolean> addLog(UUID userUuid, String username, String world, int x, int y, int z, String blockType, String action, boolean staff) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(ADD_INTERACT_LOG)) {
                ps.setString(1, userUuid.toString());
                ps.setString(2, username);
                ps.setString(3, world);
                ps.setInt(4, x);
                ps.setInt(5, y);
                ps.setInt(6, z);
                ps.setString(7, blockType);
                ps.setString(8, action);
                ps.setBoolean(9, staff);
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
    public CompletableFuture<Integer> countLog(String world, int x, int y, int z) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(COUNT_INTERACT_LOG)) {
                ps.setString(1, world);
                ps.setInt(2, x);
                ps.setInt(3, y);
                ps.setInt(4, z);
                try (var rs = ps.executeQuery()) {
                    return rs.next() ? rs.getInt(1) : 0;
                }
            } catch (SQLException e) {
                e.printStackTrace();
                return 0;
            }
        });
    }
}
