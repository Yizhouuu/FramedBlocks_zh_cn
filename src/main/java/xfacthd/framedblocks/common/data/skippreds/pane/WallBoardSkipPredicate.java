package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;

public final class WallBoardSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);

        if (side == dir)
        {
            return SideSkipPredicate.CTM.test(level, pos, state, adjState, side);
        }

        if (side != dir.getOpposite() && adjState.getBlock() == state.getBlock())
        {
            Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
            return adjDir == dir && SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        return false;
    }
}
