package it.impo.guardian.server.command.subcommands.rollback;

import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.ArgumentSuggestions;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.utils.LogUtils;
import it.impo.guardian.api.utils.Permission;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.LangKey;
import org.bukkit.entity.Player;

import java.time.Duration;

public class BlocksRollbackCommand {

    private final Guardian plugin;

    public BlocksRollbackCommand(Guardian plugin) {
        this.plugin = plugin;
    }

    public CommandAPICommand get() {
        return new CommandAPICommand("blocks")
                .withArguments(new IntegerArgument("raggio", 1, 100))
                .withArguments(new StringArgument("tempo").replaceSuggestions(ArgumentSuggestions.strings("15m", "30m", "1h", "6h", "1d")))
                .executes((sender, args) -> {
                    LangLoader lang = plugin.getLangLoader();
                    if (!(sender instanceof Player player)) {
                        lang.send(sender, LangKey.CONSOLE_CANT_DO);
                        return;
                    }

                    if (!sender.hasPermission(Permission.GUARDIAN_ROLLBACK.getPermission())) {
                        lang.send(sender, LangKey.NO_PERMISSION);
                        return;
                    }

                    long seconds = LogUtils.parseTime((String) args.get("tempo"));
                    if (seconds <= 0) {
                        lang.send(player, LangKey.ROLLBACK_INVALID_TIME);
                        return;
                    }
                    plugin.getRollbackManager().rollbackBlocks(player, (int) args.get("raggio"), Duration.ofSeconds(seconds));
                });
    }
}
