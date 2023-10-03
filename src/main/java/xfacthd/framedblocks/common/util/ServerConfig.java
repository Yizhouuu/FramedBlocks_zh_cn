package xfacthd.framedblocks.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;
import xfacthd.framedblocks.api.util.Utils;

public final class ServerConfig
{
    public static final ForgeConfigSpec SPEC;
    public static final ServerConfig INSTANCE;

    private static final String KEY_ALLOW_BLOCK_ENTITIES = "allowBlockEntities";
    private static final String KEY_ENABLE_INTANGIBILITY = "enableIntangibleFeature";
    private static final String KEY_INTANGIBLE_MARKER = "intangibleMarkerItem";
    private static final String KEY_ONE_WAY_WINDOW_OWNABLE = "oneWayWindowOwnable";
    private static final String KEY_CONSUME_CAMO_ITEM = "consumeCamoItem";

    public static final String TRANSLATION_ALLOW_BLOCK_ENTITIES = translate(KEY_ALLOW_BLOCK_ENTITIES);
    public static final String TRANSLATION_ENABLE_INTANGIBILITY = translate(KEY_ENABLE_INTANGIBILITY);
    public static final String TRANSLATION_INTANGIBLE_MARKER = translate(KEY_INTANGIBLE_MARKER);
    public static final String TRANSLATION_ONE_WAY_WINDOW_OWNABLE = translate(KEY_ONE_WAY_WINDOW_OWNABLE);
    public static final String TRANSLATION_CONSUME_CAMO_ITEM = translate(KEY_CONSUME_CAMO_ITEM);

    public static boolean allowBlockEntities;
    public static boolean enableIntangibleFeature;
    public static Item intangibleMarkerItem;
    public static boolean oneWayWindowOwnable;
    public static boolean consumeCamoItem;

    private final ForgeConfigSpec.BooleanValue allowBlockEntitiesValue;
    private final ForgeConfigSpec.BooleanValue enableIntangibleFeatureValue;
    private final ForgeConfigSpec.ConfigValue<String> intangibleMarkerItemValue;
    private final ForgeConfigSpec.BooleanValue oneWayWindowOwnableValue;
    private final ForgeConfigSpec.BooleanValue consumeCamoItemValue;

    static
    {
        final Pair<ServerConfig, ForgeConfigSpec> configSpecPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SPEC = configSpecPair.getRight();
        INSTANCE = configSpecPair.getLeft();
    }

    @SuppressWarnings("ConstantConditions")
    public ServerConfig(ForgeConfigSpec.Builder builder)
    {
        FMLJavaModLoadingContext.get().getModEventBus().register(this);

        builder.push("general");
        allowBlockEntitiesValue = builder
                .comment("Whether blocks with block entities can be placed in Framed Blocks")
                .translation(TRANSLATION_ALLOW_BLOCK_ENTITIES)
                .define(KEY_ALLOW_BLOCK_ENTITIES, false);
        enableIntangibleFeatureValue = builder
                .comment("Enables the intangbility feature. Disabling this also prevents moving through blocks that are already marked as intangible")
                .translation(TRANSLATION_ENABLE_INTANGIBILITY)
                .define(KEY_ENABLE_INTANGIBILITY, false);
        intangibleMarkerItemValue = builder
                .comment("The item to use for making Framed Blocks intangible. The value must be a valid item registry name")
                .translation(TRANSLATION_INTANGIBLE_MARKER)
                .define(KEY_INTANGIBLE_MARKER, ForgeRegistries.ITEMS.getKey(Items.PHANTOM_MEMBRANE).toString(), ServerConfig::validateItemName);
        oneWayWindowOwnableValue = builder
                .comment("If true, only the player who placed the Framed One-Way Window can modify the window direction")
                .translation(TRANSLATION_ONE_WAY_WINDOW_OWNABLE)
                .define(KEY_ONE_WAY_WINDOW_OWNABLE, true);
        consumeCamoItemValue = builder
                .comment("If true, applying a camo will consume the item and removing the camo will drop it again")
                .translation(TRANSLATION_CONSUME_CAMO_ITEM)
                .define(KEY_CONSUME_CAMO_ITEM, true);
        builder.pop();
    }

    private static boolean validateItemName(Object obj)
    {
        if (obj instanceof String name)
        {
            ResourceLocation key = new ResourceLocation(name);
            if (ForgeRegistries.ITEMS.containsKey(key))
            {
                return ForgeRegistries.ITEMS.getValue(key) != Items.AIR;
            }
        }
        return false;
    }

    private static String translate(String key)
    {
        return Utils.translateConfig("server", key);
    }

    @SubscribeEvent
    public void onConfigReloaded(ModConfigEvent event)
    {
        if (event.getConfig().getType() == ModConfig.Type.SERVER && event.getConfig().getSpec() == SPEC)
        {
            allowBlockEntities = allowBlockEntitiesValue.get();
            enableIntangibleFeature = enableIntangibleFeatureValue.get();
            intangibleMarkerItem = ForgeRegistries.ITEMS.getValue(new ResourceLocation(intangibleMarkerItemValue.get()));
            oneWayWindowOwnable = oneWayWindowOwnableValue.get();
            consumeCamoItem = consumeCamoItemValue.get();
        }
    }
}
