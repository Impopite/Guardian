package it.impo.protect.server.command;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.protect.Protect;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;
import it.impo.protect.server.command.subcommands.InspectCommand;
import it.impo.protect.server.command.subcommands.LookupCommand;
import it.impo.protect.server.command.subcommands.ReloadCommand;
import it.impo.protect.server.command.subcommands.RollbackCommand;
import it.impo.protect.server.command.subcommands.StatsCommand;
import org.bukkit.entity.Player;

public class ProtectCommand {

    private final Protect plugin;

    public ProtectCommand(Protect plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand[] get() {
        return new CommandAPICommand[]{
                create()
        };
    }

    protected CommandAPICommand create() {
        LangLoader lang = plugin.getLangLoader();
        return new CommandAPICommand("protect")
                .withSubcommands(
                        new InspectCommand(plugin).get(),
                        new RollbackCommand(plugin).get(),
                        new ReloadCommand(plugin).get(),
                        new LookupCommand(plugin).get(),
                        new StatsCommand(plugin).get()
                )
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        lang.send(sender, LangKey.CONSOLE_CANT_DO);
                        return;
                    }

                    if (!sender.hasPermission(Permission.PROTECT_STAFF.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }

                    lang.send(player, LangKey.INSPECT_COMMAND_USAGE);
                });
    }
}
