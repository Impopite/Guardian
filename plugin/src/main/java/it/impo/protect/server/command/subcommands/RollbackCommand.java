package it.impo.protect.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.protect.Protect;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;
import it.impo.protect.server.command.subcommands.rollback.BlocksRollbackCommand;
import it.impo.protect.server.command.subcommands.rollback.ContainersRollbackCommand;

public class RollbackCommand {

    private final Protect plugin;

    public RollbackCommand(Protect plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        LangLoader lang = plugin.getLangLoader();
        return new CommandAPICommand("rollback")
                .withSubcommands(
                        new BlocksRollbackCommand(plugin).get(),
                        new ContainersRollbackCommand(plugin).get()
                )
                .executes((sender, args) -> {
                    if (!sender.hasPermission(Permission.PROTECT_ROLLBACK.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }
                    lang.send(sender, LangKey.ROLLBACK_COMMAND_USAGE);
                });
    }
}
