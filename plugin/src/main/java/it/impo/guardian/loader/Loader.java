package it.impo.guardian.loader;

import it.impo.guardian.Guardian;
import it.impo.guardian.api.database.GuardianTable;
import it.impo.guardian.hook.FaweHook;
import it.impo.guardian.server.command.GuardianCommand;
import it.impo.guardian.server.listeners.BlockListener;
import it.impo.guardian.server.listeners.ContainerListener;
import it.impo.guardian.server.listeners.InspectListener;
import it.impo.guardian.server.listeners.ItemListener;
import it.impo.guardian.server.listeners.JoinListener;

import java.sql.SQLException;

public class Loader extends PluginLoader {

    public Loader(Guardian plugin) {
        super(plugin);
    }

    @Override
    protected void setupDatabase(GuardianTable table) throws SQLException {
        table.createTables();
    }

    @Override
    protected void setupListeners() {
        registerListeners(
                new BlockListener(plugin),
                new ContainerListener(plugin),
                new InspectListener(plugin),
                new ItemListener(plugin),
                new JoinListener(plugin)
        );

        if (plugin.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")) {
            new FaweHook(plugin).register();
            plugin.getLogger().info("FAWE hook enabled.");
        }
    }

    @Override
    protected void setupCommands() {
        registerCommands(
                new GuardianCommand(plugin).get()
        );
    }
}
