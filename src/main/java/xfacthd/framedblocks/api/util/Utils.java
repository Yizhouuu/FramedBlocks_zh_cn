package xfacthd.framedblocks.api.util;

import com.google.common.base.Preconditions;
import net.minecraft.ChatFormatting;
import net.minecraft.core.*;
import net.minecraft.nbt.*;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.util.Mth;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.*;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.*;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.framedblocks.api.block.FramedBlockEntity;
import xfacthd.framedblocks.api.data.CamoContainer;
import xfacthd.framedblocks.api.data.EmptyCamoContainer;
import xfacthd.framedblocks.api.util.client.ClientUtils;

import java.lang.invoke.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.function.Consumer;

public final class Utils
{
    private static final Direction[] DIRECTIONS = Direction.values();
    public static final TagKey<Block> FRAMEABLE = blockTag("frameable");
    public static final TagKey<Block> BLACKLIST = blockTag("blacklisted");
    /**Allow other mods to whitelist their BEs, circumventing the config setting*/
    public static final TagKey<Block> BE_WHITELIST = blockTag("blockentity_whitelisted");
    public static final TagKey<Block> CAMO_SUSTAIN_PLANT = blockTag("camo_sustain_plant");
    public static final TagKey<Item> WRENCH = itemTag("forge", "tools/wrench");
    /** Allow other mods to add items that temporarily disable intangibility to allow interaction with the targeted block */
    public static final TagKey<Item> DISABLE_INTANGIBLE = itemTag("disable_intangible");

    public static final RegistryObject<Item> FRAMED_HAMMER = RegistryObject.create(
            Utils.rl("framed_hammer"), ForgeRegistries.ITEMS
    );
    public static final RegistryObject<Item> FRAMED_WRENCH = RegistryObject.create(
            Utils.rl("framed_wrench"), ForgeRegistries.ITEMS
    );
    public static final RegistryObject<Item> FRAMED_KEY = RegistryObject.create(
            Utils.rl("framed_key"), ForgeRegistries.ITEMS
    );
    public static final RegistryObject<Item> FRAMED_SCREWDRIVER = RegistryObject.create(
            Utils.rl("framed_screwdriver"), ForgeRegistries.ITEMS
    );
    public static final RegistryObject<Item> FRAMED_REINFORCEMENT = RegistryObject.create(
            Utils.rl("framed_reinforcement"), ForgeRegistries.ITEMS
    );

    public static VoxelShape rotateShape(Direction from, Direction to, VoxelShape shape)
    {
        if (isY(from) || isY(to)) { throw new IllegalArgumentException("Invalid Direction!"); }
        if (from == to) { return shape; }

        VoxelShape[] buffer = new VoxelShape[] { shape, Shapes.empty() };

        int times = (to.get2DDataValue() - from.get2DDataValue() + 4) % 4;
        for (int i = 0; i < times; i++)
        {
            buffer[0].forAllBoxes((minX, minY, minZ, maxX, maxY, maxZ) -> buffer[1] = Shapes.or(
                    buffer[1],
                    Shapes.box(1 - maxZ, minY, minX, 1 - minZ, maxY, maxX)
            ));
            buffer[0] = buffer[1];
            buffer[1] = Shapes.empty();
        }

        return buffer[0];
    }

    public static Vec3 fraction(Vec3 vec)
    {
        return new Vec3(
                vec.x() - Math.floor(vec.x()),
                vec.y() - Math.floor(vec.y()),
                vec.z() - Math.floor(vec.z())
        );
    }

    /**
     * Calculate how far into the block the coordinate of the given direction points in the given direction
     */
    public static double fractionInDir(Vec3 vec, Direction dir)
    {
        vec = fraction(vec);
        double coord = switch (dir.getAxis())
        {
            case X -> vec.x;
            case Y -> vec.y;
            case Z -> vec.z;
        };
        return isPositive(dir) ? coord : (1D - coord);
    }

    /**
     * Check if the left hand value is lower than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is lower than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isLower(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs < rhs;
    }

    /**
     * Check if the left hand value is higher than the right hand value.
     * If the difference between the two values is smaller than {@code 1.0E-5F},
     * the result will be {@code false}
     * @return Returns true when the left hand value is higher than the right hand value,
     *         accounting for floating point precision issues
     */
    public static boolean isHigher(float lhs, float rhs)
    {
        if (Mth.equal(lhs, rhs))
        {
            return false;
        }
        return lhs > rhs;
    }

    @SuppressWarnings("unchecked")
    public static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createBlockEntityTicker(
            BlockEntityType<A> type, BlockEntityType<E> actualType, BlockEntityTicker<? super E> ticker
    )
    {
        return actualType == type ? (BlockEntityTicker<A>)ticker : null;
    }

    public static MutableComponent translate(String prefix, String postfix, Object... arguments)
    {
        return Component.translatable(translationKey(prefix, postfix), arguments);
    }

