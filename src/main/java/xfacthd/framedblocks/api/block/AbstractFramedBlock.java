package xfacthd.framedblocks.api.block;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.*;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.client.extensions.common.IClientBlockExtensions;
import net.minecraftforge.common.IPlantable;
import xfacthd.framedblocks.FramedBlocks;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.api.util.client.FramedBlockRenderProperties;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@SuppressWarnings("deprecation")
public abstract class AbstractFramedBlock extends Block implements IFramedBlock, SimpleWaterloggedBlock
{
    private static final VoxelShape BEACON_BEAM_SHAPE = box(5, 0, 5, 11, 16, 11);
    private final IBlockType blockType;
    private final Map<BlockState, VoxelShape> shapes;
    private final Object2BooleanMap<BlockState> beaconBeamOcclusion;

    public AbstractFramedBlock(IBlockType blockType, Properties props)
    {
        super(props);
        this.blockType = blockType;
        this.shapes = blockType.generateShapes(getStateDefinition().getPossibleStates());
        this.beaconBeamOcclusion = computeBeaconBeamOcclusion(shapes);

        if (blockType.canOccludeWithSolidCamo())
        {
            registerDefaultState(defaultBlockState()
                    .setValue(FramedProperties.SOLID, false)
                    .setValue(FramedProperties.GLOWING, false)
            );
        }
        if (blockType.supportsWaterLogging())
        {
            registerDefaultState(defaultBlockState().setValue(BlockStateProperties.WATERLOGGED, false));
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        return handleUse(state, level, pos, player, hand, hit);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        tryApplyCamoImmediately(level, pos, placer, stack);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor level, BlockPos pos, BlockPos facingPos)
    {
        updateCulling(level, pos);
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(state, facing, facingState, level, pos, facingPos);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
    {
        updateCulling(level, pos);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        if (isIntangible(state, level, pos, ctx))
        {
            return Shapes.empty();
        }
        return shapes.get(state);
    }

    @Override
    public boolean useShapeForLightOcclusion(BlockState state) { return useCamoOcclusionShapeForLightOcclusion(state); }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        return getCamoOcclusionShape(state, level, pos);
    }

    @Override
    public VoxelShape getVisualShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext ctx)
    {
        return getCamoVisualShape(state, level, pos, ctx);
    }

    @Override
    public FluidState getFluidState(BlockState state)
    {
        if (isWaterLoggable() && state.getValue(BlockStateProperties.WATERLOGGED))
        {
            return Fluids.WATER.getSource(false);
        }
        return Fluids.EMPTY.defaultFluidState();
    }

    @Override
    public boolean canPlaceLiquid(BlockGetter level, BlockPos pos, BlockState state, Fluid fluid)
    {
        if (!isWaterLoggable()) { return false; }
        return SimpleWaterloggedBlock.super.canPlaceLiquid(level, pos, state, fluid);
    }

    @Override
    public boolean placeLiquid(LevelAccessor pLevel, BlockPos pPos, BlockState pState, FluidState pFluidState)
    {
        if (!isWaterLoggable()) { return false; }
        return SimpleWaterloggedBlock.super.placeLiquid(pLevel, pPos, pState, pFluidState);
    }

    @Override
    public ItemStack pickupBlock(LevelAccessor level, BlockPos pos, BlockState state)
    {
        if (!isWaterLoggable()) { return ItemStack.EMPTY; }
        return SimpleWaterloggedBlock.super.pickupBlock(level, pos, state);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        return getCamoDrops(super.getDrops(state, builder), builder);
    }

    /**
     * Return true if the given {@link BlockState} occludes the full area of the beacon beam and
     * can therefore tint the beam
     */
    @Override
    public boolean doesBlockOccludeBeaconBeam(BlockState state, LevelReader level, BlockPos pos)
    {
        if (beaconBeamOcclusion == null)
        {
            FramedBlocks.LOGGER.warn("Block '{}' handles shapes itself but doesn't override AbstractFramedBlock#doesBlockMaskBeaconBeam()", this);
            return false;
        }
        return beaconBeamOcclusion.getBoolean(state);
    }

    @Override
    public boolean canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction side, IPlantable plant)
    {
        return canCamoSustainPlant(state, level, pos, side, plant);
    }

    @Override
    public IBlockType getBlockType() { return blockType; }

    protected final boolean isWaterLoggable() { return blockType.supportsWaterLogging(); }

    @Override
    public void initializeClient(Consumer<IClientBlockExtensions> consumer)
    {
        consumer.accept(new FramedBlockRenderProperties());
    }



    protected static BlockState withCornerFacing(BlockState state, Direction side, Direction facing, Vec3 hitVec)
    {
        if (Utils.isY(side))
        {
            return state.setValue(FramedProperties.FACING_HOR, facing);
        }

        if (Utils.fractionInDir(hitVec, side.getCounterClockWise()) > .5)
        {
            return state.setValue(FramedProperties.FACING_HOR, side.getOpposite().getClockWise());
        }
        else
        {
            return state.setValue(FramedProperties.FACING_HOR, side.getOpposite());
        }
    }

    protected static BlockState withTop(BlockState state, Direction side, Vec3 hitVec)
    {
        return withTop(state, FramedProperties.TOP, side, hitVec);
    }

    protected static BlockState withTop(BlockState state, Property<Boolean> prop, Direction side, Vec3 hitVec)
    {
        if (side == Direction.DOWN)
        {
            state = state.setValue(prop, true);
        }
        else if (side == Direction.UP)
        {
            state = state.setValue(prop, false);
        }
        else
        {
            double y = hitVec.y;
            y -= Math.floor(y);

            state = state.setValue(prop, y >= .5D);
        }
        return state;
    }

    protected static BlockState withWater(BlockState state, LevelReader level, BlockPos pos)
    {
        FluidState fluidState = level.getFluidState(pos);
        return state.setValue(BlockStateProperties.WATERLOGGED, fluidState.getType() == Fluids.WATER);
    }

    private static Object2BooleanMap<BlockState> computeBeaconBeamOcclusion(Map<BlockState, VoxelShape> shapes)
    {
        if (shapes.isEmpty())
        {
            return null;
        }

        Object2BooleanMap<BlockState> beamColorMasking = new Object2BooleanOpenHashMap<>();

        shapes.forEach((state, shape) ->
        {
            VoxelShape intersection = Shapes.join(shape, BEACON_BEAM_SHAPE, BooleanOp.AND);

            beamColorMasking.put(
                    state,
                    intersection.min(Direction.Axis.X) <= BEACON_BEAM_SHAPE.min(Direction.Axis.X) &&
                    intersection.min(Direction.Axis.Z) <= BEACON_BEAM_SHAPE.min(Direction.Axis.Z) &&
                    intersection.max(Direction.Axis.X) >= BEACON_BEAM_SHAPE.max(Direction.Axis.X) &&
                    intersection.max(Direction.Axis.Z) >= BEACON_BEAM_SHAPE.max(Direction.Axis.Z)
            );
        });

        return beamColorMasking;
    }
}