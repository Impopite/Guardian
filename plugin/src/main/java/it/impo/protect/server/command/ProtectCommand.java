package it.impo.protect.server.command;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.protect.Protect;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;
import it.impo.protect.server.command.subcommands.InspectCommand;

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
                        new InspectCommand(plugin).get()
                )
                .withPermission(Permission.PROTECT_INSPECT.getPermission())
                .executesPlayer((player, args) -> {
                    lang.send(player, LangKey.INSPECT_COMMAND_USAGE);
                });
    }
}
