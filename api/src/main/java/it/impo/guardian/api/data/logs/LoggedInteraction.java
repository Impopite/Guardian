package it.impo.guardian.api.data.logs;

import it.impo.guardian.api.data.action.ContainerAction;
import org.bukkit.inventory.ItemStack;

public record LoggedInteraction(ContainerAction action, ItemStack item, int amount) {
}
