package xfacthd.framedblocks.common.block.cube;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;
import xfacthd.framedblocks.api.util.CtmPredicate;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.block.FramedBlock;
import xfacthd.framedblocks.common.blockentity.FramedOwnableBlockEntity;
import xfacthd.framedblocks.common.data.BlockType;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.NullableDirection;
import xfacthd.framedblocks.common.util.ServerConfig;

public class FramedOneWayWindowBlock extends FramedBlock
{
    public static final CtmPredicate CTM_PREDICATE = (state, side) ->
    {
        NullableDirection face = state.getValue(PropertyHolder.NULLABLE_FACE);
        return side != null && side != face.toDirection();
    };

    public FramedOneWayWindowBlock()
    {
        super(BlockType.FRAMED_ONE_WAY_WINDOW);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        super.createBlockStateDefinition(builder);
        builder.add(PropertyHolder.NULLABLE_FACE);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer instanceof Player player && level.getBlockEntity(pos) instanceof FramedOwnableBlockEntity be)
        {
            be.setOwner(player.getUUID(), true);
        }
    }

    @Override
    public boolean handleBlockLeftClick(BlockState state, Level level, BlockPos pos, Player player)
    {
        if (player.getMainHandItem().is(FBContent.itemFramedWrench.get()) && isOwnedBy(level, pos, player))
        {
            if (!level.isClientSide())
            {
                if (player.isShiftKeyDown())
                {
                    level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.NONE));
                }
                else
                {
                    HitResult hit = player.pick(10D, 0, false);
                    if (!(hit instanceof BlockHitResult blockHit))
                    {
                        return false;
                    }

                    NullableDirection face =  NullableDirection.fromDirection(blockHit.getDirection());
                    level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.NULLABLE_FACE, face));
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public VoxelShape getOcclusionShape(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (state.getValue(PropertyHolder.NULLABLE_FACE) != NullableDirection.NONE)
        {
            return Shapes.empty();
        }
        return super.getOcclusionShape(state, level, pos);
    }

    @Override
    public boolean shouldPreventNeighborCulling(BlockGetter level, BlockPos pos, BlockState state, BlockPos adjPos, BlockState adjState)
    {
        if (adjState.getBlock() != FBContent.blockFramedOneWayWindow.get())
        {
            return true;
        }
        return state.getValue(PropertyHolder.NULLABLE_FACE) != adjState.getValue(PropertyHolder.NULLABLE_FACE);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState rotate(BlockState state, Rotation rotation)
    {
        Direction dir = state.getValue(PropertyHolder.NULLABLE_FACE).toDirection();
        if (dir != null && !Utils.isY(dir))
        {
            dir = rotation.rotate(dir);
            state = state.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.fromDirection(dir));
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockState mirror(BlockState state, Mirror mirror)
    {
        Direction dir = state.getValue(PropertyHolder.NULLABLE_FACE).toDirection();
        if (dir != null && !Utils.isY(dir))
        {
            dir = mirror.mirror(dir);
            state = state.setValue(PropertyHolder.NULLABLE_FACE, NullableDirection.fromDirection(dir));
        }
        return state;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new FramedOwnableBlockEntity(pos, state);
    }



    public static boolean isOwnedBy(BlockGetter level, BlockPos pos, Player player)
    {
        if (!ServerConfig.oneWayWindowOwnable)
        {
            return true;
        }
        if (level.getBlockEntity(pos) instanceof FramedOwnableBlockEntity be)
        {
            return player.getUUID().equals(be.getOwner());
        }
        return false;
    }
}
