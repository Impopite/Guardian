package it.impo.protect.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.protect.Protect;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;

public class ReloadCommand {

    private final Protect plugin;

    public ReloadCommand(Protect plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("reload")
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if (!sender.hasPermission(Permission.PROTECT_STAFF.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }
                    plugin.getConfigLoader().reload();
                    lang = plugin.getLangLoader();
                    lang.send(sender, LangKey.RELOAD_SUCCESS);
                });
    }
}
