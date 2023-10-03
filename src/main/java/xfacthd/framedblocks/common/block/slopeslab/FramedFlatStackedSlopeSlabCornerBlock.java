package xfacthd.framedblocks.common.block.slopeslab;

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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.blockentity.FramedStackedSlopeSlabBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;

public class FramedFlatStackedSlopeSlabCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedFlatStackedSlopeSlabCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState()
                .setValue(FramedProperties.TOP, false)
                .setValue(FramedProperties.Y_SLOPE, false)
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, FramedProperties.TOP, FramedProperties.Y_SLOPE, BlockStateProperties.WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        BlockState state = withCornerFacing(
                defaultBlockState(),
                context.getClickedFace(),
                context.getHorizontalDirection(),
                context.getClickLocation()
        );
        state = withTop(state, context.getClickedFace(), context.getClickLocation());
        return withWater(state, context.getLevel(), context.getClickedPos());
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        return IFramedBlock.toggleYSlope(state, level, pos, player);
    }

    @Override
    public BlockState rotate(BlockState state, BlockHitResult hit, Rotation rot)
    {
        Direction face = hit.getDirection();

        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (face == dir.getOpposite() || face == dir.getClockWise())
        {
            boolean top = state.getValue(FramedProperties.TOP);
            Vec3 vec = Utils.fraction(hit.getLocation());

            if (getBlockType() == BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER)
            {
                if ((vec.y > .5) != top)
                {
                    face = Direction.UP;
                }
            }
            else //FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER
            {
                Direction perpDir = face == dir.getClockWise() ? dir : dir.getCounterClockWise();

                double hor = Utils.isX(perpDir) ? vec.x() : vec.z();
                if (!Utils.isPositive(perpDir))
                {
                    hor = 1D - hor;
                }

                double y = vec.y();
                if (top)
                {
                    y = 1D - y;
                }
                y -= .5D;
                if ((y * 2D) >= hor)
                {
                    face = Direction.UP;
                }
            }
        }
        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        if (Utils.isY(face))
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
        }
        else if (rot != Rotation.NONE)
        {
            return state.cycle(FramedProperties.TOP);
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rot)
    {
        return rotate(state, Direction.UP, rot);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        return Utils.mirrorCornerBlock(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedStackedSlopeSlabBlockEntity(pos, state);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        boolean top = state.getValue(FramedProperties.TOP);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        Block topBlock;
        if (getBlockType() == BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER)
        {
            topBlock = FBContent.blockFramedFlatInnerSlopeSlabCorner.get();
        }
        else
        {
            topBlock = FBContent.blockFramedFlatSlopeSlabCorner.get();
        }

        return new Tuple<>(
                FBContent.blockFramedSlab.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.TOP, top),
                topBlock.defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, dir)
                        .setValue(FramedProperties.TOP, top)
                        .setValue(PropertyHolder.TOP_HALF, !top)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }



    public static BlockState itemModelSource()
    {
        return FBContent.blockFramedFlatStackedSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    public static BlockState itemModelSourceInner()
    {
        return FBContent.blockFramedFlatStackedInnerSlopeSlabCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
