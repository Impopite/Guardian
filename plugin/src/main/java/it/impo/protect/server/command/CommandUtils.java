package it.impo.protect.server.command;

import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class CommandUtils {

    private CommandUtils() {}

    public static Player findPlayer(LangLoader lang, CommandSender sender, String name) {
        Player target = Bukkit.getPlayerExact(name);
        if (target != null) return target;

        target = Bukkit.getPlayer(name);
        if (target != null) return target;

        lang.send(sender, LangKey.PLAYER_NOT_FOUND);
        return null;
    }

    public static boolean isPlayer(LangLoader lang, CommandSender sender) {
        if (sender instanceof Player) return true;
        lang.send(sender, LangKey.CONSOLE_CANT_DO);
        return false;
    }

    public static void sendUnknown(LangLoader lang, Audience audience) {
        lang.send((CommandSender) audience, LangKey.UNKNOWN_COMMAND);
    }
}
