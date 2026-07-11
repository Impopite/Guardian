package it.impo.defaultProject.loader;

import it.impo.defaultProject.DefaultProject;
import it.impo.defaultProject.api.database.DefaultTable;

import java.sql.SQLException;

public class Loader extends PluginLoader {

    public Loader(DefaultProject plugin) {
        super(plugin);
    }

    @Override
    protected void setupDatabase(DefaultTable table) throws SQLException {
        table.createTable();
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
