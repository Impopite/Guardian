package it.impo.guardian.api.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;

public final class BlockUtils {

    public static Block resolveOpenableBlock(Block block) {
        if (!(block.getBlockData() instanceof Bisected bisected)) return block;
        return bisected.getHalf() == Bisected.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
    }
}