    public static MutableComponent translate(String prefix, String postfix)
    {
        return Component.translatable(translationKey(prefix, postfix));
    }

    public static String translationKey(String prefix, String postfix)
    {
        String key = "";
        if (prefix != null)
        {
            key = prefix + ".";
        }
        key += FramedConstants.MOD_ID;
        if (postfix != null)
        {
            key += "." + postfix;
        }
        return key;
    }

    public static String translateConfig(String type, String key)
    {
        return translationKey("config", type + "." + key);
    }

    public static <T extends Enum<T> & StringRepresentable> Component[] buildEnumTranslations(
            String prefix, String postfix, T[] values, ChatFormatting... formatting
    )
    {
        return Arrays.stream(values)
                .map(v -> translate(prefix, postfix + "." + v.getSerializedName()))
                .map(c -> c.withStyle(formatting))
                .toArray(Component[]::new);
    }

    public static <T extends Enum<T>> Component[] bindEnumTranslation(String key, T[] values, Component[] valueTranslations)
    {
        Preconditions.checkArgument(values.length == valueTranslations.length, "Value and translation arrays must have the same length");
        Component[] components = new Component[values.length];
        for (T v : values)
        {
            components[v.ordinal()] = Component.translatable(key, valueTranslations[v.ordinal()]);
        }
        return components;
    }

    public static BlockEntity getBlockEntitySafe(BlockGetter blockGetter, BlockPos pos)
    {
        if (blockGetter instanceof Level level)
        {
            return level.getChunkAt(pos).getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
        }
        else if (blockGetter instanceof LevelChunk chunk)
        {
            return chunk.getBlockEntity(pos, LevelChunk.EntityCreationType.CHECK);
        }
        else if (FMLEnvironment.dist.isClient())
        {
            return ClientUtils.getBlockEntitySafe(blockGetter, pos);
        }
        return null;
    }

    public static boolean isPositive(Direction dir) { return dir.getAxisDirection() == Direction.AxisDirection.POSITIVE; }

    public static boolean isX(Direction dir) { return dir.getAxis() == Direction.Axis.X; }

    public static boolean isY(Direction dir) { return dir.getAxis() == Direction.Axis.Y; }

    public static boolean isZ(Direction dir) { return dir.getAxis() == Direction.Axis.Z; }

    public static Direction.Axis nextAxisNotEqualTo(Direction.Axis axis, Direction.Axis except)
    {
        Direction.Axis[] axes = Direction.Axis.VALUES;
        do
        {
            axis = axes[(axis.ordinal() + 1) % axes.length];
        }
        while (axis == except);

        return axis;
    }

    /**
     * Mirrors a block that is oriented towards a face of the block space.
     * @param state The {@link BlockState} to mirror
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given state must have the {@link FramedProperties#FACING_HOR} property
     */
    public static BlockState mirrorFaceBlock(BlockState state, Mirror mirror)
    {
        return mirrorFaceBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /**
     * Mirrors a block that is oriented towards a face of the block space
     * @param state The {@link BlockState} to mirror
     * @param property The {@link DirectionProperty} that should be mirrored on the given state
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given property must support at least all four cardinal directions
     */
    public static BlockState mirrorFaceBlock(BlockState state, DirectionProperty property, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        Direction dir = state.getValue(property);
        //Y directions are inherently ignored
        if ((mirror == Mirror.FRONT_BACK && isX(dir)) || (mirror == Mirror.LEFT_RIGHT && isZ(dir)))
        {
            return state.setValue(property, dir.getOpposite());
        }
        return state;
    }

    /**
     * Mirrors a block that is oriented into a corner of the block space.
     * @param state The {@link BlockState} to mirror
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given state must have the {@link FramedProperties#FACING_HOR} property
     */
    public static BlockState mirrorCornerBlock(BlockState state, Mirror mirror)
    {
        return mirrorCornerBlock(state, FramedProperties.FACING_HOR, mirror);
    }

    /**
     * Mirrors a block that is oriented into a corner of the block space
     * @param state The {@link BlockState} to mirror
     * @param property The {@link DirectionProperty} that should be mirrored on the given state
     * @param mirror The {@link Mirror} to apply to the state
     * @apiNote The given property must support at least all four cardinal directions
     */
    public static BlockState mirrorCornerBlock(BlockState state, DirectionProperty property, Mirror mirror)
    {
        if (mirror == Mirror.NONE)
        {
            return state;
        }

        Direction dir = state.getValue(property);
        if (isY(dir))
        {
            return state;
        }

        if (mirror == Mirror.LEFT_RIGHT)
        {
            dir = switch (dir)
            {
                case NORTH -> Direction.WEST;
                case EAST -> Direction.SOUTH;
                case SOUTH -> Direction.EAST;
                case WEST -> Direction.NORTH;
                default -> throw new IllegalArgumentException("Unreachable!");
            };
        }
        else
        {
            dir = switch (dir)
            {
                case NORTH -> Direction.EAST;
                case EAST -> Direction.NORTH;
                case SOUTH -> Direction.WEST;
                case WEST -> Direction.SOUTH;
                default -> throw new IllegalArgumentException("Unreachable!");
            };
        }
        return state.setValue(property, dir);
    }

    public static TagKey<Block> blockTag(String name) { return blockTag(FramedConstants.MOD_ID, name); }

    public static TagKey<Block> blockTag(String modid, String name)
    {
        return BlockTags.create(new ResourceLocation(modid, name));
    }

    public static TagKey<Item> itemTag(String name) { return itemTag(FramedConstants.MOD_ID, name); }

    public static TagKey<Item> itemTag(String modid, String name)
    {
        return ItemTags.create(new ResourceLocation(modid, name));
    }

    public static FluidState readFluidStateFromNbt(CompoundTag tag)
    {
        if (!tag.contains("Name", Tag.TAG_STRING))
        {
            return Fluids.EMPTY.defaultFluidState();
        }

        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(tag.getString("Name")));
        Preconditions.checkNotNull(fluid);

        FluidState fluidState = fluid.defaultFluidState();
        if (tag.contains("Properties", Tag.TAG_COMPOUND))
        {
            CompoundTag compoundtag = tag.getCompound("Properties");
            StateDefinition<Fluid, FluidState> statedefinition = fluid.getStateDefinition();

            for(String s : compoundtag.getAllKeys())
            {
                Property<?> property = statedefinition.getProperty(s);
                if (property != null)
                {
                    fluidState = NbtUtils.setValueHelper(fluidState, property, s, compoundtag, tag);
                }
            }
        }

        return fluidState;
    }

