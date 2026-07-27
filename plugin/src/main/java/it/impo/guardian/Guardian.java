package it.impo.guardian;

import it.impo.guardian.api.GuardianApi;
import it.impo.guardian.api.database.GuardianTable;
import it.impo.guardian.api.manager.GuardianManager;
import it.impo.guardian.api.manager.RollbackManager;
import it.impo.guardian.config.ConfigLoader;
import it.impo.guardian.config.LangLoader;
import it.impo.guardian.config.constant.ConfigKey;
import it.impo.guardian.database.BaseGuardianTable;
import it.impo.guardian.database.utils.DatabaseCredentials;
import it.impo.guardian.database.utils.HikariCP;
import it.impo.guardian.loader.Loader;
import it.impo.guardian.manager.BaseGuardianManager;
import it.impo.guardian.manager.BaseRollbackManager;
import it.impo.guardian.task.CleanupTask;
import it.impo.guardian.update.UpdateChecker;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class Guardian extends JavaPlugin implements GuardianApi {

    private final String projectName = getDescription().getName();

    private ConfigLoader configLoader;

    private HikariCP hikariCP;
    private GuardianTable guardianTable;

    private GuardianManager guardianManager;
    private RollbackManager rollbackManager;
    private CleanupTask cleanupTask;
    private UpdateChecker updateChecker;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        getLogger().info("");
        getLogger().info(CYAN + "====================================" + RESET);
        getLogger().info(CYAN + "   " + projectName + RESET);
        getLogger().info(GRAY + "   Developed by " + WHITE + "zImpoo" + RESET);
        getLogger().info(CYAN + "====================================" + RESET);

        BukkitAudiences adventure = BukkitAudiences.create(this);
        this.configLoader = new ConfigLoader(this, adventure).load();

        DatabaseCredentials databaseCredentials = new DatabaseCredentials(this);
        this.hikariCP = new HikariCP(this, databaseCredentials);
        this.guardianTable = new BaseGuardianTable(hikariCP.getDataSource());

        this.guardianManager = new BaseGuardianManager(this);
        this.rollbackManager = new BaseRollbackManager(this);
        Loader loader = new Loader(this);
        loader.load(guardianTable);

        int cleanupInterval = configLoader.get(ConfigKey.CLEANUP_INTERVAL_HOURS, 24);
        this.cleanupTask = new CleanupTask(this);
        cleanupTask.runTaskTimer(this, cleanupInterval * 20L * 60L * 60L, cleanupInterval * 20L * 60L * 60L);

        this.updateChecker = new UpdateChecker(this);
        updateChecker.checkAsync();

        long took = System.currentTimeMillis() - start;

        getLogger().info(GREEN + "Commands loaded" + RESET);
        getLogger().info(GREEN + "Databases loaded" + RESET);
        getLogger().info(GREEN + "Config loaded" + RESET);
        getLogger().info("");
        getLogger().info(GREEN + "enabled successfully in " + took + "ms" + RESET);
        getLogger().info(CYAN + "====================================" + RESET);
    }

    @Override
    public void onDisable() {
        if (cleanupTask != null) {
            cleanupTask.cancel();
        }
        hikariCP.close();

        getLogger().info("");
        getLogger().info(RED + "====================================" + RESET);
        getLogger().info(RED + "   " + projectName + RESET);
        getLogger().info(GRAY + "   Developed by " + WHITE + "zImpoo" + RESET);
        getLogger().info(RED + "====================================" + RESET);
        getLogger().info(RED + "Plugin disabled safely." + RESET);
        getLogger().info(RED + "====================================" + RESET);
    }

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[37m";
    private static final String WHITE = "\u001B[97m";

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public LangLoader getLangLoader() {
        return configLoader.getLangLoader();
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public GuardianTable getGuardianTable() {
        return guardianTable;
    }

    @Override
    public GuardianManager getGuardianManager() {
        return guardianManager;
    }

    @Override
    public RollbackManager getRollbackManager() {
        return rollbackManager;
    }

    public UpdateChecker getUpdateChecker() {
        return updateChecker;
    }
}
