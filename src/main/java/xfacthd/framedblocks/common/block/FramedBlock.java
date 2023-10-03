package xfacthd.framedblocks.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.block.AbstractFramedBlock;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.common.data.*;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.data.property.CornerType;
import xfacthd.framedblocks.common.data.property.SlopeType;

public abstract class FramedBlock extends AbstractFramedBlock
{
    protected FramedBlock(BlockType blockType) { this(blockType, IFramedBlock.createProperties(blockType)); }

    protected FramedBlock(BlockType blockType, Properties props) { super(blockType, props); }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isPathfindable(BlockState state, BlockGetter level, BlockPos pos, PathComputationType type)
    {
        if (getBlockType() != BlockType.FRAMED_CUBE) { return false; }
        return super.isPathfindable(state, level, pos, type);
    }

    protected static BlockState withSlopeType(BlockState state, Direction side, Direction facing, Vec3 hitVec)
    {
        state = state.setValue(FramedProperties.FACING_HOR, facing);

        Vec3 hitPoint = Utils.fraction(hitVec);
        if (!Utils.isY(side))
        {
            if (hitPoint.y() < (3D / 16D))
            {
                side = Direction.UP;
            }
            else if (hitPoint.y() > (13D / 16D))
            {
                side = Direction.DOWN;
            }
        }

        if (side == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.TOP);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.BOTTOM);
        }
        else
        {
            state = state.setValue(PropertyHolder.SLOPE_TYPE, SlopeType.HORIZONTAL);
            state = withCornerFacing(state, side, facing, hitVec);
        }

        return state;
    }

    protected static BlockState withCornerType(BlockState state, BlockPlaceContext context, Direction side, Direction typeSide, Vec3 hitPoint, Direction facing)
    {
        if (typeSide == Direction.DOWN)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.TOP);
            state = withCornerFacing(state, side, facing, hitPoint);
        }
        else if (typeSide == Direction.UP)
        {
            state = state.setValue(PropertyHolder.CORNER_TYPE, CornerType.BOTTOM);
            state = withCornerFacing(state, side, facing, hitPoint);
        }
        else
        {
            boolean xAxis = Utils.isX(context.getClickedFace());
            boolean positive = Utils.isPositive(context.getClickedFace().getCounterClockWise());
            double xz = xAxis ? hitPoint.z() : hitPoint.x();
            double y = hitPoint.y();

            CornerType type;
            if ((xz > .5D) == positive)
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_RIGHT : CornerType.HORIZONTAL_BOTTOM_RIGHT;
            }
            else
            {
                type = (y > .5D) ? CornerType.HORIZONTAL_TOP_LEFT : CornerType.HORIZONTAL_BOTTOM_LEFT;
            }

            state = state.setValue(PropertyHolder.CORNER_TYPE, type).setValue(FramedProperties.FACING_HOR, facing);
        }
        return state;
    }
}