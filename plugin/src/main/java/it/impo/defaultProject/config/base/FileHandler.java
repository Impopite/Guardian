package it.impo.defaultProject.config.base;

import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.*;

public class FileHandler {

    private final File file;
    private FileConfiguration config;

    private FileHandler(File file) {
        this.file = file;
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    @Contract("_, _ -> new")
    public static @NotNull FileHandler fromPath(@NotNull Plugin plugin, @NotNull String path) {
        getDataFolder(plugin);
        File file = new File(plugin.getDataFolder(), path);

        try {
            file.getParentFile().mkdirs();
            file.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create file: " + path, e);
        }
        return new FileHandler(file);
    }

    @Contract("_, _, _ -> new")
    public static @NotNull FileHandler fromResource(@NotNull Plugin plugin, @NotNull String resourcePath, @NotNull String destinationPath) {
        getDataFolder(plugin);
        File outFile = new File(plugin.getDataFolder(), destinationPath);

        if (!outFile.exists()) {
            outFile.getParentFile().mkdirs();

            try (InputStream in = plugin.getResource(resourcePath)) {
                if (in == null) throw new IllegalArgumentException("Resource not found: " + resourcePath);

                try (OutputStream out = new FileOutputStream(outFile)) {
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = in.read(buffer)) > 0) out.write(buffer, 0, length);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to save resource: " + resourcePath, e);
            }
        }

        return new FileHandler(outFile);
    }

    @Contract("_, _ -> new")
    public static @NotNull FileHandler fromResource(@NotNull Plugin plugin, @NotNull String path) {
        return fromResource(plugin, path, path);
    }

    public void reload() {
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void save() {
        try {
            config.save(file);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + file.getName(), e);
        }
    }

    public @NotNull ConfigurationSection getOrCreateSection(@NotNull String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        return section != null ? section : config.createSection(path);
    }

    public Configuration getConfig() {
        return config;
    }

    public File getFile() {
        return file;
    }

    private static void getDataFolder(@NotNull Plugin plugin) {
        plugin.getDataFolder().mkdirs();
    }
}
