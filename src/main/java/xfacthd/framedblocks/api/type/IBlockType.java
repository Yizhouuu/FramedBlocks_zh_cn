package xfacthd.framedblocks.api.type;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.api.util.client.FramedBlockRenderProperties;

public interface IBlockType
{
    default boolean canOccludeWithSolidCamo() { return false; }

    boolean hasSpecialHitbox();

    CtmPredicate getCtmPredicate();

    SideSkipPredicate getSideSkipPredicate();

    ImmutableMap<BlockState, VoxelShape> generateShapes(ImmutableList<BlockState> states);

    boolean hasSpecialTile();

    boolean hasBlockItem();

    boolean supportsWaterLogging();

    /**
     * @implNote If this method returns true, then the associated block must override {@link Block#initializeClient(java.util.function.Consumer)}
     * and pass an instance of {@link FramedBlockRenderProperties} to the consumer to avoid crashing when the block is
     * hit while it can be passed through
     */
    default boolean allowMakingIntangible() { return false; }

    /**
     * @return true if this type represents a block that combines two models into one and allows those to have separate
     * camos applied.
     * @apiNote Returning true doesn't imply that the {@link Block}, {@link BlockEntity} or {@link BakedModel} extends
     * any specific class, it should only ideally guarantee compliance with the data layout used by the reference
     * implementation in FramedBlocks
     */
    default boolean isDoubleBlock() { return false; }

    /**
     * Return true if this block allows locking the state in order to suppress state changes from neighbor updates.
     * Useful to allow blocks like stairs to reside in impossible states, like a corner without neighbors
     * @implNote If this method returns true, then the associated block must have the {@link FramedProperties#STATE_LOCKED} property.
     * The actual update suppression needs to be handled by each block and is not automated
     */
    default boolean canLockState() { return false; }

    String getName();

    int compareTo(IBlockType other);
}