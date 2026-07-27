package it.impo.guardian.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;
import org.bukkit.entity.Player;

public class InspectCommand {

    private final Guardian plugin;

    public InspectCommand(Guardian plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("inspect")
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if (!(sender instanceof Player player)) {
                        lang.send(sender, LangKey.CONSOLE_CANT_DO);
                        return;
                    }

                    if (!sender.hasPermission(Permission.GUARDIAN_INSPECT.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }

                    if (plugin.getGuardianManager().toggleInspect(player)) {
                        lang.send(player, LangKey.INSPECT_ENABLED);
                        return;
                    }
                    lang.send(player, LangKey.INSPECT_DISABLED);
                });
    }
}
