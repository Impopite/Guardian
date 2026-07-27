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
                    sender.sendMessage(lang.prefix());
                    sender.sendMessage(helpLine("/guardian help", lang, LangKey.HELP_SHOW, "Show this help message"));
                    sender.sendMessage(helpLine("/guardian inspect", lang, LangKey.HELP_INSPECT, "Toggle inspect mode"));
                    sender.sendMessage(helpLine("/guardian rollback <blocks|containers> <radius> <time>", lang, LangKey.HELP_ROLLBACK, "Rollback changes in a radius"));
                    sender.sendMessage(helpLine("/guardian lookup <player>", lang, LangKey.HELP_LOOKUP, "View a player's logs"));
                    sender.sendMessage(helpLine("/guardian stats <player>", lang, LangKey.HELP_STATS, "View player statistics"));
                    sender.sendMessage(helpLine("/guardian reload", lang, LangKey.HELP_RELOAD, "Reload the configuration"));
                });
    }

    private Component helpLine(String command, LangLoader lang, LangKey key, String fallback) {
        String desc = lang.getRaw(key);
        if (desc.isEmpty()) desc = fallback;
        Component prefix = Component.text("» ").color(NamedTextColor.DARK_GRAY);
        Component cmd = Component.text(command).color(NamedTextColor.GRAY);
        Component hoverText = Component.text(desc).color(NamedTextColor.GRAY);
        return Component.empty().append(prefix).append(cmd)
                .hoverEvent(HoverEvent.showText(hoverText));
    }
}
