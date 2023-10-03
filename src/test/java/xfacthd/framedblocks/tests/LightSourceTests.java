package xfacthd.framedblocks.tests;

import com.google.common.base.Preconditions;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.util.test.TestUtils;
import xfacthd.framedblocks.common.data.BlockType;

import java.util.*;

@GameTestHolder(FramedConstants.MOD_ID)
public final class LightSourceTests
{
    private static final String BATCH_NAME = "lightsource";
    private static final String STRUCTURE_NAME = FramedConstants.MOD_ID + ":lightsourcetests.box";

    @GameTestGenerator
    public static Collection<TestFunction> generateLightSourceTests()
    {
        return Arrays.stream(BlockType.values())
                .filter(LightSourceTests::isNotSelfEmitting)
                .map(type -> Utils.rl(type.getName()))
                .map(ForgeRegistries.BLOCKS::getValue)
                .filter(Objects::nonNull)
                .map(LightSourceTests::getTestState)
                .map(state -> new TestFunction(
                        BATCH_NAME,
                        getTestName(state),
                        STRUCTURE_NAME,
                        100,
                        0,
                        true,
                        helper -> TestUtils.testBlockLightEmission(helper, state, getCamoSides(state.getBlock()))
                ))
                .toList();
    }

    private static boolean isNotSelfEmitting(BlockType type)
    {
        return type != BlockType.FRAMED_TORCH &&
                type != BlockType.FRAMED_WALL_TORCH &&
                type != BlockType.FRAMED_SOUL_TORCH &&
                type != BlockType.FRAMED_SOUL_WALL_TORCH &&
                type != BlockType.FRAMED_REDSTONE_TORCH &&
                type != BlockType.FRAMED_REDSTONE_WALL_TORCH;
    }

    private static BlockState getTestState(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        if (type == BlockType.FRAMED_DOUBLE_STAIRS)
        {
            return block.defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        }
        if (type == BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS)
        {
            return block.defaultBlockState().setValue(FramedProperties.FACING_HOR, Direction.SOUTH);
        }
        return block.defaultBlockState();
    }

    private static String getTestName(BlockState state)
    {
        ResourceLocation regName = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        Preconditions.checkState(regName != null);
        return String.format("lightsourcetests.test_%s", regName.getPath());
    }

    private static List<Direction> getCamoSides(Block block)
    {
        Preconditions.checkArgument(block instanceof IFramedBlock);

        IBlockType type = ((IFramedBlock) block).getBlockType();
        if (!type.isDoubleBlock() || !(type instanceof BlockType))
        {
            return List.of(Direction.UP);
        }

        return switch ((BlockType) type)
        {
            case FRAMED_DIVIDED_SLAB,
                 FRAMED_DOUBLE_PANEL,
                 FRAMED_DOUBLE_SLOPE_PANEL,
                 FRAMED_INV_DOUBLE_SLOPE_PANEL,
                 FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL,
                 FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER,
                 FRAMED_STACKED_SLOPE_PANEL,
                 FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER,
                 FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER,
                 FRAMED_VERTICAL_DOUBLE_HALF_SLOPE -> List.of(Direction.NORTH, Direction.SOUTH);

            case FRAMED_DIVIDED_PANEL_VERTICAL,
                 FRAMED_VERTICAL_DOUBLE_STAIRS,
                 FRAMED_DIVIDED_SLOPE,
                 FRAMED_DIVIDED_STAIRS -> List.of(Direction.EAST, Direction.WEST);

            default -> List.of(Direction.UP, Direction.DOWN);
        };
    }

    private LightSourceTests() { }
}
