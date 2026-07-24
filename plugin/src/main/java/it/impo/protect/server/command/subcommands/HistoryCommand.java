package it.impo.protect.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import it.impo.protect.Protect;
import it.impo.protect.api.utils.Permission;
import it.impo.protect.config.LangLoader;
import it.impo.protect.config.constant.LangKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class HistoryCommand {

    private final Protect plugin;

    public HistoryCommand(Protect plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        LangLoader lang = plugin.getLangLoader();
        return new CommandAPICommand("history")
                .withArguments(new StringArgument("player"))
                .executes((sender, args) -> {
                    if (!sender.hasPermission(Permission.PROTECT_INSPECT.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }
                    if (!(sender instanceof Player player)) {
                        lang.send(sender, LangKey.CONSOLE_CANT_DO);
                        return;
                    }
                    String targetName = (String) args.get("player");
                    Player target = Bukkit.getPlayerExact(targetName);
                    if (target == null) {
                        lang.send(sender, LangKey.PLAYER_NOT_FOUND);
                        return;
                    }
                    plugin.getProtectManager().showPlayerHistory(player, target.getName(), 1);
                });
    }
}
