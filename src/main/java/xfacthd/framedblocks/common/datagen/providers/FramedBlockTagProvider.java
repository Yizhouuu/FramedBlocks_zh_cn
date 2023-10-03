package xfacthd.framedblocks.common.datagen.providers;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.util.FramedConstants;
import xfacthd.framedblocks.common.FBContent;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.Utils;

import java.util.LinkedHashSet;
import java.util.Set;

public final class FramedBlockTagProvider extends BlockTagsProvider
{
    public FramedBlockTagProvider(DataGenerator gen, ExistingFileHelper fileHelper) { super(gen, FramedConstants.MOD_ID, fileHelper); }

    @Override
    public String getName() { return super.getName() + ": " + FramedConstants.MOD_ID; }

    @Override
    @SuppressWarnings("unchecked")
    protected void addTags()
    {
        tag(BlockTags.SLABS).add(FBContent.blockFramedSlab.get());
        tag(BlockTags.STAIRS).add(FBContent.blockFramedStairs.get());
        tag(BlockTags.WALLS).add(FBContent.blockFramedWall.get());
        tag(BlockTags.FENCES).add(FBContent.blockFramedFence.get());
        tag(BlockTags.DOORS).add(FBContent.blockFramedDoor.get(), FBContent.blockFramedIronDoor.get());
        tag(BlockTags.TRAPDOORS).add(FBContent.blockFramedTrapDoor.get(), FBContent.blockFramedIronTrapDoor.get());
        tag(BlockTags.CLIMBABLE).add(FBContent.blockFramedLadder.get());
        tag(BlockTags.SIGNS).add(FBContent.blockFramedSign.get(), FBContent.blockFramedWallSign.get());
        tag(BlockTags.STANDING_SIGNS).add(FBContent.blockFramedSign.get());
        tag(BlockTags.WALL_SIGNS).add(FBContent.blockFramedWallSign.get());
        tag(Tags.Blocks.CHESTS).add(FBContent.blockFramedChest.get());
        tag(BlockTags.RAILS).add(
                FBContent.blockFramedRailSlope.get(),
                FBContent.blockFramedPoweredRailSlope.get(),
                FBContent.blockFramedDetectorRailSlope.get(),
                FBContent.blockFramedActivatorRailSlope.get(),
                FBContent.blockFramedFancyRail.get(),
                FBContent.blockFramedFancyPoweredRail.get(),
                FBContent.blockFramedFancyDetectorRail.get(),
                FBContent.blockFramedFancyActivatorRail.get(),
                FBContent.blockFramedFancyRailSlope.get(),
                FBContent.blockFramedFancyPoweredRailSlope.get(),
                FBContent.blockFramedFancyDetectorRailSlope.get(),
                FBContent.blockFramedFancyActivatorRailSlope.get()
        );

        TagsProvider.TagAppender<Block> frameable = tag(Utils.FRAMEABLE).addTags(
                Tags.Blocks.GLASS,
                Tags.Blocks.STAINED_GLASS,
                BlockTags.ICE,
                BlockTags.LEAVES
        );

        frameable.addOptional(rl("create", "oak_window"))
                 .addOptional(rl("create", "spruce_window"))
                 .addOptional(rl("create", "birch_window"))
                 .addOptional(rl("create", "jungle_window"))
                 .addOptional(rl("create", "acacia_window"))
                 .addOptional(rl("create", "dark_oak_window"))
                 .addOptional(rl("create", "crimson_window"))
                 .addOptional(rl("create", "warped_window"))
                 .addOptional(rl("create", "ornate_iron_window"))
                 .addOptionalTag(rl("chipped", "glass"))
                 .addOptionalTag(rl("chipped", "white_stained_glass"))
                 .addOptionalTag(rl("chipped", "orange_stained_glass"))
                 .addOptionalTag(rl("chipped", "magenta_stained_glass"))
                 .addOptionalTag(rl("chipped", "light_blue_stained_glass"))
                 .addOptionalTag(rl("chipped", "yellow_stained_glass"))
                 .addOptionalTag(rl("chipped", "lime_stained_glass"))
                 .addOptionalTag(rl("chipped", "pink_stained_glass"))
                 .addOptionalTag(rl("chipped", "gray_stained_glass"))
                 .addOptionalTag(rl("chipped", "light_gray_stained_glass"))
                 .addOptionalTag(rl("chipped", "cyan_stained_glass"))
                 .addOptionalTag(rl("chipped", "purple_stained_glass"))
                 .addOptionalTag(rl("chipped", "blue_stained_glass"))
                 .addOptionalTag(rl("chipped", "brown_stained_glass"))
                 .addOptionalTag(rl("chipped", "green_stained_glass"))
                 .addOptionalTag(rl("chipped", "red_stained_glass"))
                 .addOptionalTag(rl("chipped", "black_stained_glass"))
                 .addOptionalTag(rl("forge", "hardened_glass"));

        tag(Utils.BLACKLIST).add(
                Blocks.PISTON,
                Blocks.STICKY_PISTON,
                Blocks.COMPOSTER
        );

        tag(Utils.BE_WHITELIST);

        tag(Utils.CAMO_SUSTAIN_PLANT).addTags(
                BlockTags.DIRT,
                BlockTags.SAND,
                BlockTags.NYLIUM
        ).add(
                Blocks.SOUL_SAND,
                Blocks.SOUL_SOIL
        );

        Set<Block> noToolBlocks = Set.of(
                FBContent.blockFramedItemFrame.get(),
                FBContent.blockFramedGlowingItemFrame.get()
        );

        Set<Block> pickaxeBlocks = new LinkedHashSet<>();

        pickaxeBlocks.add(FBContent.blockFramedIronDoor.get());
        pickaxeBlocks.add(FBContent.blockFramedIronTrapDoor.get());
        pickaxeBlocks.add(FBContent.blockFramedIronGate.get());
        pickaxeBlocks.add(FBContent.blockFramingSaw.get());

        TagsProvider.TagAppender<Block> axeTag = tag(BlockTags.MINEABLE_WITH_AXE);
        FBContent.getRegisteredBlocks()
                .stream()
                .map(RegistryObject::get)
                .filter(b -> b instanceof IFramedBlock)
                .filter(b -> !noToolBlocks.contains(b))
                .filter(b -> !pickaxeBlocks.contains(b))
                .forEach(axeTag::add);

        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(pickaxeBlocks.toArray(Block[]::new));

        tag(BlockTags.create(rl("diagonalwindows", "non_diagonal_panes"))).add(FBContent.blockFramedBars.get());
    }



    private static ResourceLocation rl(String modid, String path)
    {
        return new ResourceLocation(modid, path);
    }
}