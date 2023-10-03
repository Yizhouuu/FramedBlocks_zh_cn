package xfacthd.framedblocks.common.datagen.providers;

import com.mojang.datafixers.util.Pair;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.*;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public final class FramedLootTableProvider extends LootTableProvider
{
    public FramedLootTableProvider(DataGenerator gen) { super(gen); }

    @Override
    public String getName() { return super.getName() + ": " + FramedConstants.MOD_ID; }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationContext tracker) { /*NOOP*/ }

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootContextParamSet>> getTables()
    {
        return Collections.singletonList(Pair.of(BlockLootTable::new, LootContextParamSets.BLOCK));
    }

    private static class BlockLootTable extends BlockLoot
    {
        @Override
        protected Iterable<Block> getKnownBlocks()
        {
            return FBContent.getRegisteredBlocks()
                    .stream()
                    .map(RegistryObject::get)
                    .collect(Collectors.toList());
        }

        @Override
        protected void addTables()
        {
            dropOther(FBContent.blockFramedWaterloggablePressurePlate.get(), FBContent.blockFramedPressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableStonePressurePlate.get(), FBContent.blockFramedStonePressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableObsidianPressurePlate.get(), FBContent.blockFramedObsidianPressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableGoldPressurePlate.get(), FBContent.blockFramedGoldPressurePlate.get());
            dropOther(FBContent.blockFramedWaterloggableIronPressurePlate.get(), FBContent.blockFramedIronPressurePlate.get());

            dropDoor(FBContent.blockFramedDoor.get());
            dropDoor(FBContent.blockFramedIronDoor.get());
            dropTwoOf(FBContent.blockFramedDoubleSlab.get(), FBContent.blockFramedSlab.get());
            dropTwoOf(FBContent.blockFramedDoublePanel.get(), FBContent.blockFramedPanel.get());

            dropOther(FBContent.blockFramedVerticalHalfSlope.get(), FBContent.blockFramedHalfSlope.get());
            dropOther(FBContent.blockFramedVerticalDoubleHalfSlope.get(), FBContent.blockFramedDoubleHalfSlope.get());

            FBContent.getRegisteredBlocks()
                    .stream()
                    .map(RegistryObject::get)
                    .filter(block -> !map.containsKey(block.getLootTable()))
                    .forEach(this::dropSelf);
        }

        protected void dropTwoOf(Block block, Block drop)
        {
            add(block, b -> LootTable.lootTable().withPool(
                    LootPool.lootPool().setRolls(ConstantValue.exactly(1)).add(
                            applyExplosionDecay(block, LootItem.lootTableItem(drop).apply(
                                    SetItemCountFunction.setCount(ConstantValue.exactly(2))
                                    )
                            )
                    )
            ));
        }

        protected void dropDoor(Block block)
        {
            add(block, BlockLoot::createDoorTable);
        }
    }
}