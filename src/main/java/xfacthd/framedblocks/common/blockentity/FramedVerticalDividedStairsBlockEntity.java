package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.util.DoubleSoundMode;

public class FramedVerticalDividedStairsBlockEntity extends FramedDoubleBlockEntity
{
    public FramedVerticalDividedStairsBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedVerticalDividedStairs.get(), pos, state);
    }

    @Override
    protected boolean hitSecondary(BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        if (side == Direction.UP) { return true; }
        if (side == Direction.DOWN) { return false; }
        return Utils.fraction(hit.getLocation()).y > .5;
    }

    @Override
    public DoubleSoundMode getSoundMode()
    {
        return DoubleSoundMode.SECOND;
    }

    @Override
    public CamoContainer getCamo(Direction side)
    {
        return switch (side)
        {
            case UP -> getCamoTwo();
            case DOWN -> getCamo();
            default -> EmptyCamoContainer.EMPTY;
        };
    }

    @Override
    public boolean isSolidSide(Direction side)
    {
        Direction facing = getBlockState().getValue(FramedProperties.FACING_HOR);
        if (side == facing || side == facing.getCounterClockWise())
        {
            return getCamo().isSolid(level, worldPosition) && getCamoTwo().isSolid(level, worldPosition);
        }

        return false;
    }
}
