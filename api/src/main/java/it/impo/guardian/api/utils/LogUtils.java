package it.impo.guardian.api.utils;

import it.impo.guardian.api.data.action.Action;
import it.impo.guardian.api.data.action.ContainerAction;
import it.impo.guardian.api.data.action.Interaction;
import it.impo.guardian.api.data.action.ItemAction;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.Logs;
import it.impo.guardian.api.data.logs.impl.BlockLog;
import it.impo.guardian.api.data.logs.impl.ContainerLog;
import it.impo.guardian.api.data.logs.impl.InteractLog;
import it.impo.guardian.api.data.logs.impl.ItemLog;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilities to parse time inputs and to format logs as Adventure {@link Component}s
 * for the inspect and lookup layouts.
 */
public class LogUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([smhd])");
    private static final Pattern STRICT_TIME_PATTERN = Pattern.compile("^(\\d+[smhd])+$");

    private static String prettyName(String materialName) {
        StringBuilder sb = new StringBuilder();
        for (String part : materialName.split("_")) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    /**
     * Parses a compact time string such as {@code "10s"}, {@code "5m"}, {@code "2h"} or
     * {@code "1d"} (and combinations like {@code "1h30m"}) into seconds.
     *
     * @param input the input to parse
     * @return the total amount of seconds, or {@code 0} if the input is {@code null},
     *         blank or invalid
     */
    public static long parseTime(String input) {
        if (input == null || input.isBlank()) return 0;
        input = input.toLowerCase().trim();
        if (!isValidTimeInput(input)) return 0;

        long seconds = 0;
        Matcher m = TIME_PATTERN.matcher(input);
        while (m.find()) {
            long v = Long.parseLong(m.group(1));
            seconds += switch (m.group(2).charAt(0)) {
                case 's' -> v;
                case 'm' -> v * 60;
                case 'h' -> v * 3600;
                case 'd' -> v * 86400;
                default -> 0;
            };
        }
        return seconds;
    }

    /**
     * Checks whether a string is a valid Guardian time input, i.e. one or more
     * number+unit pairs made only of the suffixes {@code s}, {@code m}, {@code h} and {@code d}.
     *
     * @param input the input to validate
     * @return {@code true} if the input is a valid time string
     */
    public static boolean isValidTimeInput(String input) {
        if (input == null || input.isBlank()) return false;
        return STRICT_TIME_PATTERN.matcher(input.toLowerCase().trim()).matches();
    }

    /**
     * Formats a log into the interactive one-time line used by the inspect feature.
     *
     * @param logs   the log to format
     * @param player the player that will receive the message (used for teleport callbacks)
     * @param plugin the plugin used to deserialize items
     * @return the formatted component, or an empty component for unknown log types
     */
    public static Component formatLogs(Logs logs, Player player, Plugin plugin) {
        return switch (logs) {
            case BlockLog log -> formatBlockLog(log, player);
            case ContainerLog log -> formatContainerLog(log, player, plugin);
            case ItemLog log -> formatItemLog(log, player, plugin);
            case InteractLog log -> formatInteractLog(log, player);
            default -> Component.empty();
        };
    }

    /**
     * Formats a log into the line used by the history/lookup feature, showing the plain
     * action label instead of the compact +/- symbol.
     *
     * @param logs   the log to format
     * @param player the player that will receive the message (used for teleport callbacks)
     * @param plugin the plugin used to deserialize items
     * @return the formatted component, or an empty component for unknown log types
     */
    public static Component formatHistory(Logs logs, Player player, Plugin plugin) {
        return switch (logs) {
            case BlockLog log -> formatBlockHistory(log, player);
            case ContainerLog log -> formatItemStackHistory(log, player, plugin);
            case ItemLog log -> formatItemStackHistory(log, player, plugin);
            case InteractLog log -> formatInteractHistory(log, player);
            default -> Component.empty();
        };
    }

    private static Component dateComponent(String date) {
        return Component.text("§8[§bDATE§8]")
                .hoverEvent(HoverEvent.showText(Component.text(date)));
    }

    private static Component locationComponent(String world, int x, int y, int z, Player player) {
        return Component.text("§8(§7" + x + ", " + y + ", " + z + "§8)")
                .clickEvent(ClickEvent.callback(audience -> {
                    World w = Bukkit.getWorld(world);
                    if (w != null) player.teleport(new Location(w, x + 0.5, y, z + 0.5));
                }))
                .hoverEvent(HoverEvent.showText(
                        Component.text("Click to teleport", NamedTextColor.YELLOW)
                                .appendNewline()
                                .append(Component.text(world + " " + x + " " + y + " " + z, NamedTextColor.WHITE))
                ));
    }

    private static Component itemComponent(ItemStack item, String fallbackName, Player player) {
        if (item == null) {
            return Component.text(fallbackName).color(NamedTextColor.WHITE);
        }
        ItemMeta meta = item.getItemMeta();
        Component displayName = meta != null && meta.hasDisplayName()
                ? meta.displayName()
                : Component.text(prettyName(item.getType().name()));
        List<Component> lore = meta != null ? meta.lore() : null;

        HoverEvent<Component> hover = HoverEvent.showText(displayName);
        if (lore != null && !lore.isEmpty()) {
            Component loreText = Component.empty();
            for (Component line : lore) {
                loreText = loreText.appendNewline().append(line);
            }
            hover = HoverEvent.showText(displayName.appendNewline().append(loreText));
        }

        return Component.text(fallbackName)
                .color(NamedTextColor.WHITE)
                .hoverEvent(hover);
    }

    private static Component line(Component... parts) {
        Component root = Component.empty();
        for (Component part : parts) root = root.append(part);
        return root;
    }

    private static Component space() {
        return Component.text(" ");
    }

    private static Component formatBlockLog(BlockLog log, Player player) {
        String action = log.getAction() == Action.PLACE ? "+" : "-";
        NamedTextColor actionColor = log.getAction() == Action.PLACE ? NamedTextColor.GREEN : NamedTextColor.RED;
        return line(
                dateComponent(log.getDate().format(FORMATTER)),
                space(),
                Component.text(log.getPlayerName()).color(NamedTextColor.WHITE),
                space(),
                Component.text(action).color(actionColor),
                space(),
                itemComponent(null, prettyName(log.getBlockType()), player),
                space(),
                locationComponent(log.getLocation().world(), log.getLocation().x(), log.getLocation().y(), log.getLocation().z(), player)
        );
    }

    private static Component formatContainerLog(ContainerLog log, Player player, Plugin plugin) {
        String action = log.getAction() == ContainerAction.ADD ? "+" : "-";
        NamedTextColor actionColor = log.getAction() == ContainerAction.ADD ? NamedTextColor.GREEN : NamedTextColor.RED;
        ItemStack item = ItemSerializer.safeItemFromBytes(log.getItem(), plugin);
        String itemName = item != null ? prettyName(item.getType().name()) : "Unknown";
        return line(
                dateComponent(log.getDate().format(FORMATTER)),
                space(),
                Component.text(log.getPlayerName()).color(NamedTextColor.WHITE),
                space(),
                Component.text(action).color(actionColor),
                space(),
                itemComponent(item, itemName, player),
                space(),
                locationComponent(log.getLocation().world(), log.getLocation().x(), log.getLocation().y(), log.getLocation().z(), player)
        );
    }

    private static Component formatItemLog(ItemLog log, Player player, Plugin plugin) {
        String action = log.getAction() == ItemAction.PICKUP ? "+" : "-";
        NamedTextColor actionColor = log.getAction() == ItemAction.PICKUP ? NamedTextColor.GREEN : NamedTextColor.RED;
        ItemStack item = ItemSerializer.safeItemFromBytes(log.getItem(), plugin);
        String itemName = item != null ? prettyName(item.getType().name()) : "Unknown";
        return line(
                dateComponent(log.getDate().format(FORMATTER)),
                space(),
                Component.text(log.getPlayerName()).color(NamedTextColor.WHITE),
                space(),
                Component.text(action).color(actionColor),
                space(),
                itemComponent(item, itemName, player),
                space(),
                locationComponent(log.getLocation().world(), log.getLocation().x(), log.getLocation().y(), log.getLocation().z(), player)
        );
    }

    private static Component formatInteractLog(InteractLog log, Player player) {
        String action = log.getAction() == Interaction.OPEN ? "OPEN" : "CLOSE";
        NamedTextColor actionColor = log.getAction() == Interaction.OPEN ? NamedTextColor.GREEN : NamedTextColor.RED;
        return line(
                dateComponent(log.getDate().format(FORMATTER)),
                space(),
                Component.text(log.getPlayerName()).color(NamedTextColor.WHITE),
                space(),
                Component.text(action).color(actionColor),
                space(),
                itemComponent(null, prettyName(log.getBlockType()), player),
                space(),
                locationComponent(log.getLocation().world(), log.getLocation().x(), log.getLocation().y(), log.getLocation().z(), player)
        );
    }

    // --- History format (action: label) ---

    private static Component formatBlockHistory(BlockLog log, Player player) {
        return line(
                dateComponent(log.getDate().format(FORMATTER)),
                space(),
                Component.text(log.getPlayerName()).color(NamedTextColor.WHITE),
                space(),
                Component.text(log.getAction().getLabel()).color(NamedTextColor.GRAY),
                space(),
                itemComponent(null, prettyName(log.getBlockType()), player),
                space(),
                locationComponent(log.getLocation().world(), log.getLocation().x(), log.getLocation().y(), log.getLocation().z(), player)
        );
    }

    private static Component formatItemStackHistory(ContainerLog log, Player player, Plugin plugin) {
        return formatItemStackHistory(log.getItem(), log.getAction().getLabel(), log.getAmount(), log.getPlayerName(), log.getDate(), log.getLocation(), player, plugin);
    }

    private static Component formatItemStackHistory(ItemLog log, Player player, Plugin plugin) {
        return formatItemStackHistory(log.getItem(), log.getAction().getLabel(), log.getAmount(), log.getPlayerName(), log.getDate(), log.getLocation(), player, plugin);
    }

    private static Component formatItemStackHistory(byte[] itemBytes, String actionLabel, int amount, String playerName, LocalDateTime date, BasicLocation location, Player player, Plugin plugin) {
        ItemStack item = ItemSerializer.safeItemFromBytes(itemBytes, plugin);
        String name = item != null ? prettyName(item.getType().name()) : "Unknown";
        return line(
                dateComponent(date.format(FORMATTER)),
                space(),
                Component.text(playerName).color(NamedTextColor.WHITE),
                space(),
                Component.text(actionLabel).color(NamedTextColor.GRAY),
                space(),
                itemComponent(item, name, player),
                space(),
                Component.text("x" + amount).color(NamedTextColor.WHITE),
                space(),
                locationComponent(location.world(), location.x(), location.y(), location.z(), player)
        );
    }

    private static Component formatInteractHistory(InteractLog log, Player player) {
        return line(
                dateComponent(log.getDate().format(FORMATTER)),
                space(),
                Component.text(log.getPlayerName()).color(NamedTextColor.WHITE),
                space(),
                Component.text(log.getAction().getLabel()).color(NamedTextColor.GRAY),
                space(),
                itemComponent(null, prettyName(log.getBlockType()), player),
                space(),
                locationComponent(log.getLocation().world(), log.getLocation().x(), log.getLocation().y(), log.getLocation().z(), player)
        );
    }
}