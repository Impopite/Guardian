package it.impo.defaultProject.config;

import it.impo.defaultProject.config.constant.ConfigKey;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ConfigLoader {

    private final JavaPlugin plugin;
    private final BukkitAudiences adventure;

    private FileConfiguration config;
    private LangLoader langLoader;

    public ConfigLoader(@NotNull JavaPlugin plugin, @NotNull BukkitAudiences adventure) {
        this.plugin = plugin;
        this.adventure = adventure;
    }

    public ConfigLoader load() {
        plugin.saveDefaultConfig();
        config = plugin.getConfig();
        langLoader = initLang();
        return this;
    }

    public void reload() {
        plugin.reloadConfig();
        config = plugin.getConfig();
        langLoader = initLang();
    }

    public <T> T get(@NotNull ConfigKey key, @NotNull T defaultValue) {
        Object value = config.get(key.getPath());

        if (value == null) {
            warn(key, defaultValue, true);
            return defaultValue;
        }

        if (!defaultValue.getClass().isInstance(value)) {
            warn(key, defaultValue, false);
            return defaultValue;
        }

        return (T) value;
    }

    public LangLoader getLangLoader() {
        return langLoader;
    }

    private @NotNull LangLoader initLang() {
        saveDefaultLangs();
        String lang = get(ConfigKey.LANG_FILE, "EN_us");
        return LangLoader.fromFile(plugin, adventure, "lang/" + lang + ".yml");
    }

    private void saveDefaultLangs() {
        List.of("EN_us", "IT_it").forEach(lang -> {
            String path = "lang/" + lang + ".yml";
            if (!new java.io.File(plugin.getDataFolder(), path).exists()) {
                plugin.saveResource(path, false);
            }
        });
    }

    private void warn(@NotNull ConfigKey key, Object defaultValue, boolean empty) {
        plugin.getLogger().warning(
                "[Config] " + (empty ? "Empty" : "Invalid") + " value for '" + key.getPath() + "' — using default: " + defaultValue
        );
    }
}