package xfacthd.framedblocks.common.block.slopepanel;

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
import xfacthd.framedblocks.common.blockentity.FramedStackedSlopePanelBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public class FramedFlatStackedSlopePanelCornerBlock extends AbstractFramedDoubleBlock
{
    public FramedFlatStackedSlopePanelCornerBlock(BlockType type)
    {
        super(type);
        registerDefaultState(defaultBlockState().setValue(FramedProperties.Y_SLOPE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(FramedProperties.FACING_HOR, PropertyHolder.ROTATION, BlockStateProperties.WATERLOGGED, FramedProperties.Y_SLOPE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        return FramedFlatSlopePanelCornerBlock.getStateForPlacement(this, false, context);
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
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        Direction rotDir = rotation.withFacing(dir);
        Direction perpRotDir = rotation.rotate(Rotation.COUNTERCLOCKWISE_90).withFacing(dir);

        if (face == rotDir || face == perpRotDir)
        {
            if (getBlockType() == BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER)
            {
                double xz = Utils.fractionInDir(hit.getLocation(), dir.getOpposite());
                if (xz > .5)
                {
                    face = dir.getOpposite();
                }
            }
            else //FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER
            {
                Vec3 vec = Utils.fraction(hit.getLocation());

                double hor = Utils.isX(dir) ? vec.x() : vec.z();
                if (!Utils.isPositive(dir))
                {
                    hor = 1D - hor;
                }

                Direction perpDir = face == rotDir ? perpRotDir : rotDir;
                double perpHor = Utils.isY(perpDir) ? vec.y() : (Utils.isX(dir) ? vec.z() : vec.x());
                if (perpDir == Direction.DOWN || (!Utils.isY(perpDir) && !Utils.isPositive(perpDir)))
                {
                    perpHor = 1F - perpHor;
                }
                if ((hor * 2D) < perpHor)
                {
                    face = dir.getOpposite();
                }
            }
        }

        return rotate(state, face, rot);
    }

    @Override
    public BlockState rotate(BlockState state, Direction face, Rotation rot)
    {
        Direction dir = state.getValue(FramedProperties.FACING_HOR);
        if (face.getAxis() == dir.getAxis())
        {
            HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
            return state.setValue(PropertyHolder.ROTATION, rotation.rotate(rot));
        }
        else if (Utils.isY(face))
        {
            return state.setValue(FramedProperties.FACING_HOR, rot.rotate(dir));
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
        return FramedFlatSlopePanelCornerBlock.mirrorCorner(state, mirror);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedStackedSlopePanelBlockEntity(pos, state);
    }

    @Override
    protected Tuple<BlockState, BlockState> getBlockPair(BlockState state)
    {
        Direction facing = state.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        boolean ySlope = state.getValue(FramedProperties.Y_SLOPE);

        Block topBlock;
        if (getBlockType() == BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER)
        {
            topBlock = FBContent.blockFramedFlatInnerSlopePanelCorner.get();
        }
        else
        {
            topBlock = FBContent.blockFramedFlatSlopePanelCorner.get();
        }

        return new Tuple<>(
                FBContent.blockFramedPanel.get()
                        .defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing),
                topBlock.defaultBlockState()
                        .setValue(FramedProperties.FACING_HOR, facing)
                        .setValue(PropertyHolder.ROTATION, rotation)
                        .setValue(PropertyHolder.FRONT, true)
                        .setValue(FramedProperties.Y_SLOPE, ySlope)
        );
    }



    public static BlockState itemModelSource()
    {
        return FBContent.blockFramedFlatStackedSlopePanelCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }

    public static BlockState itemModelSourceInner()
    {
        return FBContent.blockFramedFlatStackedInnerSlopePanelCorner.get()
                .defaultBlockState()
                .setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
    }
}
