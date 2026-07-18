package it.impo.protect.api.manager;

import org.bukkit.entity.Player;

import java.time.Duration;

public abstract class RollbackManager {

    public abstract void rollbackBlocks(Player player, int radius, Duration duration);

    public abstract void rollbackContainers(Player player, int radius, Duration duration);
}
