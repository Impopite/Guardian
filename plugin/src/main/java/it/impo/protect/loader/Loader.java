package it.impo.protect.loader;

import it.impo.protect.Protect;
import it.impo.protect.api.database.ProtectTable;
import it.impo.protect.server.listeners.BlockListener;
import it.impo.protect.server.listeners.ContainerListener;
import it.impo.protect.server.listeners.InspectListener;
import it.impo.protect.server.listeners.ItemListener;

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
                new BlockListener(plugin),
                new ContainerListener(plugin),
                new InspectListener(plugin),
                new ItemListener(plugin)
        );
    }

    @Override
    protected void setupCommands() {
        registerCommands(
                // new Command(plugin).get() ecc....
        );
    }
}
