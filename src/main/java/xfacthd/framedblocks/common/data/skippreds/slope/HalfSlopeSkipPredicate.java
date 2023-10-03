package xfacthd.framedblocks.common.data.skippreds.slope;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Tuple;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.SideSkipPredicate;
import xfacthd.framedblocks.common.block.AbstractFramedDoubleBlock;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.*;
import xfacthd.framedblocks.common.data.skippreds.HalfDir;
import xfacthd.framedblocks.common.data.skippreds.TriangleDir;
import xfacthd.framedblocks.common.data.skippreds.pillar.CornerPillarSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.PanelSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slab.SlabEdgeSkipPredicate;
import xfacthd.framedblocks.common.data.skippreds.slopepanel.*;
import xfacthd.framedblocks.common.data.skippreds.stairs.*;
import xfacthd.framedblocks.common.util.FramedUtils;

public final class HalfSlopeSkipPredicate implements SideSkipPredicate
{
    @Override
    public boolean test(BlockGetter level, BlockPos pos, BlockState state, BlockState adjState, Direction side)
    {
        if (adjState.getBlock() instanceof IFramedBlock block && block.getBlockType() instanceof BlockType blockType)
        {
            Direction dir = state.getValue(FramedProperties.FACING_HOR);
            boolean top = state.getValue(FramedProperties.TOP);
            boolean right = state.getValue(PropertyHolder.RIGHT);

            return switch (blockType)
            {
                case FRAMED_HALF_SLOPE -> testAgainstHalfSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_HALF_SLOPE -> testAgainstDoubleHalfSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_SLOPE -> testAgainstDividedSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_SLOPED_STAIRS -> testAgainstVerticalSlopedStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_SLOPE,
                     FRAMED_RAIL_SLOPE,
                     FRAMED_POWERED_RAIL_SLOPE,
                     FRAMED_DETECTOR_RAIL_SLOPE,
                     FRAMED_ACTIVATOR_RAIL_SLOPE,
                     FRAMED_FANCY_RAIL_SLOPE,
                     FRAMED_FANCY_POWERED_RAIL_SLOPE,
                     FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
                     FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE -> testAgainstSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE -> testAgainstDoubleSlope(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_CORNER_SLOPE -> testAgainstCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_INNER_CORNER_SLOPE -> testAgainstInnerCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_CORNER -> testAgainstDoubleCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_PRISM_CORNER, FRAMED_THREEWAY_CORNER -> testAgainstThreewayCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_INNER_PRISM_CORNER, FRAMED_INNER_THREEWAY_CORNER -> testAgainstInnerThreewayCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_PRISM_CORNER, FRAMED_DOUBLE_THREEWAY_CORNER -> testAgainstDoubleThreewayCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_SLAB_EDGE -> testAgainstSlabEdge(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_SLAB -> testAgainstDividedSlab(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_HORIZONTAL -> testAgainstDividedPanelHor(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_PANEL -> testAgainstPanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_PANEL -> testAgainstDoublePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_CORNER_PILLAR -> testAgainstCornerPillar(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_PANEL_VERTICAL -> testAgainstDividedPanelVert(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_STAIRS -> testAgainstStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_STAIRS -> testAgainstDoubleStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_STAIRS -> testAgainstVerticalStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_VERTICAL_DOUBLE_STAIRS -> testAgainstVerticalDoubleStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_HALF_STAIRS -> testAgainstHalfStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DIVIDED_STAIRS -> testAgainstDividedStairs(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_SLOPE_PANEL -> testAgainstSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_EXTENDED_SLOPE_PANEL -> testAgainstExtendedSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_DOUBLE_SLOPE_PANEL -> testAgainstDoubleSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_INV_DOUBLE_SLOPE_PANEL -> testAgainstInverseDoubleSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL -> testAgainstExtendedDoubleSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_STACKED_SLOPE_PANEL -> testAgainstStackedSlopePanel(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatInnerSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatDoubleSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatInverseDoubleSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER -> testAgainstFlatExtendedDoubleSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER -> testAgainstFlatStackedSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                case FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER -> testAgainstFlatStackedInnerSlopePanelCorner(
                        level, pos, state, dir, top, right, adjState, side
                );
                default -> false;
            };
        }

        return false;
    }

    private static boolean testAgainstHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getHalfDir(dir, top, right, side).isEqualTo(getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getTriDir(dir, top, right, side).isEqualTo(getTriDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleHalfSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstHalfSlope(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstDividedSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        SlopeType adjType = adjState.getValue(PropertyHolder.SLOPE_TYPE);
        if (adjType == SlopeType.HORIZONTAL) { return false; }

        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfSlope(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstHalfSlope(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstVerticalSlopedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getTriDir(dir, top, right, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getTriDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        else if (getHalfDir(dir, top, right, side).isEqualTo(VerticalSlopedStairsSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = FramedUtils.getSlopeBlockFacing(adjState);
        SlopeType adjType = FramedUtils.getSlopeType(adjState);

        if (getTriDir(dir, top, right, side).isEqualTo(SlopeSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlope(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlope(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlope(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (getTriDir(dir, top, right, side).isEqualTo(CornerSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstInnerCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        CornerType adjType = adjState.getValue(PropertyHolder.CORNER_TYPE);

        if (getTriDir(dir, top, right, side).isEqualTo(InnerCornerSkipPredicate.getTriDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerCorner(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstCorner(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstThreewayCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getTriDir(dir, top, right, side).isEqualTo(ThreewayCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstInnerThreewayCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getTriDir(dir, top, right, side).isEqualTo(InnerThreewayCornerSkipPredicate.getTriDir(adjDir, adjTop, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleThreewayCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstInnerThreewayCorner(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstThreewayCorner(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstSlabEdge(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);

        if (getHalfDir(dir, top, right, side).isEqualTo(SlabEdgeSkipPredicate.getHalfDir(adjDir, adjTop, side.getOpposite())))
        {
            return adjTop != top && SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDividedSlab(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstDividedPanelHor(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlabEdge(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstPanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, top, right, side).isEqualTo(PanelSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, side.getOpposite());
        }

        return false;
    }

    private static boolean testAgainstDoublePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstPanel(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstCornerPillar(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);

        if (getHalfDir(dir, top, right, side).isEqualTo(CornerPillarSkipPredicate.getHalfDir(adjDir, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedPanelVert(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstCornerPillar(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstCornerPillar(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(StairBlock.FACING);
        StairsShape adjShape = adjState.getValue(StairBlock.SHAPE);
        Half adjHalf = adjState.getValue(StairBlock.HALF);

        if (getHalfDir(dir, top, right, side).isEqualTo(StairsSkipPredicate.getHalfDir(adjDir, adjShape, adjHalf, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstStairs(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlabEdge(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstVerticalStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        StairsType adjType = adjState.getValue(PropertyHolder.STAIRS_TYPE);

        if (getHalfDir(dir, top, right, side).isEqualTo(VerticalStairsSkipPredicate.getHalfDir(adjDir, adjType, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstVerticalDoubleStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstVerticalStairs(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstCornerPillar(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstHalfStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        boolean adjTop = adjState.getValue(FramedProperties.TOP);
        boolean adjRight = adjState.getValue(PropertyHolder.RIGHT);

        if (getHalfDir(dir, top, right, side).isEqualTo(HalfStairsSkipPredicate.getHalfDir(adjDir, adjTop, adjRight, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDividedStairs(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstHalfStairs(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstHalfStairs(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, top, right, side).isEqualTo(SlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstExtendedSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, top, right, side).isEqualTo(ExtendedSlopePanelSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstInverseDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstSlopePanel(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstExtendedDoubleSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstExtendedSlopePanel(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstStackedSlopePanel(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstSlopePanel(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstFlatInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);
        boolean adjFront = adjState.getValue(PropertyHolder.FRONT);

        if (getHalfDir(dir, top, right, side).isEqualTo(FlatInnerSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, adjFront, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatExtendedSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Direction adjDir = adjState.getValue(FramedProperties.FACING_HOR);
        HorizontalRotation adjRot = adjState.getValue(PropertyHolder.ROTATION);

        if (getHalfDir(dir, top, right, side).isEqualTo(FlatExtendedSlopePanelCornerSkipPredicate.getHalfDir(adjDir, adjRot, side.getOpposite())))
        {
            return SideSkipPredicate.compareState(level, pos, side, state, adjState);
        }
        return false;
    }

    private static boolean testAgainstFlatDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, top, right, states.getA(), side);
    }

    private static boolean testAgainstFlatInverseDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, top, right, states.getA(), side);
    }

    private static boolean testAgainstFlatExtendedDoubleSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstFlatExtendedSlopePanelCorner(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, top, right, states.getB(), side);
    }

    private static boolean testAgainstFlatStackedSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, top, right, states.getA(), side);
    }

    private static boolean testAgainstFlatStackedInnerSlopePanelCorner(
            BlockGetter level, BlockPos pos, BlockState state, Direction dir, boolean top, boolean right, BlockState adjState, Direction side
    )
    {
        Tuple<BlockState, BlockState> states = AbstractFramedDoubleBlock.getStatePair(adjState);
        return testAgainstPanel(level, pos, state, dir, top, right, states.getA(), side) ||
               testAgainstFlatInnerSlopePanelCorner(level, pos, state, dir, top, right, states.getB(), side);
    }



    public static TriangleDir getTriDir(Direction dir, boolean top, boolean right, Direction side)
    {
        if ((!right && side == dir.getCounterClockWise()) || (right && side == dir.getClockWise()))
        {
            return TriangleDir.fromDirections(
                    dir,
                    top ? Direction.UP : Direction.DOWN
            );
        }
        return TriangleDir.NULL;
    }

    public static HalfDir getHalfDir(Direction dir, boolean top, boolean right, Direction side)
    {
        if (side == dir || (!top && side == Direction.DOWN) || (top && side == Direction.UP))
        {
            return HalfDir.fromDirections(
                    side,
                    right ? dir.getClockWise() : dir.getCounterClockWise()
            );
        }
        return HalfDir.NULL;
    }
}
