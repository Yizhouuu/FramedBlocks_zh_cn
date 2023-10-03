package xfacthd.framedblocks.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import net.minecraftforge.network.NetworkHooks;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.util.Utils;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.common.menu.FramedStorageMenu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class FramedStorageBlockEntity extends FramedBlockEntity implements MenuProvider, Nameable
{
    public static final Component TITLE = Utils.translate("title", "framed_secret_storage");

    private final ItemStackHandler itemHandler = new ItemStackHandler(9 * 4)
    {
        @Override
        protected void onContentsChanged(int slot)
        {
            FramedStorageBlockEntity.this.setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();
    private Component customName = null;

    public FramedStorageBlockEntity(BlockPos pos, BlockState state)
    {
        super(FBContent.blockEntityTypeFramedSecretStorage.get(), pos, state);
    }

    protected FramedStorageBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) { super(type, pos, state); }

    public void open(ServerPlayer player) { NetworkHooks.openScreen(player, this, worldPosition); }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == ForgeCapabilities.ITEM_HANDLER)
        {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps()
    {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public boolean isUsableByPlayer(Player player)
    {
        //noinspection ConstantConditions
        if (level.getBlockEntity(worldPosition) != this)
        {
            return false;
        }
        return !(player.distanceToSqr((double)worldPosition.getX() + 0.5D, (double)worldPosition.getY() + 0.5D, (double)worldPosition.getZ() + 0.5D) > 64.0D);
    }

    public List<ItemStack> getDrops()
    {
        List<ItemStack> drops = new ArrayList<>();
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                drops.add(stack);
            }
        }
        return drops;
    }

    public void clearContents()
    {
        for (int i = 0; i < itemHandler.getSlots(); i++)
        {
            itemHandler.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public int getAnalogOutputSignal()
    {
        int stacks = 0;
        float fullness = 0;

        for(int i = 0; i < itemHandler.getSlots(); ++i)
        {
            ItemStack stack = itemHandler.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                float sizeLimit = Math.min(itemHandler.getSlotLimit(i), stack.getMaxStackSize());
                fullness += (float)stack.getCount() / sizeLimit;

                stacks++;
            }
        }

        fullness /= (float)itemHandler.getSlots();
        return Mth.floor(fullness * 14F) + (stacks > 0 ? 1 : 0);
    }

    public void setCustomName(Component customName)
    {
        this.customName = customName;
        setChanged();
    }

    @Override
    public Component getName() { return customName != null ? customName : getDefaultName(); }

    @Override
    public Component getCustomName() { return customName; }



    @Override //Prevent writing inventory contents
    public CompoundTag writeToBlueprint()
    {
        CompoundTag tag = saveWithoutMetadata();
        tag.remove("inventory");
        tag.remove("custom_name");
        return tag;
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        nbt.put("inventory", itemHandler.serializeNBT());
        if (customName != null)
        {
            nbt.putString("custom_name", Component.Serializer.toJson(customName));
        }
        super.saveAdditional(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        itemHandler.deserializeNBT(nbt.getCompound("inventory"));
        if (nbt.contains("custom_name", Tag.TAG_STRING))
        {
            customName = Component.Serializer.fromJson(nbt.getString("custom_name"));
        }
    }



    protected Component getDefaultName() { return TITLE; }

    @Override
    public final Component getDisplayName() { return getName(); }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
    {
        return new FramedStorageMenu(windowId, inv, this);
    }
}
