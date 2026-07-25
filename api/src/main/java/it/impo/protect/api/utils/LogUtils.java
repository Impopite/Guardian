package it.impo.protect.api.utils;

import it.impo.protect.api.data.Action.Action;
import it.impo.protect.api.data.Action.ContainerAction;
import it.impo.protect.api.data.Action.Interaction;
import it.impo.protect.api.data.Action.ItemAction;
import it.impo.protect.api.data.logs.Logs;
import it.impo.protect.api.data.logs.impl.BlockLog;
import it.impo.protect.api.data.logs.impl.ContainerLog;
import it.impo.protect.api.data.logs.impl.InteractLog;
import it.impo.protect.api.data.logs.impl.ItemLog;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogUtils {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");

    private static String prettyName(String materialName) {
        StringBuilder sb = new StringBuilder();
        for (String part : materialName.split("_")) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(Character.toUpperCase(part.charAt(0)));
            sb.append(part.substring(1).toLowerCase());
        }
        return sb.toString();
    }
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d+)([smhd])");
    private static final Pattern STRICT_TIME_PATTERN = Pattern.compile("^(\\d+[smhd])+$");


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
                default  -> 0;
            };
        }
        return seconds;
    }

    public static boolean isValidTimeInput(String input) {
        if (input == null || input.isBlank()) return false;
        return STRICT_TIME_PATTERN.matcher(input.toLowerCase().trim()).matches();
    }

    public static Component formatLogs(Logs logs, Plugin plugin) {
        return switch(logs){
            case BlockLog blockLog -> formatBlockLog(blockLog);
            case ContainerLog containerLog -> formatContainerLog(containerLog, plugin);
            case ItemLog itemLog -> formatItemLog(itemLog, plugin);
            case InteractLog interactLog -> formatInteractLog(interactLog);
            default -> Component.empty();
        };
    }

    public static Component formatHistory(Logs logs, Plugin plugin) {
        return switch(logs){
            case BlockLog log -> formatBlockHistory(log);
            case ContainerLog log -> formatItemStackHistory(log, plugin);
            case ItemLog log -> formatItemStackHistory(log, plugin);
            case InteractLog log -> formatInteractHistory(log);
            default -> Component.empty();
        };
    }

    private static Component formatBlockLog(BlockLog blockLog){
        String action = blockLog.getAction() == Action.PLACE ? "§a+" : "§c-";
        return Component.text("§8[§b" + blockLog.getDate().format(FORMATTER) + "§8] §f" + blockLog.getPlayerName() + " §7" + action + " §f" + prettyName(blockLog.getBlockType()) + " §8(§f" + blockLog.getLocation() + "§8)");
    }

    private static Component formatContainerLog(ContainerLog containerLog, Plugin plugin){
        String action = containerLog.getAction() == ContainerAction.ADD ? "§a+" : "§c-";
        ItemStack item = ItemSerializer.safeItemFromBytes(containerLog.getItem(), plugin);
        return Component.text("§8[§b" + containerLog.getDate().format(FORMATTER) + "§8] §f" + containerLog.getPlayerName() + " §7" + action + " §f" + item.getItemMeta().getDisplayName() + " §8(§f" + containerLog.getLocation() + "§8)");
    }

    private static Component formatItemLog(ItemLog itemLog, Plugin plugin) {
        String action = itemLog.getAction() == ItemAction.PICKUP ? "§a+" : "§c-";
        ItemStack item = ItemSerializer.safeItemFromBytes(itemLog.getItem(), plugin);
        return Component.text("§8[§b" + itemLog.getDate().format(FORMATTER) + "§8] §f" + itemLog.getPlayerName() + " §7" + action + " §f" + item.getItemMeta().getDisplayName() + " §8(§f" + itemLog.getLocation() + "§8)");
    }

    private static Component formatInteractLog(InteractLog interactLog){
        String action = interactLog.getAction() == Interaction.OPEN ? "§aOPEN" : "§cCLOSE";
        return Component.text("§8[§b" + interactLog.getDate().format(FORMATTER) + "§8] §f" + interactLog.getPlayerName() + " §7" + action + " §f" + prettyName(interactLog.getBlockType()) + " §8(§f" + interactLog.getLocation() + "§8)");
    }

    private static Component formatBlockHistory(BlockLog log) {
        return Component.text("§8[§b" + log.getDate().format(FORMATTER) + "§8] §f" + log.getPlayerName() + " §7" + log.getAction().getLabel() + " §f" + prettyName(log.getBlockType()) + " §8(§f" + log.getLocation() + "§8)");
    }

    private static Component formatItemStackHistory(ContainerLog log, Plugin plugin) {
        ItemStack item = ItemSerializer.safeItemFromBytes(log.getItem(), plugin);
        String name = item != null ? item.getType().name() : "Unknown";
        return Component.text("§8[§b" + log.getDate().format(FORMATTER) + "§8] §f" + log.getPlayerName() + " §7" + log.getAction().getLabel() + " §f" + name + " x" + log.getAmount() + " §8(§f" + log.getLocation() + "§8)");
    }

    private static Component formatItemStackHistory(ItemLog log, Plugin plugin) {
        ItemStack item = ItemSerializer.safeItemFromBytes(log.getItem(), plugin);
        String name = item != null ? item.getType().name() : "Unknown";
        return Component.text("§8[§b" + log.getDate().format(FORMATTER) + "§8] §f" + log.getPlayerName() + " §7" + log.getAction().getLabel() + " §f" + name + " x" + log.getAmount() + " §8(§f" + log.getLocation() + "§8)");
    }

    private static Component formatInteractHistory(InteractLog log) {
        return Component.text("§8[§b" + log.getDate().format(FORMATTER) + "§8] §f" + log.getPlayerName() + " §7" + log.getAction().getLabel() + " §f" + prettyName(log.getBlockType()) + " §8(§f" + log.getLocation() + "§8)");
    }
}
