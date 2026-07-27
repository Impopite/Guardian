package it.impo.guardian.server.command.subcommands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.StringArgument;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class LookupCommand {

    private final Guardian plugin;

    public LookupCommand(Guardian plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("lookup")
                .withArguments(new StringArgument("player"))
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if (!sender.hasPermission(Permission.GUARDIAN_INSPECT.getPermission())) {
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
                    plugin.getGuardianManager().showPlayerLogs(player, target.getName(), 1);
                });
    }
}
