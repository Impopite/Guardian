package it.impo.protect.task;

import it.impo.protect.Protect;
import it.impo.protect.config.constant.ConfigKey;
import org.bukkit.scheduler.BukkitRunnable;

public class CleanupTask extends BukkitRunnable {

    private final Protect plugin;

    public CleanupTask(Protect plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int retentionDays = plugin.getConfigLoader().get(ConfigKey.CLEANUP_RETENTION_DAYS, 30);
        plugin.getProtectManager().purgeOldLogs(retentionDays);
        plugin.getLogger().info("[Cleanup] Logs older than " + retentionDays + " days deleted.");
    }
}
