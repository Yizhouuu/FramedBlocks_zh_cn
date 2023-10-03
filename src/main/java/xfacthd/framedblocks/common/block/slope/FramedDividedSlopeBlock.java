package xfacthd.framedblocks.common.block.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.client.util.DoubleBlockParticleMode;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedDividedSlopeBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.SlopeType;

public class FramedDividedSlopeBlock extends AbstractFramedDoubleBlock
{
    public FramedDividedSlopeBlock()
    {
        super(BlockType.FRAMED_DIVIDED_SLOPE);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(
                FramedProperties.FACING_HOR, PropertyHolder.SLOPE_TYPE, BlockStateProperties.WATERLOGGED,
                FramedProperties.SOLID, FramedProperties.GLOWING, FramedProperties.Y_SLOPE
        );
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = withSlopeType(defaultBlockState(), context.getClickedFace(), context.getHorizontalDirection(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        if (Utils.isY(face) || (type != SlopeType.HORIZONTAL && face == dir.getOpposite()))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE && face == dir)
        {
            return state.cycle(PropertyHolder.SLOPE_TYPE);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot) { return rotate(state, Direction.UP, rot); }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        if (state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL)
        {
            return Utils.mirrorCornerBlock(state, mirror);
        }
        else
        {
            return Utils.mirrorFaceBlock(state, mirror);
        }
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        SlopeType type = state.getValue(PropertyHolder.SLOPE_TYPE);
        Direction facing = state.getValue(FramedProperties.FACING_HOR);

        if (type == SlopeType.HORIZONTAL)
        {
            BlockState defState = FBContent.blockFramedVerticalHalfSlope.get().defaultBlockState();
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, false),
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, true)
            );
        }
        else
        {
            BlockState defState = FBContent.blockFramedHalfSlope.get().defaultBlockState();
            boolean top = type == SlopeType.TOP;
            boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);
            return new Tuple<>(
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, false)
                            .setValue(FramedProperties.Y_SLOPE, ySlope),
                    defState.setValue(FramedProperties.FACING_HOR, facing)
                            .setValue(FramedProperties.TOP, top)
                            .setValue(PropertyHolder.RIGHT, true)
                            .setValue(FramedProperties.Y_SLOPE, ySlope)
            );
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDividedSlopeBlockEntity(pos, state);
    }



    public static DoubleBlockParticleMode particleMode(BlockState state)
    {
        boolean horizontal = state.getValue(PropertyHolder.SLOPE_TYPE) == SlopeType.HORIZONTAL;
        return horizontal ? DoubleBlockParticleMode.SECOND : DoubleBlockParticleMode.EITHER;
    }

    public static BlockState itemSource()
    {
        return FBContent.blockFramedDividedSlope.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
