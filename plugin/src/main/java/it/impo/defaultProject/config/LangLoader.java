package it.impo.defaultProject.config;

import it.impo.defaultProject.config.base.FileHandler;
import it.impo.defaultProject.config.constant.LangKey;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class LangLoader {

    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    private final Configuration langConfig;
    private final BukkitAudiences adventure;
    private final Map<String, String> palette;

    private LangLoader(Configuration langConfig, BukkitAudiences adventure) {
        this.langConfig = langConfig;
        this.adventure = adventure;
        this.palette = buildPalette();
    }

    @Contract("_, _, _ -> new")
    public static @NotNull LangLoader fromFile(@NotNull Plugin plugin, @NotNull BukkitAudiences adventure, @NotNull String filePath) {
        FileHandler handle = FileHandler.fromPath(plugin, filePath);

        if (!handle.getFile().exists()) {
            plugin.getLogger().log(Level.SEVERE, "[Lang] File not found: " + filePath + ". Falling back to EN_us.");
            handle = FileHandler.fromResource(plugin, "lang/EN_us.yml");
        }

        plugin.getLogger().log(Level.INFO, "[Lang] Loaded: " + handle.getFile().getName());
        return new LangLoader(handle.getConfig(), adventure);
    }

    public @NotNull Component get(@NotNull LangKey key, TagResolver... resolvers) {
        return prefix().append(component(key, resolvers));
    }

    public @NotNull Component get(@NotNull LangKey key) {
        return prefix().append(component(key));
    }

    public @NotNull Component prefix() {
        return component(LangKey.PREFIX);
    }

    public @NotNull Component component(@NotNull LangKey key, TagResolver... resolvers) {
        String raw = langConfig.getString(key.getPath(), "");

        TagResolver paletteResolver = TagResolver.resolver(
                palette.entrySet().stream()
                        .map(e -> Placeholder.parsed(e.getKey(), e.getValue()))
                        .toArray(TagResolver.Single[]::new)
        );

        return MINI_MESSAGE.deserialize(raw, TagResolver.resolver(paletteResolver, TagResolver.resolver(resolvers)));
    }

    public @NotNull Component component(@NotNull LangKey key) {
        return component(key, TagResolver.empty());
    }

    public void send(@NotNull CommandSender sender, @NotNull LangKey key, TagResolver... resolvers) {
        adventure.sender(sender).sendMessage(get(key, resolvers));
    }

    public void send(@NotNull CommandSender sender, @NotNull LangKey key) {
        send(sender, key, TagResolver.empty());
    }

    public boolean isEmpty(@NotNull LangKey key) {
        return langConfig.getString(key.getPath(), "").isEmpty();
    }

    private @NotNull Map<String, String> buildPalette() {
        Map<String, String> map = new HashMap<>();
        ConfigurationSection vars = langConfig.getConfigurationSection("vars");

        if (vars == null) return map;

        vars.getKeys(false).forEach(key -> {
            String value = vars.getString(key);
            if (value != null) map.put(key.toLowerCase(), value);
        });

        return map;
    }
}
