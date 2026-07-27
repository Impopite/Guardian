package it.impo.guardian.hook;

import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.AbstractDelegateExtent;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockStateHolder;
import it.impo.guardian.Guardian;
import it.impo.guardian.api.data.action.Action;
import it.impo.guardian.api.data.BasicLocation;
import it.impo.guardian.api.data.logs.impl.BlockLog;

import java.time.LocalDateTime;
import java.util.UUID;

public final class FaweLogExtent extends AbstractDelegateExtent {

    private final Guardian plugin;
    private final World world;
    private final UUID actorUuid;
    private final String actorName;

    public FaweLogExtent(Guardian plugin, Extent extent, World world, UUID actorUuid, String actorName) {
        super(extent);
        this.plugin = plugin;
        this.world = world;
        this.actorUuid = actorUuid;
        this.actorName = actorName;
    }

    @Override
    public <T extends BlockStateHolder<T>> boolean setBlock(BlockVector3 position, T block) throws WorldEditException {
        BlockState oldBlock = getExtent().getBlock(position);
        BlockState newBlock = block.toImmutableState();

        if (oldBlock.equals(newBlock)) {
            return false;
        }

        boolean changed = super.setBlock(position, block);
        if (!changed) {
            return false;
        }

        BasicLocation location = new BasicLocation(world.getName(), position.x(), position.y(), position.z());

        if (!oldBlock.getBlockType().getMaterial().isAir()) {
            plugin.getGuardianManager().saveLogs(new BlockLog(
                    actorUuid, actorName, location, LocalDateTime.now(), false,
                    oldBlock.getBlockType().id(),
                    oldBlock.getAsString(),
                    Action.BREAK
            ));
        }

        if (!newBlock.getBlockType().getMaterial().isAir()) {
            plugin.getGuardianManager().saveLogs(new BlockLog(
                    actorUuid, actorName, location, LocalDateTime.now(), false,
                    newBlock.getBlockType().id(),
                    newBlock.getAsString(),
                    Action.PLACE
            ));
        }

        return true;
    }
}