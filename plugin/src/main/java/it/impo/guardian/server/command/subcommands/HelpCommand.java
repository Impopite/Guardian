package it.impo.guardian.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class HelpCommand {

    private final Guardian plugin;

    public HelpCommand(Guardian plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("help")
                .withPermission(Permission.GUARDIAN_STAFF.getPermission())
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    lang.send(sender, LangKey.PREFIX);
                    sender.sendMessage(helpLine("/guardian help", lang, LangKey.HELP_SHOW));
                    sender.sendMessage(helpLine("/guardian inspect", lang, LangKey.HELP_INSPECT));
                    sender.sendMessage(helpLine("/guardian rollback <blocks/containers> <radius> <time>", lang, LangKey.HELP_ROLLBACK));
                    sender.sendMessage(helpLine("/guardian lookup <player>", lang, LangKey.HELP_LOOKUP));
                    sender.sendMessage(helpLine("/guardian stats <player>", lang, LangKey.HELP_STATS));
                    sender.sendMessage(helpLine("/guardian reload", lang, LangKey.HELP_RELOAD));
                });
    }

    private Component helpLine(String command, LangLoader lang, LangKey description) {
        return Component.text(command)
                .color(NamedTextColor.AQUA)
                .hoverEvent(HoverEvent.showText(
                        Component.text(lang.getRaw(description), NamedTextColor.GRAY)
                ));
    }
}
