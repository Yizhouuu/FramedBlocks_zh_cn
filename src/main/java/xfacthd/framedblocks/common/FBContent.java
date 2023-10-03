package xfacthd.framedblocks.common;

import com.google.common.base.Preconditions;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.*;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.block.FramingSawBlock;
import xfacthd.framedblocks.common.block.cube.*;
import xfacthd.framedblocks.common.block.door.*;
import xfacthd.framedblocks.common.block.interactive.*;
import xfacthd.framedblocks.common.block.pane.*;
import xfacthd.framedblocks.common.block.pillar.*;
import xfacthd.framedblocks.common.block.prism.*;
import xfacthd.framedblocks.common.block.rail.*;
import xfacthd.framedblocks.common.block.slab.*;
import xfacthd.framedblocks.common.block.slope.*;
import xfacthd.framedblocks.common.block.slopepanel.*;
import xfacthd.framedblocks.common.block.slopeslab.*;
import xfacthd.framedblocks.common.block.stairs.*;
import xfacthd.framedblocks.common.block.torch.*;
import xfacthd.framedblocks.common.crafting.FramingSawRecipe;
import xfacthd.framedblocks.common.crafting.FramingSawRecipeSerializer;
import xfacthd.framedblocks.common.data.camo.BlockCamoContainer;
import xfacthd.framedblocks.common.data.camo.FluidCamoContainer;
import xfacthd.framedblocks.common.menu.FramedStorageMenu;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.FramedToolType;
import xfacthd.framedblocks.common.item.FramedBlueprintItem;
import xfacthd.framedblocks.common.item.FramedToolItem;
import xfacthd.framedblocks.common.blockentity.*;
import xfacthd.framedblocks.common.menu.FramingSawMenu;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

