package it.impo.guardian.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;
import it.impo.guardian.server.command.subcommands.rollback.BlocksRollbackCommand;
import it.impo.guardian.server.command.subcommands.rollback.ContainersRollbackCommand;

public class RollbackCommand {

    private final Guardian plugin;

    public RollbackCommand(Guardian plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("rollback")
                .withSubcommands(
                        new BlocksRollbackCommand(plugin).get(),
                        new ContainersRollbackCommand(plugin).get()
                )
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if (!sender.hasPermission(Permission.GUARDIAN_ROLLBACK.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }
                    lang.send(sender, LangKey.ROLLBACK_COMMAND_USAGE);
                });
    }
}
