package it.impo.guardian.api.utils;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;

/**
 * Utility methods for working with blocks.
 */
public final class BlockUtils {

    /**
     * Resolves the "main" block of an openable block that is split in two halves,
     * such as doors or double blocks.
     *
     * <p>If the given block is a {@link Bisected} block in its top half, the block right
     * below it is returned. For every other block the input is returned unchanged.</p>
     *
     * @param block the block to resolve
     * @return the bottom half if the block was the top half of a bisected block, the input otherwise
     */
    public static Block resolveOpenableBlock(Block block) {
        if (!(block.getBlockData() instanceof Bisected bisected)) return block;
        return bisected.getHalf() == Bisected.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
    }
}