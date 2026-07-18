package it.impo.protect.server.command;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.protect.Protect;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;
import it.impo.protect.server.command.subcommands.InspectCommand;
import it.impo.protect.server.command.subcommands.RollbackCommand;

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
                        new RollbackCommand(plugin).get()
                )
                .withPermission(Permission.PROTECT_STAFF.getPermission())
                .executes((sender, args) -> {
                    if (!sender.hasPermission(Permission.PROTECT_STAFF.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }
                    if (!(sender instanceof org.bukkit.entity.Player player)) {
                        lang.send(sender, LangKey.CONSOLE_CANT_DO);
                        return;
                    }
                    lang.send(player, LangKey.INSPECT_COMMAND_USAGE);
                });
    }
}
