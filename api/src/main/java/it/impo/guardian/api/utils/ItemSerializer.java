package it.impo.guardian.api.utils;

import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.IOException;

/**
 * Utilities to serialize {@link ItemStack}s into raw bytes and back, so they
 * can be stored in the database.
 */
public class ItemSerializer {

    /**
     * Serializes an item into raw bytes.
     *
     * @param item the item to serialize
     * @return the serialized bytes, or an empty array if the item is {@code null} or air
     * @throws IOException if the item cannot be serialized
     */
    public static byte[] itemToBytes(ItemStack item) throws IOException {
        if (item == null || item.getType().isAir()) return new byte[0];
        return item.serializeAsBytes();
    }

    /**
     * Deserializes raw bytes back into an {@link ItemStack}.
     *
     * @param bytes the serialized bytes
     * @return the deserialized item, or {@code null} if the bytes are {@code null} or empty
     * @throws IOException if the bytes cannot be deserialized
     */
    public static ItemStack itemFromBytes(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length == 0) return null;
        return ItemStack.deserializeBytes(bytes);
    }

    /**
     * Serializes an item into raw bytes without throwing, logging a warning on failure.
     *
     * @param item   the item to serialize
     * @param plugin the plugin used for logging
     * @return the serialized bytes, or an empty array on error or if the item is {@code null}/air
     */
    public static byte[] safeItemToBytes(ItemStack item, Plugin plugin) {
        try {
            return itemToBytes(item);
        } catch (IOException e) {
            plugin.getLogger().warning("Error while trying to serialize item to bytes: " + e);
            return new byte[0];
        }
    }

    /**
     * Deserializes raw bytes back into an {@link ItemStack} without throwing,
     * logging a warning on failure.
     *
     * @param bytes  the serialized bytes
     * @param plugin the plugin used for logging
     * @return the deserialized item, or {@code null} on error or if the bytes are {@code null}/empty
     */
    public static ItemStack safeItemFromBytes(byte[] bytes, Plugin plugin) {
        try {
            return itemFromBytes(bytes);
        } catch (IOException e) {
            plugin.getLogger().warning("Error while trying to deserialize item from bytes: " + e);
            return null;
        }
    }
}