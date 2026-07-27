package it.impo.guardian.database.utils;

import it.impo.guardian.Guardian;
import it.impo.guardian.config.constant.ConfigKey;

public class DatabaseCredentials {

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private final boolean ssl;

    public DatabaseCredentials(Guardian plugin) {
        var config = plugin.getConfigLoader();
        this.host = config.get(ConfigKey.DATABASE_HOST, "localhost");
        this.database = config.get(ConfigKey.DATABASE_NAME, "DefaultDatabaseName");
        this.username = config.get(ConfigKey.DATABASE_USERNAME, "root");
        this.password = config.get(ConfigKey.DATABASE_PASSWORD, "");
        this.port = config.get(ConfigKey.DATABASE_PORT, 3306);
        this.ssl = config.get(ConfigKey.DATABASE_SSL, false);
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public int getPort() {
        return port;
    }

    public boolean isSsl() {
        return ssl;
    }
}
