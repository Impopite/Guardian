package it.impo.guardian.loader;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.database.GuardianTable;
import org.bukkit.event.Listener;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class PluginLoader {

    protected final Guardian plugin;

    private final List<CommandAPICommand> commands = new ArrayList<>();
    private final List<Listener> listeners = new ArrayList<>();

    public PluginLoader(Guardian plugin) {
        this.plugin = plugin;
    }

    public final void load(GuardianTable table) {
        try {
            setupDatabase(table);
        } catch (SQLException e) {
            plugin.getLogger().severe("Error while creating the tables: " + e.getMessage());
            plugin.getServer().getPluginManager().disablePlugin(plugin);
            return;
        }

        setupListeners();
        setupCommands();

        listeners.forEach(listener ->
                plugin.getServer().getPluginManager().registerEvents(listener, plugin));

        commands.forEach(CommandAPICommand::register);

        plugin.getLogger().info("Plugin loaded successfully.");
    }

    protected abstract void setupDatabase(GuardianTable table) throws SQLException;

    protected void setupListeners() {}

    protected void setupCommands() {}

    protected final void registerListeners(Listener... toRegister) {
        listeners.addAll(Arrays.asList(toRegister));
    }

    protected final void registerCommands(CommandAPICommand... toRegister) {
        commands.addAll(Arrays.asList(toRegister));
    }

    protected final void registerCommands(CommandAPICommand[]... arrays) {
        for (CommandAPICommand[] array : arrays) {
            commands.addAll(Arrays.asList(array));
        }
    }
}