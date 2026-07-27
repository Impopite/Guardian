package it.impo.guardian.api.data.logs;

import it.impo.guardian.api.data.Action.ContainerAction;
import org.bukkit.inventory.ItemStack;

public record LoggedInteraction(ContainerAction action, ItemStack item, int amount) {
}
