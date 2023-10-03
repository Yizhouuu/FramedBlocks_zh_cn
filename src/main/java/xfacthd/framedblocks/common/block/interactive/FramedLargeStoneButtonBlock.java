package xfacthd.framedblocks.common.block.interactive;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedLargeStoneButtonBlock extends FramedStoneButtonBlock
{
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context)
    {
        return FramedLargeButtonBlock.getShape(state);
    }

    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        return state.getValue(FACE) != AttachFace.WALL;
    }

    @Override
    public BlockType getBlockType() { return BlockType.FRAMED_LARGE_STONE_BUTTON; }
}