public final class FBContent
{
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, FramedConstants.MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, FramedConstants.MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BE_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FramedConstants.MOD_ID);
    private static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, FramedConstants.MOD_ID);
    private static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, FramedConstants.MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, FramedConstants.MOD_ID);

    private static final DeferredRegister<CamoContainer.Factory> CAMO_CONTAINER_FACTORIES = DeferredRegister.create(
            FramedConstants.CAMO_CONTAINER_FACTORY_REGISTRY_NAME,
            FramedConstants.MOD_ID
    );
    public static final Supplier<IForgeRegistry<CamoContainer.Factory>> CAMO_CONTAINER_FACTORY_REGISTRY = CAMO_CONTAINER_FACTORIES.makeRegistry(
            () ->
            {
                RegistryBuilder<CamoContainer.Factory> builder = new RegistryBuilder<>();
                builder.disableOverrides().setDefaultKey(Utils.rl("empty"));
                return builder;
            }
    );

    private static final Map<BlockType, RegistryObject<Block>> BLOCKS_BY_TYPE = new EnumMap<>(BlockType.class);
    private static final Map<FramedToolType, RegistryObject<Item>> TOOLS_BY_TYPE = new EnumMap<>(FramedToolType.class);

    // region Blocks
    public static final RegistryObject<Block> blockFramedCube = registerBlock(FramedCube::new, BlockType.FRAMED_CUBE);
    public static final RegistryObject<Block> blockFramedSlope = registerBlock(FramedSlopeBlock::new, BlockType.FRAMED_SLOPE);
    public static final RegistryObject<Block> blockFramedCornerSlope = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_CORNER_SLOPE);
    public static final RegistryObject<Block> blockFramedInnerCornerSlope = registerBlock(FramedCornerSlopeBlock::new, BlockType.FRAMED_INNER_CORNER_SLOPE);
    public static final RegistryObject<Block> blockFramedPrismCorner = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_PRISM_CORNER);
    public static final RegistryObject<Block> blockFramedInnerPrismCorner = registerBlock(FramedPrismCornerBlock::new, BlockType.FRAMED_INNER_PRISM_CORNER);
    public static final RegistryObject<Block> blockFramedThreewayCorner = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_THREEWAY_CORNER);
    public static final RegistryObject<Block> blockFramedInnerThreewayCorner = registerBlock(FramedThreewayCornerBlock::new, BlockType.FRAMED_INNER_THREEWAY_CORNER);
    public static final RegistryObject<Block> blockFramedSlab = registerBlock(FramedSlabBlock::new, BlockType.FRAMED_SLAB);
    public static final RegistryObject<Block> blockFramedSlabEdge = registerBlock(FramedSlabEdgeBlock::new, BlockType.FRAMED_SLAB_EDGE);
    public static final RegistryObject<Block> blockFramedSlabCorner = registerBlock(FramedSlabCornerBlock::new, BlockType.FRAMED_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedDividedSlab = registerBlock(FramedDividedSlabBlock::new, BlockType.FRAMED_DIVIDED_SLAB);
    public static final RegistryObject<Block> blockFramedPanel = registerBlock(FramedPanelBlock::new, BlockType.FRAMED_PANEL);
    public static final RegistryObject<Block> blockFramedCornerPillar = registerBlock(FramedCornerPillarBlock::new, BlockType.FRAMED_CORNER_PILLAR);
    public static final RegistryObject<Block> blockFramedDividedPanelHor = registerBlock(FramedDividedPanelBlock::new, BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL);
    public static final RegistryObject<Block> blockFramedDividedPanelVert = registerBlock(FramedDividedPanelBlock::new, BlockType.FRAMED_DIVIDED_PANEL_VERTICAL);
    public static final RegistryObject<Block> blockFramedStairs = registerBlock(FramedStairsBlock::new, BlockType.FRAMED_STAIRS);
    public static final RegistryObject<Block> blockFramedWall = registerBlock(FramedWallBlock::new, BlockType.FRAMED_WALL);
    public static final RegistryObject<Block> blockFramedFence = registerBlock(FramedFenceBlock::new, BlockType.FRAMED_FENCE);
    public static final RegistryObject<Block> blockFramedFenceGate = registerBlock(FramedFenceGateBlock::new, BlockType.FRAMED_GATE);
    public static final RegistryObject<Block> blockFramedDoor = registerBlock(FramedDoorBlock::wood, BlockType.FRAMED_DOOR);
    public static final RegistryObject<Block> blockFramedIronDoor = registerBlock(FramedDoorBlock::iron, BlockType.FRAMED_IRON_DOOR);
    public static final RegistryObject<Block> blockFramedTrapDoor = registerBlock(FramedTrapDoorBlock::wood, BlockType.FRAMED_TRAPDOOR);
    public static final RegistryObject<Block> blockFramedIronTrapDoor = registerBlock(FramedTrapDoorBlock::iron, BlockType.FRAMED_IRON_TRAPDOOR);
    public static final RegistryObject<Block> blockFramedPressurePlate = registerBlock(FramedPressurePlateBlock::wood, BlockType.FRAMED_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedWaterloggablePressurePlate = registerBlock(FramedPressurePlateBlock::woodWaterloggable, BlockType.FRAMED_WATERLOGGABLE_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedStonePressurePlate = registerBlock(FramedPressurePlateBlock::stone, BlockType.FRAMED_STONE_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedWaterloggableStonePressurePlate = registerBlock(FramedPressurePlateBlock::stoneWaterloggable, BlockType.FRAMED_WATERLOGGABLE_STONE_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedObsidianPressurePlate = registerBlock(FramedPressurePlateBlock::obsidian, BlockType.FRAMED_OBSIDIAN_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedWaterloggableObsidianPressurePlate = registerBlock(FramedPressurePlateBlock::obsidianWaterloggable, BlockType.FRAMED_WATERLOGGABLE_OBSIDIAN_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedGoldPressurePlate = registerBlock(FramedWeightedPressurePlateBlock::gold, BlockType.FRAMED_GOLD_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedWaterloggableGoldPressurePlate = registerBlock(FramedWeightedPressurePlateBlock::goldWaterloggable, BlockType.FRAMED_WATERLOGGABLE_GOLD_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedIronPressurePlate = registerBlock(FramedWeightedPressurePlateBlock::iron, BlockType.FRAMED_IRON_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedWaterloggableIronPressurePlate = registerBlock(FramedWeightedPressurePlateBlock::ironWaterloggable, BlockType.FRAMED_WATERLOGGABLE_IRON_PRESSURE_PLATE);
    public static final RegistryObject<Block> blockFramedLadder = registerBlock(FramedLadderBlock::new, BlockType.FRAMED_LADDER);
    public static final RegistryObject<Block> blockFramedButton = registerBlock(FramedButtonBlock::new, BlockType.FRAMED_BUTTON);
    public static final RegistryObject<Block> blockFramedStoneButton = registerBlock(FramedStoneButtonBlock::new, BlockType.FRAMED_STONE_BUTTON);
    public static final RegistryObject<Block> blockFramedLever = registerBlock(FramedLeverBlock::new, BlockType.FRAMED_LEVER);
    public static final RegistryObject<Block> blockFramedSign = registerBlock(FramedSignBlock::new, BlockType.FRAMED_SIGN);
    public static final RegistryObject<Block> blockFramedWallSign = registerBlock(FramedWallSignBlock::new, BlockType.FRAMED_WALL_SIGN);
    public static final RegistryObject<Block> blockFramedDoubleSlab = registerBlock(FramedDoubleSlabBlock::new, BlockType.FRAMED_DOUBLE_SLAB);
    public static final RegistryObject<Block> blockFramedDoublePanel = registerBlock(FramedDoublePanelBlock::new, BlockType.FRAMED_DOUBLE_PANEL);
    public static final RegistryObject<Block> blockFramedDoubleSlope = registerBlock(FramedDoubleSlopeBlock::new, BlockType.FRAMED_DOUBLE_SLOPE);
    public static final RegistryObject<Block> blockFramedDoubleCorner = registerBlock(FramedDoubleCornerBlock::new, BlockType.FRAMED_DOUBLE_CORNER);
    public static final RegistryObject<Block> blockFramedDoublePrismCorner = registerBlock(FramedDoublePrismCornerBlock::new, BlockType.FRAMED_DOUBLE_PRISM_CORNER);
    public static final RegistryObject<Block> blockFramedDoubleThreewayCorner = registerBlock(FramedDoubleThreewayCornerBlock::new, BlockType.FRAMED_DOUBLE_THREEWAY_CORNER);
    public static final RegistryObject<Block> blockFramedTorch = registerBlock(FramedTorchBlock::new, BlockType.FRAMED_TORCH);
    public static final RegistryObject<Block> blockFramedWallTorch = registerBlock(FramedWallTorchBlock::new, BlockType.FRAMED_WALL_TORCH);
    public static final RegistryObject<Block> blockFramedSoulTorch = registerBlock(FramedSoulTorchBlock::new, BlockType.FRAMED_SOUL_TORCH);
    public static final RegistryObject<Block> blockFramedSoulWallTorch = registerBlock(FramedSoulWallTorchBlock::new, BlockType.FRAMED_SOUL_WALL_TORCH);
    public static final RegistryObject<Block> blockFramedRedstoneTorch = registerBlock(FramedRedstoneTorchBlock::new, BlockType.FRAMED_REDSTONE_TORCH);
    public static final RegistryObject<Block> blockFramedRedstoneWallTorch = registerBlock(FramedRedstoneWallTorchBlock::new, BlockType.FRAMED_REDSTONE_WALL_TORCH);
    public static final RegistryObject<Block> blockFramedFloor = registerBlock(FramedFloorBlock::new, BlockType.FRAMED_FLOOR_BOARD);
    public static final RegistryObject<Block> blockFramedLattice = registerBlock(FramedLatticeBlock::new, BlockType.FRAMED_LATTICE_BLOCK);
    public static final RegistryObject<Block> blockFramedVerticalStairs = registerBlock(FramedVerticalStairsBlock::new, BlockType.FRAMED_VERTICAL_STAIRS);
    public static final RegistryObject<Block> blockFramedChest = registerBlock(FramedChestBlock::new, BlockType.FRAMED_CHEST);
    public static final RegistryObject<Block> blockFramedBars = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_BARS);
    public static final RegistryObject<Block> blockFramedPane = registerBlock(FramedPaneBlock::new, BlockType.FRAMED_PANE);
    public static final RegistryObject<Block> blockFramedRailSlope = registerBlock(FramedRailSlopeBlock::normal, BlockType.FRAMED_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedPoweredRailSlope = registerBlock(FramedPoweredRailSlopeBlock::powered, BlockType.FRAMED_POWERED_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedDetectorRailSlope = registerBlock(FramedDetectorRailSlopeBlock::normal, BlockType.FRAMED_DETECTOR_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedActivatorRailSlope = registerBlock(FramedPoweredRailSlopeBlock::activator, BlockType.FRAMED_ACTIVATOR_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedFlowerPot = registerBlock(FramedFlowerPotBlock::new, BlockType.FRAMED_FLOWER_POT);
    public static final RegistryObject<Block> blockFramedPillar = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_PILLAR);
    public static final RegistryObject<Block> blockFramedHalfPillar = registerBlock(FramedHalfPillarBlock::new, BlockType.FRAMED_HALF_PILLAR);
    public static final RegistryObject<Block> blockFramedPost = registerBlock(FramedPillarBlock::new, BlockType.FRAMED_POST);
    public static final RegistryObject<Block> blockFramedCollapsibleBlock = registerBlock(FramedCollapsibleBlock::new, BlockType.FRAMED_COLLAPSIBLE_BLOCK);
    public static final RegistryObject<Block> blockFramedHalfStairs = registerBlock(FramedHalfStairsBlock::new, BlockType.FRAMED_HALF_STAIRS);
    public static final RegistryObject<Block> blockFramedDividedStairs = registerBlock(FramedDividedStairsBlock::new, BlockType.FRAMED_DIVIDED_STAIRS);
    public static final RegistryObject<Block> blockFramedBouncyCube = registerBlock(FramedBouncyCubeBlock::new, BlockType.FRAMED_BOUNCY_CUBE);
    public static final RegistryObject<Block> blockFramedSecretStorage = registerBlock(FramedStorageBlock::new, BlockType.FRAMED_SECRET_STORAGE);
    public static final RegistryObject<Block> blockFramedRedstoneBlock = registerBlock(FramedRedstoneBlock::new, BlockType.FRAMED_REDSTONE_BLOCK);
    public static final RegistryObject<Block> blockFramedPrism = registerBlock(FramedPrismBlock::new, BlockType.FRAMED_PRISM);
    public static final RegistryObject<Block> blockFramedInnerPrism = registerBlock(FramedPrismBlock::new, BlockType.FRAMED_INNER_PRISM);
    public static final RegistryObject<Block> blockFramedDoublePrism = registerBlock(FramedDoublePrismBlock::new, BlockType.FRAMED_DOUBLE_PRISM);
    public static final RegistryObject<Block> blockFramedSlopedPrism = registerBlock(FramedSlopedPrismBlock::new, BlockType.FRAMED_SLOPED_PRISM);
    public static final RegistryObject<Block> blockFramedInnerSlopedPrism = registerBlock(FramedSlopedPrismBlock::new, BlockType.FRAMED_INNER_SLOPED_PRISM);
    public static final RegistryObject<Block> blockFramedDoubleSlopedPrism = registerBlock(FramedDoubleSlopedPrismBlock::new, BlockType.FRAMED_DOUBLE_SLOPED_PRISM);
    public static final RegistryObject<Block> blockFramedSlopeSlab = registerBlock(FramedSlopeSlabBlock::new, BlockType.FRAMED_SLOPE_SLAB);
    public static final RegistryObject<Block> blockFramedElevatedSlopeSlab = registerBlock(FramedElevatedSlopeSlabBlock::new, BlockType.FRAMED_ELEVATED_SLOPE_SLAB);
    public static final RegistryObject<Block> blockFramedDoubleSlopeSlab = registerBlock(FramedDoubleSlopeSlabBlock::new, BlockType.FRAMED_DOUBLE_SLOPE_SLAB);
    public static final RegistryObject<Block> blockFramedInverseDoubleSlopeSlab = registerBlock(FramedInverseDoubleSlopeSlabBlock::new, BlockType.FRAMED_INV_DOUBLE_SLOPE_SLAB);
    public static final RegistryObject<Block> blockFramedElevatedDoubleSlopeSlab = registerBlock(FramedElevatedDoubleSlopeSlabBlock::new, BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB);
    public static final RegistryObject<Block> blockFramedStackedSlopeSlab = registerBlock(FramedStackedSlopeSlabBlock::new, BlockType.FRAMED_STACKED_SLOPE_SLAB);
    public static final RegistryObject<Block> blockFramedFlatSlopeSlabCorner = registerBlock(FramedFlatSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatInnerSlopeSlabCorner = registerBlock(FramedFlatSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_INNER_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatElevatedSlopeSlabCorner = registerBlock(FramedFlatElevatedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatElevatedInnerSlopeSlabCorner = registerBlock(FramedFlatElevatedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_INNER_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatDoubleSlopeSlabCorner = registerBlock(FramedFlatDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatInverseDoubleSlopeSlabCorner = registerBlock(FramedFlatInverseDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatElevatedDoubleSlopeSlabCorner = registerBlock(FramedFlatElevatedDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatElevatedInnerDoubleSlopeSlabCorner = registerBlock(FramedFlatElevatedDoubleSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatStackedSlopeSlabCorner = registerBlock(FramedFlatStackedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedFlatStackedInnerSlopeSlabCorner = registerBlock(FramedFlatStackedSlopeSlabCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER);
    public static final RegistryObject<Block> blockFramedVerticalHalfStairs = registerBlock(FramedVerticalHalfStairsBlock::new, BlockType.FRAMED_VERTICAL_HALF_STAIRS);
    public static final RegistryObject<Block> blockFramedVerticalDividedStairs = registerBlock(FramedVerticalDividedStairsBlock::new, BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS);
    public static final RegistryObject<Block> blockFramedSlopePanel = registerBlock(FramedSlopePanelBlock::new, BlockType.FRAMED_SLOPE_PANEL);
    public static final RegistryObject<Block> blockFramedExtendedSlopePanel = registerBlock(FramedExtendedSlopePanelBlock::new, BlockType.FRAMED_EXTENDED_SLOPE_PANEL);
    public static final RegistryObject<Block> blockFramedDoubleSlopePanel = registerBlock(FramedDoubleSlopePanelBlock::new, BlockType.FRAMED_DOUBLE_SLOPE_PANEL);
    public static final RegistryObject<Block> blockFramedInverseDoubleSlopePanel = registerBlock(FramedInverseDoubleSlopePanelBlock::new, BlockType.FRAMED_INV_DOUBLE_SLOPE_PANEL);
    public static final RegistryObject<Block> blockFramedExtendedDoubleSlopePanel = registerBlock(FramedExtendedDoubleSlopePanelBlock::new, BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL);
    public static final RegistryObject<Block> blockFramedStackedSlopePanel = registerBlock(FramedStackedSlopePanelBlock::new, BlockType.FRAMED_STACKED_SLOPE_PANEL);
    public static final RegistryObject<Block> blockFramedFlatSlopePanelCorner = registerBlock(FramedFlatSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatInnerSlopePanelCorner = registerBlock(FramedFlatSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_INNER_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatExtendedSlopePanelCorner = registerBlock(FramedFlatExtendedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatExtendedInnerSlopePanelCorner = registerBlock(FramedFlatExtendedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_INNER_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatDoubleSlopePanelCorner = registerBlock(FramedFlatDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatInverseDoubleSlopePanelCorner = registerBlock(FramedFlatInverseDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatExtendedDoubleSlopePanelCorner = registerBlock(FramedFlatExtendedDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatExtendedInnerDoubleSlopePanelCorner = registerBlock(FramedFlatExtendedDoubleSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatStackedSlopePanelCorner = registerBlock(FramedFlatStackedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedFlatStackedInnerSlopePanelCorner = registerBlock(FramedFlatStackedSlopePanelCornerBlock::new, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER);
    public static final RegistryObject<Block> blockFramedDoubleStairs = registerBlock(FramedDoubleStairsBlock::new, BlockType.FRAMED_DOUBLE_STAIRS);
    public static final RegistryObject<Block> blockFramedVerticalDoubleStairs = registerBlock(FramedVerticalDoubleStairsBlock::new, BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS);
    public static final RegistryObject<Block> blockFramedWallBoard = registerBlock(FramedWallBoardBlock::new, BlockType.FRAMED_WALL_BOARD);
    public static final RegistryObject<Block> blockFramedGlowingCube = registerBlock(FramedGlowingCube::new, BlockType.FRAMED_GLOWING_CUBE);
    public static final RegistryObject<Block> blockFramedPyramid = registerBlock(FramedPyramidBlock::new, BlockType.FRAMED_PYRAMID);
    public static final RegistryObject<Block> blockFramedPyramidSlab = registerBlock(FramedPyramidBlock::new, BlockType.FRAMED_PYRAMID_SLAB);
    public static final RegistryObject<Block> blockFramedHorizontalPane = registerBlock(FramedHorizontalPaneBlock::new, BlockType.FRAMED_HORIZONTAL_PANE);
    public static final RegistryObject<Block> blockFramedLargeButton = registerBlock(FramedLargeButtonBlock::new, BlockType.FRAMED_LARGE_BUTTON);
    public static final RegistryObject<Block> blockFramedLargeStoneButton = registerBlock(FramedLargeStoneButtonBlock::new, BlockType.FRAMED_LARGE_STONE_BUTTON);
    public static final RegistryObject<Block> blockFramedTarget = registerBlock(FramedTargetBlock::new, BlockType.FRAMED_TARGET);
    public static final RegistryObject<Block> blockFramedGate = registerBlock(FramedGateBlock::wood, BlockType.FRAMED_GATE_DOOR);
    public static final RegistryObject<Block> blockFramedIronGate = registerBlock(FramedGateBlock::iron, BlockType.FRAMED_IRON_GATE_DOOR);
    public static final RegistryObject<Block> blockFramedItemFrame = registerBlock(FramedItemFrameBlock::new, BlockType.FRAMED_ITEM_FRAME);
    public static final RegistryObject<Block> blockFramedGlowingItemFrame = registerBlock(FramedItemFrameBlock::new, BlockType.FRAMED_GLOWING_ITEM_FRAME);
    public static final RegistryObject<Block> blockFramedFancyRail = registerBlock(FramedFancyRailBlock::new, BlockType.FRAMED_FANCY_RAIL);
    public static final RegistryObject<Block> blockFramedFancyPoweredRail = registerBlock(FramedFancyPoweredRailBlock::powered, BlockType.FRAMED_FANCY_POWERED_RAIL);
    public static final RegistryObject<Block> blockFramedFancyDetectorRail = registerBlock(FramedFancyDetectorRailBlock::new, BlockType.FRAMED_FANCY_DETECTOR_RAIL);
    public static final RegistryObject<Block> blockFramedFancyActivatorRail = registerBlock(FramedFancyPoweredRailBlock::activator, BlockType.FRAMED_FANCY_ACTIVATOR_RAIL);
    public static final RegistryObject<Block> blockFramedFancyRailSlope = registerBlock(FramedRailSlopeBlock::fancy, BlockType.FRAMED_FANCY_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedFancyPoweredRailSlope = registerBlock(FramedPoweredRailSlopeBlock::poweredFancy, BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedFancyDetectorRailSlope = registerBlock(FramedDetectorRailSlopeBlock::fancy, BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedFancyActivatorRailSlope = registerBlock(FramedPoweredRailSlopeBlock::activatorFancy, BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE);
    public static final RegistryObject<Block> blockFramedHalfSlope = registerBlock(FramedHalfSlopeBlock::new, BlockType.FRAMED_HALF_SLOPE);
    public static final RegistryObject<Block> blockFramedVerticalHalfSlope = registerBlock(FramedVerticalHalfSlopeBlock::new, BlockType.FRAMED_VERTICAL_HALF_SLOPE);
    public static final RegistryObject<Block> blockFramedDividedSlope = registerBlock(FramedDividedSlopeBlock::new, BlockType.FRAMED_DIVIDED_SLOPE);
    public static final RegistryObject<Block> blockFramedDoubleHalfSlope = registerBlock(FramedDoubleHalfSlopeBlock::new, BlockType.FRAMED_DOUBLE_HALF_SLOPE);
    public static final RegistryObject<Block> blockFramedVerticalDoubleHalfSlope = registerBlock(FramedVerticalDoubleHalfSlopeBlock::new, BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE);
    public static final RegistryObject<Block> blockFramedSlopedStairs = registerBlock(FramedSlopedStairsBlock::new, BlockType.FRAMED_SLOPED_STAIRS);
    public static final RegistryObject<Block> blockFramedVerticalSlopedStairs = registerBlock(FramedVerticalSlopedStairsBlock::new, BlockType.FRAMED_VERTICAL_SLOPED_STAIRS);
    public static final RegistryObject<Block> blockFramedMiniCube = registerBlock(FramedMiniCubeBlock::new, BlockType.FRAMED_MINI_CUBE);
    public static final RegistryObject<Block> blockFramedOneWayWindow = registerBlock(FramedOneWayWindowBlock::new, BlockType.FRAMED_ONE_WAY_WINDOW);
    // endregion

    // region Special Blocks
    public static final RegistryObject<Block> blockFramingSaw = registerBlock("framing_saw", FramingSawBlock::new);
    // endregion

    // region Items
    public static final RegistryObject<Item> itemFramedHammer = registerToolItem(FramedToolItem::new, FramedToolType.HAMMER);
    public static final RegistryObject<Item> itemFramedWrench = registerToolItem(FramedToolItem::new, FramedToolType.WRENCH);
    public static final RegistryObject<Item> itemFramedBlueprint = registerToolItem(FramedBlueprintItem::new, FramedToolType.BLUEPRINT);
    public static final RegistryObject<Item> itemFramedKey = registerToolItem(FramedToolItem::new, FramedToolType.KEY);
    public static final RegistryObject<Item> itemFramedScrewdriver = registerToolItem(FramedToolItem::new, FramedToolType.SCREWDRIVER);
    public static final RegistryObject<Item> itemFramedReinforcement = ITEMS.register("framed_reinforcement", () ->
            new Item(new Item.Properties().tab(FramedBlocks.FRAMED_TAB))
    );
    // endregion

    // region BlockEntityTypes
    public static final RegistryObject<BlockEntityType<FramedBlockEntity>> blockEntityTypeFramedBlock = createBlockEntityType(
            FramedBlockEntity::new,
            "framed_tile",
            getDefaultEntityBlocks()
    );
    public static final RegistryObject<BlockEntityType<FramedDividedSlabBlockEntity>> blockEntityTypeFramedDividedSlab = createBlockEntityType(
            FramedDividedSlabBlockEntity::new,
            BlockType.FRAMED_DIVIDED_SLAB
    );
    public static final RegistryObject<BlockEntityType<FramedDividedPanelBlockEntity>> blockEntityTypeFramedDividedPanel = createBlockEntityType(
            FramedDividedPanelBlockEntity::new,
            BlockType.FRAMED_DIVIDED_PANEL_HORIZONTAL, BlockType.FRAMED_DIVIDED_PANEL_VERTICAL
    );
    public static final RegistryObject<BlockEntityType<FramedSignBlockEntity>> blockEntityTypeFramedSign = createBlockEntityType(
            FramedSignBlockEntity::new,
            BlockType.FRAMED_SIGN, BlockType.FRAMED_WALL_SIGN
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleSlabBlockEntity>> blockEntityTypeDoubleFramedSlab = createBlockEntityType(
            FramedDoubleSlabBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLAB
    );
    public static final RegistryObject<BlockEntityType<FramedDoublePanelBlockEntity>> blockEntityTypeDoubleFramedPanel = createBlockEntityType(
            FramedDoublePanelBlockEntity::new,
            BlockType.FRAMED_DOUBLE_PANEL
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleSlopeBlockEntity>> blockEntityTypeDoubleFramedSlope = createBlockEntityType(
            FramedDoubleSlopeBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleCornerBlockEntity>> blockEntityTypeDoubleFramedCorner = createBlockEntityType(
            FramedDoubleCornerBlockEntity::new,
            BlockType.FRAMED_DOUBLE_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleThreewayCornerBlockEntity>> blockEntityTypeDoubleFramedThreewayCorner = createBlockEntityType(
            FramedDoubleThreewayCornerBlockEntity::new,
            BlockType.FRAMED_DOUBLE_THREEWAY_CORNER, BlockType.FRAMED_DOUBLE_PRISM_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedChestBlockEntity>> blockEntityTypeFramedChest = createBlockEntityType(
            FramedChestBlockEntity::new,
            BlockType.FRAMED_CHEST
    );
    public static final RegistryObject<BlockEntityType<FramedFlowerPotBlockEntity>> blockEntityTypeFramedFlowerPot = createBlockEntityType(
            FramedFlowerPotBlockEntity::new,
            BlockType.FRAMED_FLOWER_POT
    );
    public static final RegistryObject<BlockEntityType<FramedCollapsibleBlockEntity>> blockEntityTypeFramedCollapsibleBlock = createBlockEntityType(
            FramedCollapsibleBlockEntity::new,
            BlockType.FRAMED_COLLAPSIBLE_BLOCK
    );
    public static final RegistryObject<BlockEntityType<FramedDividedStairsBlockEntity>> blockEntityTypeFramedDividedStairs = createBlockEntityType(
            FramedDividedStairsBlockEntity::new,
            BlockType.FRAMED_DIVIDED_STAIRS
    );
    public static final RegistryObject<BlockEntityType<FramedStorageBlockEntity>> blockEntityTypeFramedSecretStorage = createBlockEntityType(
            FramedStorageBlockEntity::new,
            BlockType.FRAMED_SECRET_STORAGE
    );
    public static final RegistryObject<BlockEntityType<FramedDoublePrismBlockEntity>> blockEntityTypeFramedDoublePrism = createBlockEntityType(
            FramedDoublePrismBlockEntity::new,
            BlockType.FRAMED_DOUBLE_PRISM
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleSlopedPrismBlockEntity>> blockEntityTypeFramedDoubleSlopedPrism = createBlockEntityType(
            FramedDoubleSlopedPrismBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPED_PRISM
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleSlopeSlabBlockEntity>> blockEntityTypeFramedDoubleSlopeSlab = createBlockEntityType(
            FramedDoubleSlopeSlabBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE_SLAB
    );
    public static final RegistryObject<BlockEntityType<FramedInverseDoubleSlopeSlabBlockEntity>> blockEntityTypeFramedInverseDoubleSlopeSlab = createBlockEntityType(
            FramedInverseDoubleSlopeSlabBlockEntity::new,
            "framed_inverse_double_slope_slab",
            () -> new Block[] { blockFramedInverseDoubleSlopeSlab.get() }
    );
    public static final RegistryObject<BlockEntityType<FramedElevatedDoubleSlopeSlabBlockEntity>> blockEntityTypeFramedElevatedDoubleSlopeSlab = createBlockEntityType(
            FramedElevatedDoubleSlopeSlabBlockEntity::new,
            BlockType.FRAMED_ELEVATED_DOUBLE_SLOPE_SLAB
    );
    public static final RegistryObject<BlockEntityType<FramedStackedSlopeSlabBlockEntity>> blockEntityTypeFramedStackedSlopeSlab = createBlockEntityType(
            FramedStackedSlopeSlabBlockEntity::new,
            BlockType.FRAMED_STACKED_SLOPE_SLAB, BlockType.FRAMED_FLAT_STACKED_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_SLAB_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedVerticalDividedStairsBlockEntity>> blockEntityTypeFramedVerticalDividedStairs = createBlockEntityType(
            FramedVerticalDividedStairsBlockEntity::new,
            BlockType.FRAMED_VERTICAL_DIVIDED_STAIRS
    );
    public static final RegistryObject<BlockEntityType<FramedFlatDoubleSlopeSlabCornerBlockEntity>> blockEntityTypeFramedFlatDoubleSlopeSlabCorner = createBlockEntityType(
            FramedFlatDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedFlatInverseDoubleSlopeSlabCornerBlockEntity>> blockEntityTypeFramedFlatInverseDoubleSlopeSlabCorner = createBlockEntityType(
            FramedFlatInverseDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity>> blockEntityTypeFramedFlatElevatedDoubleSlopeSlabCorner = createBlockEntityType(
            FramedFlatElevatedDoubleSlopeSlabCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_ELEV_DOUBLE_SLOPE_SLAB_CORNER, BlockType.FRAMED_FLAT_ELEV_INNER_DOUBLE_SLOPE_SLAB_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleSlopePanelBlockEntity>> blockEntityTypeFramedDoubleSlopePanel = createBlockEntityType(
            FramedDoubleSlopePanelBlockEntity::new,
            BlockType.FRAMED_DOUBLE_SLOPE_PANEL
    );
    public static final RegistryObject<BlockEntityType<FramedInverseDoubleSlopePanelBlockEntity>> blockEntityTypeFramedInverseDoubleSlopePanel = createBlockEntityType(
            FramedInverseDoubleSlopePanelBlockEntity::new,
            "framed_inverse_double_slope_panel",
            () -> new Block[] { blockFramedInverseDoubleSlopePanel.get() }
    );
    public static final RegistryObject<BlockEntityType<FramedExtendedDoubleSlopePanelBlockEntity>> blockEntityTypeFramedExtendedDoubleSlopePanel = createBlockEntityType(
            FramedExtendedDoubleSlopePanelBlockEntity::new,
            BlockType.FRAMED_EXTENDED_DOUBLE_SLOPE_PANEL
    );
    public static final RegistryObject<BlockEntityType<FramedStackedSlopePanelBlockEntity>> blockEntityTypeFramedStackedSlopePanel = createBlockEntityType(
            FramedStackedSlopePanelBlockEntity::new,
            BlockType.FRAMED_STACKED_SLOPE_PANEL, BlockType.FRAMED_FLAT_STACKED_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_STACKED_INNER_SLOPE_PANEL_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedFlatDoubleSlopePanelCornerBlockEntity>> blockEntityTypeFramedFlatDoubleSlopePanelCorner = createBlockEntityType(
            FramedFlatDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedFlatInverseDoubleSlopePanelCornerBlockEntity>> blockEntityTypeFramedFlatInverseDoubleSlopePanelCorner = createBlockEntityType(
            FramedFlatInverseDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_INV_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedFlatExtendedDoubleSlopePanelCornerBlockEntity>> blockEntityTypeFramedFlatExtendedDoubleSlopePanelCorner = createBlockEntityType(
            FramedFlatExtendedDoubleSlopePanelCornerBlockEntity::new,
            BlockType.FRAMED_FLAT_EXT_DOUBLE_SLOPE_PANEL_CORNER, BlockType.FRAMED_FLAT_EXT_INNER_DOUBLE_SLOPE_PANEL_CORNER
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleStairsBlockEntity>> blockEntityTypeFramedDoubleStairs = createBlockEntityType(
            FramedDoubleStairsBlockEntity::new,
            BlockType.FRAMED_DOUBLE_STAIRS
    );
    public static final RegistryObject<BlockEntityType<FramedVerticalDoubleStairsBlockEntity>> blockEntityTypeFramedVerticalDoubleStairs = createBlockEntityType(
            FramedVerticalDoubleStairsBlockEntity::new,
            BlockType.FRAMED_VERTICAL_DOUBLE_STAIRS
    );
    public static final RegistryObject<BlockEntityType<FramedTargetBlockEntity>> blockEntityTypeFramedTarget = createBlockEntityType(
            FramedTargetBlockEntity::new,
            BlockType.FRAMED_TARGET
    );
    public static final RegistryObject<BlockEntityType<FramedItemFrameBlockEntity>> blockEntityTypeFramedItemFrame = createBlockEntityType(
            FramedItemFrameBlockEntity::new,
            BlockType.FRAMED_ITEM_FRAME, BlockType.FRAMED_GLOWING_ITEM_FRAME
    );
    public static final RegistryObject<BlockEntityType<FramedFancyRailSlopeBlockEntity>> blockEntityTypeFramedFancyRailSlope = createBlockEntityType(
            FramedFancyRailSlopeBlockEntity::new,
            BlockType.FRAMED_FANCY_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_POWERED_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_DETECTOR_RAIL_SLOPE,
            BlockType.FRAMED_FANCY_ACTIVATOR_RAIL_SLOPE
    );
    public static final RegistryObject<BlockEntityType<FramedDividedSlopeBlockEntity>> blockEntityTypeFramedDividedSlope = createBlockEntityType(
            FramedDividedSlopeBlockEntity::new,
            BlockType.FRAMED_DIVIDED_SLOPE
    );
    public static final RegistryObject<BlockEntityType<FramedDoubleHalfSlopeBlockEntity>> blockEntityTypeFramedDoubleHalfSlope = createBlockEntityType(
            FramedDoubleHalfSlopeBlockEntity::new,
            BlockType.FRAMED_DOUBLE_HALF_SLOPE
    );
    public static final RegistryObject<BlockEntityType<FramedVerticalDoubleHalfSlopeBlockEntity>> blockEntityTypeFramedVerticalDoubleHalfSlope = createBlockEntityType(
            FramedVerticalDoubleHalfSlopeBlockEntity::new,
            BlockType.FRAMED_VERTICAL_DOUBLE_HALF_SLOPE
    );
    public static final RegistryObject<BlockEntityType<FramedOwnableBlockEntity>> blockEntityTypeFramedOwnableBlock = createBlockEntityType(
            FramedOwnableBlockEntity::new,
            BlockType.FRAMED_ONE_WAY_WINDOW
    );
    // endregion

    // region MenuTypes
    public static final RegistryObject<MenuType<FramedStorageMenu>> menuTypeFramedStorage = createMenuType(FramedStorageMenu::new, "framed_chest");
    public static final RegistryObject<MenuType<FramingSawMenu>> menuTypeFramingSaw = createMenuType(
            (id, inv, buf) -> new FramingSawMenu(id, inv, ContainerLevelAccess.NULL),
            "framing_saw"
    );
    // endregion

    // region RecipeTypes
    public static final RegistryObject<RecipeType<FramingSawRecipe>> recipeTypeFramingSawRecipe = createRecipeType("frame");
    // endregion

    // region RecipeSerializers
    public static final RegistryObject<RecipeSerializer<FramingSawRecipe>> recipeSerializerFramingSawRecipe = RECIPE_SERIALIZERS.register(
            "frame",
            FramingSawRecipeSerializer::new
    );
    // endregion

    // region CamoContainer.Factories
    public static final RegistryObject<CamoContainer.Factory> factoryEmpty = CAMO_CONTAINER_FACTORIES.register(
            "empty",
            EmptyCamoContainer.Factory::new
    );
    public static final RegistryObject<CamoContainer.Factory> factoryBlock = CAMO_CONTAINER_FACTORIES.register(
            "block",
            BlockCamoContainer.Factory::new
    );
    public static final RegistryObject<CamoContainer.Factory> factoryFluid = CAMO_CONTAINER_FACTORIES.register(
            "fluid",
            FluidCamoContainer.Factory::new
    );
    // endregion



    public static void init(IEventBus modBus)
    {
        BLOCKS.register(modBus);
        ITEMS.register(modBus);
        BE_TYPES.register(modBus);
        CONTAINER_TYPES.register(modBus);
        RECIPE_TYPES.register(modBus);
        RECIPE_SERIALIZERS.register(modBus);
        CAMO_CONTAINER_FACTORIES.register(modBus);
    }

    public static Collection<RegistryObject<Block>> getRegisteredBlocks() { return BLOCKS.getEntries(); }

    public static Block byType(BlockType type) { return BLOCKS_BY_TYPE.get(type).get(); }

    public static Item toolByType(FramedToolType type) { return TOOLS_BY_TYPE.get(type).get(); }

    private static Supplier<Block[]> getDefaultEntityBlocks()
    {
        //noinspection SuspiciousToArrayCall
        return () -> BLOCKS.getEntries()
                .stream()
                .map(RegistryObject::get)
                .filter(block -> block instanceof IFramedBlock)
                .map(IFramedBlock.class::cast)
                .filter(block -> !block.getBlockType().hasSpecialTile())
                .toArray(Block[]::new);
    }

    private static <T extends Block & IFramedBlock> RegistryObject<Block> registerBlock(Function<BlockType, T> blockFactory, BlockType type)
    {
        return registerBlock(() -> blockFactory.apply(type), type);
    }

    private static <T extends Block & IFramedBlock> RegistryObject<Block> registerBlock(Supplier<T> blockFactory, BlockType type)
    {
        RegistryObject<Block> result = BLOCKS.register(type.getName(), () ->
        {
            T block = blockFactory.get();
            Preconditions.checkArgument(block.getBlockType() == type);
            return block;
        });
        BLOCKS_BY_TYPE.put(type, result);

        if (type.hasBlockItem())
        {
            ITEMS.register(type.getName(), () ->
                    ((IFramedBlock) result.get()).createBlockItem()
            );
        }

        return result;
    }

    @SuppressWarnings("SameParameterValue")
    private static RegistryObject<Block> registerBlock(String name, Supplier<? extends Block> blockFactory)
    {
        RegistryObject<Block> result = BLOCKS.register(name, blockFactory);
        ITEMS.register(name, () -> new BlockItem(result.get(), new Item.Properties().tab(FramedBlocks.FRAMED_TAB)));
        return result;
    }

    private static RegistryObject<Item> registerToolItem(Function<FramedToolType, Item> itemFactory, FramedToolType type)
    {
        RegistryObject<Item> result = ITEMS.register(type.getName(), () -> itemFactory.apply(type));
        TOOLS_BY_TYPE.put(type, result);
        return result;
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> createBlockEntityType(
            BlockEntityType.BlockEntitySupplier<T> factory, BlockType... types
    )
    {
        Supplier<Block[]> blocks = () -> Arrays.stream(types)
                .map(BLOCKS_BY_TYPE::get)
                .map(RegistryObject::get)
                .toArray(Block[]::new);
        return createBlockEntityType(factory, types[0].getName(), blocks);
    }

    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> createBlockEntityType(
            BlockEntityType.BlockEntitySupplier<T> factory, String name, Supplier<Block[]> blocks
    )
    {
        return BE_TYPES.register(name, () ->
        {
            //noinspection ConstantConditions
            return BlockEntityType.Builder.of(factory, blocks.get()).build(null);
        });
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> createMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINER_TYPES.register(name, () -> IForgeMenuType.create(factory));
    }

    @SuppressWarnings("SameParameterValue")
    private static <T extends Recipe<?>> RegistryObject<RecipeType<T>> createRecipeType(String name)
    {
        return RECIPE_TYPES.register(name, () -> RecipeType.simple(Utils.rl(name)));
    }



    private FBContent() { }
}