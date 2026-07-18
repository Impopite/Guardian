package it.impo.protect;

import it.impo.protect.api.ProtectApi;
import it.impo.protect.api.database.ProtectTable;
import it.impo.protect.api.manager.ProtectManager;
import it.impo.protect.api.manager.RollbackManager;
import it.impo.protect.config.ConfigLoader;
import it.impo.protect.config.LangLoader;
import it.impo.protect.database.BaseProtectTable;
import it.impo.protect.database.utils.DatabaseCredentials;
import it.impo.protect.database.utils.HikariCP;
import it.impo.protect.loader.Loader;
import it.impo.protect.manager.BaseProtectManager;
import it.impo.protect.manager.BaseRollbackManager;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.plugin.java.JavaPlugin;

public final class Protect extends JavaPlugin implements ProtectApi {

    private final String projectName = getDescription().getName();

    private ConfigLoader configLoader;
    private LangLoader langLoader;

    private HikariCP hikariCP;
    private ProtectTable protectTable;

    private ProtectManager protectManager;
    private RollbackManager rollbackManager;

    @Override
    public void onEnable() {
        long start = System.currentTimeMillis();

        getLogger().info("");
        getLogger().info(CYAN + "====================================" + RESET);
        getLogger().info(CYAN + projectName + RESET);
        getLogger().info(GRAY + "   Developed by " + WHITE + "zImpoo" + RESET);
        getLogger().info(CYAN + "====================================" + RESET);

        BukkitAudiences adventure = BukkitAudiences.create(this);
        this.configLoader = new ConfigLoader(this, adventure).load();
        this.langLoader = configLoader.getLangLoader();

        DatabaseCredentials databaseCredentials = new DatabaseCredentials(this);
        this.hikariCP = new HikariCP(this, databaseCredentials);
        this.protectTable = new BaseProtectTable(hikariCP.getDataSource());

        this.protectManager = new BaseProtectManager(this);
        this.rollbackManager = new BaseRollbackManager(this);
        Loader loader = new Loader(this);
        loader.load(protectTable);

        long took = System.currentTimeMillis() - start;

        getLogger().info(GREEN + "Commands loaded" + RESET);
        getLogger().info(GREEN + "Databases loaded" + RESET);
        getLogger().info(GREEN + "Config loaded" + RESET);
        getLogger().info("");
        getLogger().info(GREEN +  "enabled successfully in " + took + "ms" + RESET);
        getLogger().info(CYAN + "====================================" + RESET);

    }

    @Override
    public void onDisable() {
        hikariCP.close();

        getLogger().info("");
        getLogger().info(RED + "====================================" + RESET);
        getLogger().info(RED + projectName + RESET);
        getLogger().info(GRAY + "   Developed by " + WHITE + "zImpoo" + RESET);
        getLogger().info(RED + "====================================" + RESET);
        getLogger().info(RED + "Plugin disabled safely." + RESET);
        getLogger().info(RED + "====================================" + RESET);
    }

    public ConfigLoader getConfigLoader() {
        return configLoader;
    }

    public LangLoader getLangLoader() {
        return langLoader;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public ProtectTable getProtectTable() {
        return protectTable;
    }

    @Override
    public ProtectManager getProtectManager() {
        return protectManager;
    }

    @Override
    public RollbackManager getRollbackManager() {
        return rollbackManager;
    }

    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String CYAN = "\u001B[36m";
    private static final String GRAY = "\u001B[37m";
    private static final String WHITE = "\u001B[97m";
}
