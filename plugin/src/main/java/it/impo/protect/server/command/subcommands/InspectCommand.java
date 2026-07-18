package it.impo.protect.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.protect.Protect;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;
import org.bukkit.entity.Player;

public class InspectCommand {

    private final Protect plugin;

    public InspectCommand(Protect plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        LangLoader lang = plugin.getLangLoader();
        return new CommandAPICommand("inspect")
                .executes((sender, args) -> {
                    if (!(sender instanceof Player player)) {
                        lang.send(sender, LangKey.CONSOLE_CANT_DO);
                        return;
                    }

                    if (!sender.hasPermission(Permission.PROTECT_INSPECT.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }

                    if (plugin.getProtectManager().toggleInspect(player)) {
                        lang.send(player, LangKey.INSPECT_ENABLED);
                        return;
                    }
                    lang.send(player, LangKey.INSPECT_DISABLED);
                });
    }
}
