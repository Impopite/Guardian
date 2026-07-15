package it.impo.protect.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.protect.Protect;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;

public class InspectCommand {

    private final Protect plugin;

    public InspectCommand(Protect plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("create")
                .executesPlayer((player, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if(plugin.getProtectManager().toggleInspect(player)){
                        lang.send(player, LangKey.INSPECT_ENABLED);
                        return;
                    }

                    lang.send(player, LangKey.INSPECT_DISABLED);
                });
    }
}
