package it.impo.guardian.api.data.logs;

import it.impo.guardian.api.data.action.ContainerAction;
import org.bukkit.inventory.ItemStack;

/**
 * Represents a single logged item movement into or out of a container.
 *
 * <p>Used to render the history of a player's container interactions.</p>
 *
 * @param action whether the item was added or removed
 * @param item   the item involved
 * @param amount the number of affected items
 */
public record LoggedInteraction(ContainerAction action, ItemStack item, int amount) {
}