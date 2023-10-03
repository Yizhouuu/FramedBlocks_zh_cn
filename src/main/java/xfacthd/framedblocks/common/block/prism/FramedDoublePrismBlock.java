package xfacthd.framedblocks.common.block.prism;

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
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.client.util.DoubleBlockParticleMode;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedDoublePrismBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;

public class FramedDoublePrismBlock extends AbstractFramedDoubleBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        return side != null && side.getAxis() != axis;
    };

    public FramedDoublePrismBlock()
    {
        super(BlockType.FRAMED_DOUBLE_PRISM);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(BlockStateProperties.FACING, BlockStateProperties.AXIS, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedPrismBlock.getStateForPlacement(context, defaultBlockState(), getBlockType());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        if (rot == Rotation.NONE) { return state; }

        Direction dir = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);

        if (Utils.isY(dir))
        {
            if (rot == Rotation.CLOCKWISE_180)
            {
                return state;
            }

            return state.setValue(
                    BlockStateProperties.AXIS,
                    Utils.nextAxisNotEqualTo(axis, dir.getAxis())
            );
        }
        else
        {
            if (!axis.isVertical())
            {
                state = state.setValue(
                        BlockStateProperties.AXIS,
                        Utils.nextAxisNotEqualTo(axis, Direction.Axis.Y)
                );
            }
            return state.setValue(BlockStateProperties.FACING, rot.rotate(dir));
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorFaceBlock(state, BlockStateProperties.FACING, mirror);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        Direction facing = state.getValue(BlockStateProperties.FACING);
        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        return new Tuple<>(
                FBContent.blockFramedInnerPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing)
                        .setValue(BlockStateProperties.AXIS, axis)
                        .setValue(FramedProperties.Y_SLOPE, ySlope),
                FBContent.blockFramedPrism.get()
                        .defaultBlockState()
                        .setValue(BlockStateProperties.FACING, facing.getOpposite())
                        .setValue(BlockStateProperties.AXIS, axis)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedDoublePrismBlockEntity(pos, state);
    }



    public static DoubleBlockParticleMode particleMode(BlockState state)
    {
        Direction dir = state.getValue(BlockStateProperties.FACING);
        if (dir == Direction.UP)
        {
            return DoubleBlockParticleMode.SECOND;
        }
        else if (dir == Direction.DOWN || state.getValue(BlockStateProperties.AXIS) != Direction.Axis.Y)
        {
            return DoubleBlockParticleMode.FIRST;
        }
        return DoubleBlockParticleMode.EITHER;
    }

    public static BlockState itemModelSource()
    {
        return FBContent.blockFramedDoublePrism.get()
                .defaultBlockState()
                .setValue(BlockStateProperties.FACING, Direction.UP);
    }
}
