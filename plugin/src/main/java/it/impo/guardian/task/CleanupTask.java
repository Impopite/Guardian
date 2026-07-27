package it.impo.guardian.task;

import it.impo.guardian.Guardian;
import it.impo.guardian.config.constant.ConfigKey;
import org.bukkit.scheduler.BukkitRunnable;

public class CleanupTask extends BukkitRunnable {

    private final Guardian plugin;

    public CleanupTask(Guardian plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        int retentionDays = plugin.getConfigLoader().get(ConfigKey.CLEANUP_RETENTION_DAYS, 30);
        plugin.getGuardianManager().purgeOldLogs(retentionDays);
        plugin.getLogger().info("[Cleanup] Logs older than " + retentionDays + " days deleted.");
    }
}