    public static Property<?> getRotatableProperty(BlockState state)
    {
        for (Property<?> prop : state.getProperties())
        {
            if (prop.getValueClass() == Direction.Axis.class)
            {
                return prop;
            }
            else if (prop instanceof DirectionProperty)
            {
                return prop;
            }
        }
        return null;
    }

    public static void forAllDirections(Consumer<Direction> consumer)
    {
        consumer.accept(null);
        for (Direction dir : DIRECTIONS)
        {
            consumer.accept(dir);
        }
    }

    @Deprecated(forRemoval = true)
    public static void wrapInStateCopy(LevelAccessor level, BlockPos pos, boolean writeToCamoTwo, Runnable action)
    {
        wrapInStateCopy(level, pos, null, ItemStack.EMPTY, writeToCamoTwo, false, action);
    }

    public static void wrapInStateCopy(
            LevelAccessor level, BlockPos pos, Player player, ItemStack stack, boolean writeToCamoTwo, boolean consumeItem, Runnable action
    )
    {
        CamoContainer camo = EmptyCamoContainer.EMPTY;
        boolean glowing = false;
        boolean intangible = false;
        boolean reinforced = false;

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            camo = be.getCamo();
            glowing = be.isGlowing();
            intangible = be.isIntangible(null);
            reinforced = be.isReinforced();
        }

        action.run();

        if (consumeItem && !player.isCreative())
        {
            stack.shrink(1);
            player.getInventory().setChanged();
        }

        if (level.getBlockEntity(pos) instanceof FramedBlockEntity be)
        {
            be.setCamo(camo, writeToCamoTwo);
            be.setGlowing(glowing);
            be.setIntangible(intangible);
            be.setReinforced(reinforced);
        }
    }

    public static ResourceLocation rl(String path) { return new ResourceLocation(FramedConstants.MOD_ID, path); }

    public static MethodHandle unreflectMethod(Class<?> clazz, String srgMethodName, Class<?>... paramTypes)
    {
        Method method = ObfuscationReflectionHelper.findMethod(clazz, srgMethodName, paramTypes);
        try
        {
            return MethodHandles.publicLookup().unreflect(method);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect method '%s#%s()'".formatted(clazz.getName(), srgMethodName), e);
        }
    }

    public static MethodHandle unreflectField(Class<?> clazz, String srgFieldName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, srgFieldName);
        try
        {
            return MethodHandles.publicLookup().unreflectGetter(field);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect field '%s#%s'".formatted(clazz.getName(), srgFieldName), e);
        }
    }

    public static MethodHandle unreflectFieldSetter(Class<?> clazz, String srgFieldName)
    {
        Field field = ObfuscationReflectionHelper.findField(clazz, srgFieldName);
        try
        {
            return MethodHandles.publicLookup().unreflectSetter(field);
        }
        catch (IllegalAccessException e)
        {
            throw new RuntimeException("Failed to unreflect field '%s#%s'".formatted(clazz.getName(), srgFieldName), e);
        }
    }



    private Utils() { }
}