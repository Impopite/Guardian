package it.impo.protect.loader;

import it.impo.protect.Protect;
import it.impo.protect.api.database.ProtectTable;

import java.sql.SQLException;

public class Loader extends PluginLoader {

    public Loader(Protect plugin) {
        super(plugin);
    }

    @Override
    protected void setupDatabase(ProtectTable table) throws SQLException {
        table.createTables();
    }

    @Override
    protected void setupListeners() {
        registerListeners(
                // new Listener(plugin) ecc....
        );
    }

    @Override
    protected void setupCommands() {
        registerCommands(
                // new Command(plugin).get() ecc....
        );
    }
}
