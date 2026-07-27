package it.impo.guardian.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;

public class ReloadCommand {

    private final Guardian plugin;

    public ReloadCommand(Guardian plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("reload")
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if (!sender.hasPermission(Permission.GUARDIAN_STAFF.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }
                    plugin.getConfigLoader().reload();
                    lang = plugin.getLangLoader();
                    lang.send(sender, LangKey.RELOAD_SUCCESS);
                });
    }
}
