package it.impo.guardian.database.utils;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import it.impo.guardian.Guardian;

public class HikariCP {

    private final HikariDataSource dataSource;

    public HikariCP(Guardian plugin, DatabaseCredentials credentials) {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(String.format("jdbc:mysql://%s:%s/%s",
                credentials.getHost(), credentials.getPort(), credentials.getDatabase()));
        config.setUsername(credentials.getUsername());
        config.setPassword(credentials.getPassword());
        config.setPoolName(plugin.getProjectName() + "-pool");
        config.setMaximumPoolSize(10);
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("allowMultiQueries", true);
        config.addDataSourceProperty("sslMode", credentials.isSsl() ? "REQUIRED" : "DISABLED");

        this.dataSource = new HikariDataSource(config);
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public HikariDataSource getDataSource() {
        return dataSource;
    }
}
