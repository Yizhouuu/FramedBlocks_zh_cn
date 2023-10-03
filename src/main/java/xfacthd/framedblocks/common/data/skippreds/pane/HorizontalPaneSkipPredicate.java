package xfacthd.framedblocks.common.data.skippreds.pane;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.api.util.Utils;

public final class HorizontalPaneSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() == state.getBlock() && !Utils.isY(side))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }

        return false;
    }
}
