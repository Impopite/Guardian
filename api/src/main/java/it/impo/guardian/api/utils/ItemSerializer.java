package it.impo.guardian.api.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

public class ItemSerializer {

    public static byte[] itemToBytes(ItemStack item) throws IOException {
        if (item == null || item.getType().isAir()) return new byte[0];
        return item.serializeAsBytes();
    }

    public static ItemStack itemFromBytes(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) return null;
        return ItemStack.deserializeBytes(bytes);
    }

    public static byte[] safeItemToBytes(ItemStack item, Plugin plugin) {
        try {
            return itemToBytes(item);
        } catch (IOException e) {
            plugin.getLogger().warning("Error while trying to serialize item to bytes: " + e);
            return new byte[0];
        }
    }

    public static ItemStack safeItemFromBytes(byte[] bytes, Plugin plugin) {
        try {
            return itemFromBytes(bytes);
        } catch (IOException e) {
            plugin.getLogger().warning("Error while trying to deserialize item from bytes: " + e);
            return null;
        }
    }
}
