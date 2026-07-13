package it.impo.protect.api.data.logs;

import it.impo.protect.api.data.Action.ContainerAction;
import org.bukkit.inventory.ItemStack;

public record LoggedInteraction(ContainerAction action, ItemStack item, int amount) {
}
