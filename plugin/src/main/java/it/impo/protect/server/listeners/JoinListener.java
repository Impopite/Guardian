package it.impo.protect.server.listeners;

import it.impo.protect.Protect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final Protect plugin;

    public JoinListener(Protect plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {
            plugin.getUpdateChecker().notify(player);
        }
    }
}
