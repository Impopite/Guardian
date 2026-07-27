package it.impo.guardian.server.command;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;
import it.impo.guardian.server.command.subcommands.HelpCommand;
import it.impo.guardian.server.command.subcommands.InspectCommand;
import it.impo.guardian.server.command.subcommands.LookupCommand;
import it.impo.guardian.server.command.subcommands.ReloadCommand;
import it.impo.guardian.server.command.subcommands.RollbackCommand;
import it.impo.guardian.server.command.subcommands.StatsCommand;

public class GuardianCommand {

    private final Guardian plugin;

    public GuardianCommand(Guardian plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand[] get() {
        return new CommandAPICommand[]{
                create()
        };
    }

    protected CommandAPICommand create() {
        return new CommandAPICommand("guardian")
                .withSubcommands(
                        new HelpCommand(plugin).get(),
                        new InspectCommand(plugin).get(),
                        new RollbackCommand(plugin).get(),
                        new ReloadCommand(plugin).get(),
                        new LookupCommand(plugin).get(),
                        new StatsCommand(plugin).get()
                )
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if (!sender.hasPermission(Permission.GUARDIAN_STAFF.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }
                    lang.send(sender, LangKey.USE_HELP);
                });
    }
}
