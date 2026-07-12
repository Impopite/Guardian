package it.impo.protect.database.impl;

import com.zaxxer.hikari.HikariDataSource;
import it.impo.protect.api.data.BasicLocation;
import it.impo.protect.api.data.logs.impl.BlockLog;
import it.impo.protect.api.database.impl.BlockLogTable;
import org.intellij.lang.annotations.Language;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

import static java.util.concurrent.CompletableFuture.supplyAsync;

public class BaseBlockLogTable extends BlockLogTable {

    private final HikariDataSource dataSource;

    @Language("SQL")
    private static final String CREATE_BLOCK_LOG_TABLE = """
        CREATE TABLE IF NOT EXISTS protect_block_log (
            id         INT              NOT NULL AUTO_INCREMENT PRIMARY KEY,
            user_uuid  VARCHAR(36)      NOT NULL,
            username   VARCHAR(16)      NOT NULL,
            world      VARCHAR(32)      NOT NULL,
            x          INT              NOT NULL,
            y          INT              NOT NULL,
            z          INT              NOT NULL,
            block_type VARCHAR(64)      NOT NULL,
            block_data VARCHAR(512)     NULL,
            action     VARCHAR(8)       NOT NULL,
            staff      TINYINT(1)       NOT NULL DEFAULT 0,
            date       DATETIME         NOT NULL DEFAULT CURRENT_TIMESTAMP,
        
            INDEX idx_block_location_date (world, x, y, z, date),
            INDEX idx_block_user_date     (user_uuid, username, date),
            INDEX idx_block_date          (date)
        );
        """;

    @Language("SQL")
    private static final String ADD_BLOCK_LOG = """
        INSERT INTO protect_block_log (user_uuid, username, world, x, y, z, block_type, block_data, action, staff)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    @Language("SQL")
    private static final String REMOVE_BLOCK_LOG = "DELETE FROM protect_block_log WHERE id = ?";

    @Language("SQL")
    private static final String REMOVE_OLD_BLOCK_LOG = "DELETE FROM protect_block_log WHERE date < (NOW() - INTERVAL ? DAY)";

    @Language("SQL")
    private static final String COUNT_BLOCK_LOG = "SELECT COUNT(*) FROM protect_block_log WHERE world = ? AND x = ? AND y = ? AND z = ?";

    public BaseBlockLogTable(HikariDataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void createTable() throws SQLException {
        try (Connection c = dataSource.getConnection();
             PreparedStatement ps = c.prepareStatement(CREATE_BLOCK_LOG_TABLE)) {
            ps.execute();
        }
    }

    @Override
    public CompletableFuture<Boolean> addLog(BlockLog log) {
        return supplyAsync(() -> {
            try (Connection c = dataSource.getConnection();
                 PreparedStatement ps = c.prepareStatement(ADD_BLOCK_LOG)) {
                ps.setString(1, log.getPlayer().getUniqueId().toString());
                ps.setString(2, log.getPlayer().getName());
                ps.setString(3, log.getLocation().world());
                ps.setInt(4, log.getLocation().x());
                ps.setInt(5, log.getLocation().y());
                ps.setInt(6, log.getLocation().z());
                ps.setString(7, log.getBlockType());
                ps.setString(8, log.getBlockData());
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
                 PreparedStatement ps = c.prepareStatement(REMOVE_BLOCK_LOG)) {
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
                 PreparedStatement ps = c.prepareStatement(REMOVE_OLD_BLOCK_LOG)) {
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
                 PreparedStatement ps = c.prepareStatement(COUNT_BLOCK_LOG)) {
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


}
