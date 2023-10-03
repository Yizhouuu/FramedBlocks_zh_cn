package xfacthd.framedblocks.common.data.skippreds.pillar;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.WallBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WallSide;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class WallSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() == state.getBlock())
        {
            if (Utils.isY(side))
            {
                boolean up = state.getValue(WallBlock.UP);
                boolean adjUp = adjState.getValue(WallBlock.UP);
                return up == adjUp && SideSkipPredicate.compareState(level, pos, side, state, adjState);
            }
            else if (getArm(state, side) == getArm(adjState, side.getOpposite()))
            {
                return SideSkipPredicate.compareState(level, pos, side, state, adjState);
            }
        }
        return false;
    }

    private static WallSide getArm(BlockState state, Direction dir)
    {
        return switch (dir)
        {
            case NORTH -> state.getValue(WallBlock.NORTH_WALL);
            case EAST -> state.getValue(WallBlock.EAST_WALL);
            case SOUTH -> state.getValue(WallBlock.SOUTH_WALL);
            case WEST -> state.getValue(WallBlock.WEST_WALL);
            default -> throw new IllegalArgumentException("Invalid wall arm direction");
        };
    }
}
