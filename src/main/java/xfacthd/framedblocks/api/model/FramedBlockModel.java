package xfacthd.framedblocks.api.model;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.base.Preconditions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import xfacthd.framedblocks.api.FramedBlocksAPI;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.model.quad.QuadModifier;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.*;
import xfacthd.framedblocks.api.block.IFramedBlock;
import xfacthd.framedblocks.api.util.client.*;

import javax.annotation.Nonnull;
import java.util.*;

@SuppressWarnings("deprecation")
public abstract class FramedBlockModel extends BakedModelProxy
{
    private static final Direction[] DIRECTIONS = Direction.values();
    private static final FramedBlockData DEFAULT_DATA = new FramedBlockData.Immutable(
            Blocks.AIR.defaultBlockState(), new boolean[6], false
    );
    public static final ResourceLocation REINFORCEMENT_LOCATION = Utils.rl("block/framed_reinforcement");
    private static BakedModel reinforcementModel = null;

    private final Cache<QuadCacheKey, QuadTable> quadCache = Caffeine.newBuilder()
            .expireAfterAccess(ModelCache.DEFAULT_CACHE_DURATION)
            .build();
    private final Cache<QuadCacheKey, CachedRenderTypes> renderTypeCache = Caffeine.newBuilder()
            .expireAfterAccess(ModelCache.DEFAULT_CACHE_DURATION)
            .build();
    protected final BlockState state;
    private final ChunkRenderTypeSet baseModelRenderTypes;
    private final boolean cacheFullRenderTypes;
    private final boolean forceUngeneratedBaseModel;
    private final boolean useBaseModel;
    private final boolean transformAllQuads;
    private final FullFaceCache fullFaceCache;

    public FramedBlockModel(BlockState state, BakedModel baseModel)
    {
        super(baseModel);
        this.state = state;
        this.baseModelRenderTypes = getBaseModelRenderTypes();
        this.cacheFullRenderTypes = canFullyCacheRenderTypes();
        this.forceUngeneratedBaseModel = forceUngeneratedBaseModel();
        this.useBaseModel = useBaseModel();
        this.transformAllQuads = transformAllQuads(state);
        IBlockType type = ((IFramedBlock) state.getBlock()).getBlockType();
        this.fullFaceCache = new FullFaceCache(type, state);

        Preconditions.checkState(
                this.useBaseModel || !this.forceUngeneratedBaseModel,
                "FramedBlockModel::useBaseModel() must return true when FramedBlockModel::forceUngeneratedBaseModel() returns true"
        );
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand, ModelData extraData, RenderType renderType)
    {
        BlockState camoState = Blocks.AIR.defaultBlockState();

        if (state == null) { state = this.state; }

        FramedBlockData data = extraData.get(FramedBlockData.PROPERTY);
        if (data != null && renderType != null)
        {
            if (side != null && data.isSideHidden(side)) { return Collections.emptyList(); }

            camoState = data.getCamoState();
            if (camoState != null && !camoState.isAir())
            {
                return getCamoQuads(state, camoState, side, rand, extraData, data, renderType);
            }
        }

        if (data == null) { data = DEFAULT_DATA; }
        if (renderType == null) { renderType = RenderType.cutout(); }
        if (camoState == null || camoState.isAir())
        {
            return getCamoQuads(state, null, side, rand, extraData, data, renderType);
        }

        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(BlockState state, Direction side, RandomSource rand)
    {
        if (state == null) { state = this.state; }
        return getCamoQuads(state, null, side, rand, ModelData.EMPTY, DEFAULT_DATA, RenderType.cutout());
    }

    @Override
    public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data)
    {
        FramedBlockData fbData = data.get(FramedBlockData.PROPERTY);
        if (fbData == null)
        {
            fbData = DEFAULT_DATA;
        }

        BlockState camoState = fbData.getCamoState();
        BlockState keyState = camoState;
        if (camoState == null || camoState.isAir())
        {
            camoState = Blocks.AIR.defaultBlockState();
            keyState = getNoCamoModelState(FramedBlocksAPI.getInstance().defaultModelState(), fbData);
        }

        CachedRenderTypes cachedTypes = getCachedRenderTypes(keyState, camoState, fbData, rand, data);
        if (cacheFullRenderTypes)
        {
            return cachedTypes.allTypes;
        }

        ChunkRenderTypeSet renderTypes = cachedTypes.camoTypes;
        ChunkRenderTypeSet overlayTypes = getAdditionalRenderTypes(rand, data);
        if (!overlayTypes.isEmpty())
        {
            renderTypes = ChunkRenderTypeSet.union(renderTypes, overlayTypes);
        }
        return renderTypes;
    }

    private CachedRenderTypes getCachedRenderTypes(BlockState keyState, BlockState camoState, FramedBlockData fbData, RandomSource rand, ModelData data)
    {
        return renderTypeCache.get(
                makeCacheKey(keyState, null, data),
                key -> buildRenderTypeCache(camoState, fbData, rand, data)
        );
    }

    private CachedRenderTypes buildRenderTypeCache(BlockState camoState, FramedBlockData fbData, RandomSource rand, ModelData data)
    {
        ChunkRenderTypeSet camoTypes = baseModelRenderTypes;
        if (!camoState.isAir())
        {
            camoTypes = ChunkRenderTypeSet.union(
                ModelCache.getRenderTypes(camoState, rand, ModelData.EMPTY),
                ModelCache.getCamoRenderTypes(camoState, rand, data)
            );
        }
        else if (fbData.isReinforced())
        {
            camoTypes = ChunkRenderTypeSet.union(baseModelRenderTypes, ModelUtils.CUTOUT);
        }
        return new CachedRenderTypes(camoTypes, cacheFullRenderTypes ? getAdditionalRenderTypes(rand, data) : ChunkRenderTypeSet.none());
    }

    private List<BakedQuad> getCamoQuads(BlockState state, BlockState camoState, Direction side, RandomSource rand, ModelData extraData, FramedBlockData fbData, RenderType renderType)
    {
        ModelData camoData;
        BakedModel model;
        boolean noProcessing;
        boolean noCamo = camoState == null;
        boolean needCtCtx;
        boolean camoInRenderType;
        boolean addReinforcement;

        if (noCamo)
        {
            needCtCtx = false;
            camoState = getNoCamoModelState(FramedBlocksAPI.getInstance().defaultModelState(), fbData);
            addReinforcement = useBaseModel && fbData.isReinforced();
            camoInRenderType = baseModelRenderTypes.contains(renderType);
            noProcessing = (camoInRenderType && forceUngeneratedBaseModel) || fullFaceCache.isFullFace(side);
            model = getCamoModel(camoState, useBaseModel);
            camoData = ModelData.EMPTY;
        }
        else
        {
            noProcessing = fullFaceCache.isFullFace(side);
            needCtCtx = needCtContext(noProcessing);
            model = getCamoModel(camoState, false);
            camoData = needCtCtx ? ModelUtils.getCamoModelData(model, camoState, extraData) : ModelData.EMPTY;
            camoInRenderType = getCachedRenderTypes(camoState, camoState, fbData, rand, camoData).camoTypes.contains(renderType);
            addReinforcement = false;
        }

        if (noProcessing)
        {
            ChunkRenderTypeSet addLayers = getAdditionalRenderTypes(rand, extraData);
            boolean additionalQuads = addLayers.contains(renderType);
            if (!camoInRenderType && !additionalQuads && !addReinforcement) { return List.of(); }

            List<BakedQuad> quads = new ArrayList<>();

            if (camoInRenderType)
            {
                quads.addAll(model.getQuads(camoState, side, rand, camoData, renderType));
            }

            if (additionalQuads)
            {
                getAdditionalQuads(quads, side, state, rand, extraData, renderType);
            }

            if (addReinforcement && renderType == RenderType.cutout())
            {
                quads.addAll(reinforcementModel.getQuads(camoState, side, rand, camoData, renderType));
            }

            return quads;
        }
        else
        {
            Object ctCtx = needCtCtx ? FramedBlocksClientAPI.getInstance().extractCTContext(camoData) : null;
            return quadCache.get(
                    makeCacheKey(camoState, ctCtx, extraData),
                    key -> buildQuadCache(state, key.state(), rand, extraData, ctCtx != null ? camoData : ModelData.EMPTY, noCamo, addReinforcement)
            ).getQuads(renderType, side);
        }
    }

    private static boolean needCtContext(boolean noProcessing)
    {
        ConTexMode mode = FramedBlocksClientAPI.getInstance().getConTexMode();
        if (mode == ConTexMode.NONE) { return false; }

        return noProcessing || mode.atleast(ConTexMode.FULL_CON_FACE);
    }

    /**
     * Builds a {@link RenderType} -> {@link Direction} -> {@link List<BakedQuad>} table with all render types used by this model
     */
    private QuadTable buildQuadCache(
            BlockState state, BlockState camoState, RandomSource rand, ModelData data, ModelData camoData, boolean noCamo, boolean addReinforcement
    )
    {
        QuadTable quadTable = new QuadTable();

        ChunkRenderTypeSet modelLayers = getRenderTypes(state, rand, data);
        ChunkRenderTypeSet camoLayers;
        if (noCamo)
        {
            camoLayers = addReinforcement ? ChunkRenderTypeSet.union(baseModelRenderTypes, ModelUtils.CUTOUT) : baseModelRenderTypes;
            // Make sure the RenderType set being iterated actually contains the no-camo layers in case getQuads()
            // was called with a null RenderType while a camo is provided (i.e. block breaking overlay)
            modelLayers = ChunkRenderTypeSet.union(modelLayers, camoLayers);
        }
        else
        {
            camoLayers = ModelCache.getRenderTypes(camoState, rand, camoData);
        }

        for (RenderType renderType : modelLayers)
        {
            boolean camoInRenderType = camoLayers.contains(renderType);

            quadTable.put(renderType, makeQuads(
                    state,
                    camoState,
                    rand,
                    data,
                    camoData,
                    renderType,
                    camoInRenderType,
                    noCamo,
                    addReinforcement && renderType == RenderType.cutout()
            ));
        }

        return quadTable;
    }

    /**
     * Builds the list of quads per side for a given {@linkplain BlockState camo state} and {@link RenderType}
     */
    private Map<Direction, List<BakedQuad>> makeQuads(
            BlockState state, BlockState camoState, RandomSource rand, ModelData data, ModelData camoData, RenderType renderType,
            boolean camoInRenderType, boolean noCamo, boolean addReinforcement
    )
    {
        Map<Direction, List<BakedQuad>> quadMap = new IdentityHashMap<>();
        quadMap.put(null, new ArrayList<>());
        for (Direction dir : DIRECTIONS) { quadMap.put(dir, new ArrayList<>()); }

        if (camoInRenderType)
        {
            BakedModel camoModel = getCamoModel(camoState, noCamo && useBaseModel);
            List<BakedQuad> quads = ModelUtils.getAllCullableQuads(camoModel, camoState, rand, camoData, renderType);
            if (addReinforcement)
            {
                quads.addAll(ModelUtils.getAllCullableQuads(reinforcementModel, camoState, rand, camoData, renderType));
            }
            if (!transformAllQuads)
            {
                quads.removeIf(q -> fullFaceCache.isFullFace(q.getDirection()));
            }

            for (BakedQuad quad : quads)
            {
                transformQuad(quadMap, quad, data);
            }
            postProcessQuads(quadMap);
        }

        ChunkRenderTypeSet addLayers = getAdditionalRenderTypes(rand, data);
        if (addLayers.contains(renderType))
        {
            getAdditionalQuads(quadMap, state, rand, data, renderType);
        }

        return quadMap;
    }

    /**
     * Called for each {@link BakedQuad} of the camo block's model for whose side this block's
     * {@link CtmPredicate#test(BlockState, Direction)} returns {@code false}.
     * @param quadMap The target map to put all final quads into
     * @param quad @param quad The source quad. Must not be modified directly, use {@link QuadModifier}s to
     *             modify the quad
     * @param data The {@link ModelData}
     */
    protected void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad, ModelData data)
    {
        transformQuad(quadMap, quad);
    }

    /**
     * Called for each {@link BakedQuad} of the camo block's model for whose side this block's
     * {@link CtmPredicate#test(BlockState, Direction)} returns {@code false}.
     * @param quadMap The target map to put all final quads into
     * @param quad The source quad. Must not be modified directly, use {@link QuadModifier}s to
     *             modify the quad
     */
    protected abstract void transformQuad(Map<Direction, List<BakedQuad>> quadMap, BakedQuad quad);

    /**
     * Called after all quads have been piped through {@link FramedBlockModel#transformQuad(Map, BakedQuad)}
     * to apply bulk modifications to all quads, like transformation or rotation
     */
    protected void postProcessQuads(Map<Direction, List<BakedQuad>> quadMap) {}

    /**
     * Return true if the base model loaded from JSON should be used when no camo is applied without going
     * through the quad manipulation process
     */
    protected boolean forceUngeneratedBaseModel() { return false; }

    /**
     * Return true if the base model loaded from JSON should be used instead of the Framed Cube model
     * when no camo is applied. Quad manipulation will still be done if
     * {@link FramedBlockModel#forceUngeneratedBaseModel()} returns false
     * @apiNote Must return true if {@link FramedBlockModel#forceUngeneratedBaseModel()} returns true
     */
    protected boolean useBaseModel() { return forceUngeneratedBaseModel(); }

    /**
     * Return true if all quads should be submitted for transformation, even if their cull-face would be filtered
     * by the {@link CtmPredicate}
     */
    protected boolean transformAllQuads(BlockState state) { return false; }

    /**
     * Return the set of {@link RenderType}s used for the base model without camo.
     * @apiNote Must be available when the FramedBlockModel constructor is called
     */
    protected ChunkRenderTypeSet getBaseModelRenderTypes() { return ModelUtils.CUTOUT; }

    /**
     * Return true if the full set of {@link RenderType}s including overlay render types returned by
     * {@link FramedBlockModel#getAdditionalRenderTypes(RandomSource, ModelData)} are only dependent on the
     * {@link BlockState} associated with this model and/or the camo BlockState in the model data and can
     * therefore be cached based on the camo BlockState
     */
    protected boolean canFullyCacheRenderTypes() { return true; }

    @ApiStatus.Internal
    protected BlockState getNoCamoModelState(BlockState camoState, FramedBlockData fbData)
    {
        if (fbData.useAltModel())
        {
            camoState = camoState.setValue(FramedProperties.ALT, true);
        }
        if (fbData.isReinforced())
        {
            camoState = camoState.setValue(FramedProperties.REINFORCED, true);
        }
        return camoState;
    }

    /**
     * Return the {@link BakedModel} to use as the camo model for the given camoState
     *
     * @param camoState The {@link BlockState} used as camo
     * @param useBaseModel If true, the {@link BakedModelProxy#baseModel} is requested instead of the model of the given state
     *
     * @apiNote Most models shouldn't need to override this. If the model loaded from JSON should be used when no camo
     * is applied, return true from {@link FramedBlockModel#useBaseModel()}. If the model loaded from JSON should be
     * used without applying any quad modifications when no camo is applied, return true from
     * {@link FramedBlockModel#forceUngeneratedBaseModel()} as well
     */
    protected BakedModel getCamoModel(BlockState camoState, boolean useBaseModel)
    {
        if (useBaseModel)
        {
            return baseModel;
        }
        return ModelCache.getModel(camoState);
    }

    /**
     * Return {@link RenderType}s which contain additional quads (i.e. overlays) or {@link ChunkRenderTypeSet#none()} when no additional
     * render types are present
     */
    protected ChunkRenderTypeSet getAdditionalRenderTypes(RandomSource rand, ModelData extraData) { return ChunkRenderTypeSet.none(); }

    /**
     * Add additional quads to faces that return {@code true} from {@code xfacthd.framedblocks.api.util.CtmPredicate#test(BlockState, Direction)}<br>
     * The result of this method will NOT be cached, execution should therefore be as fast as possible
     */
    protected void getAdditionalQuads(List<BakedQuad> quads, Direction side, BlockState state, RandomSource rand, ModelData data, RenderType renderType) {}

    /**
     * Add additional quads to faces that return {@code false} from {@code xfacthd.framedblocks.api.util.CtmPredicate#test(BlockState, Direction)}<br>
     * The result of this method will be cached, processing time is therefore not critical
     */
    protected void getAdditionalQuads(Map<Direction, List<BakedQuad>> quadMap, BlockState state, RandomSource rand, ModelData data, RenderType renderType) {}

    /**
     * Return a custom {@link QuadCacheKey} that holds additional metadata which influences the resulting quads.
     * @implNote The resulting object must at least store the given {@link BlockState} and connected textures context object
     * and should either be a record or have an otherwise properly implemented {@code hashCode()} and {@code equals()}
     * implementation
     * @param state The {@link BlockState} of the camo applied to the block
     * @param ctCtx The current connected textures context object, may be null
     * @param data The {@link ModelData} from the {@link xfacthd.framedblocks.api.block.FramedBlockEntity}
     */
    protected QuadCacheKey makeCacheKey(BlockState state, Object ctCtx, ModelData data)
    {
        return new SimpleQuadCacheKey(state, ctCtx);
    }

    @Nonnull
    @Override
    public final ModelData getModelData(@Nonnull BlockAndTintGetter level, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull ModelData tileData)
    {
        FramedBlockData data = tileData.get(FramedBlockData.PROPERTY);
        if (data != null && !data.getCamoState().isAir())
        {
            BlockState camoState = data.getCamoState();
            BakedModel model = ModelCache.getModel(camoState);
            tileData = tileData.derive()
                    .with(FramedBlockData.CAMO_DATA, model.getModelData(level, pos, camoState, tileData))
                    .build();
        }

        //noinspection removal
        return tileData.derive()
                .with(FramedBlockData.LEVEL, level)
                .with(FramedBlockData.POS, pos)
                .build();
    }

    @Override
    public TextureAtlasSprite getParticleIcon(ModelData data)
    {
        FramedBlockData fbdata = data.get(FramedBlockData.PROPERTY);
        if (fbdata != null)
        {
            BlockState camoState = fbdata.getCamoState();
            if (!camoState.isAir())
            {
                return getCamoModel(camoState, false).getParticleIcon();
            }
        }
        return baseModel.getParticleIcon();
    }

    public final void clearCache()
    {
        quadCache.invalidateAll();
        renderTypeCache.invalidateAll();
    }



    public static void captureReinforcementModel(Map<ResourceLocation, BakedModel> models)
    {
        reinforcementModel = models.get(REINFORCEMENT_LOCATION);
    }



    @SuppressWarnings("unused")
    protected interface QuadCacheKey
    {
        BlockState state();

        Object ctCtx();
    }

    /**
     * @param state The {@link BlockState} of the camo applied to the block
     * @param ctCtx The connected textures context data used by the camo model, may be null
     */
    private record SimpleQuadCacheKey(BlockState state, Object ctCtx) implements QuadCacheKey { }

    private record CachedRenderTypes(ChunkRenderTypeSet camoTypes, ChunkRenderTypeSet overlayTypes, ChunkRenderTypeSet allTypes)
    {
        public CachedRenderTypes(ChunkRenderTypeSet camoTypes, ChunkRenderTypeSet overlayTypes)
        {
            this(camoTypes, overlayTypes, ChunkRenderTypeSet.union(camoTypes, overlayTypes));
        }
    }

    private static class FullFaceCache
    {
        private final boolean[] cache = new boolean[7];

        public FullFaceCache(IBlockType type, BlockState state)
        {
            CtmPredicate pred = type.getCtmPredicate();
            for (Direction side : DIRECTIONS)
            {
                boolean full = pred.test(state, side);
                cache[side.ordinal()] = full;
            }
            cache[6] = false;
        }

        public boolean isFullFace(Direction side)
        {
            return side != null && cache[side.ordinal()];
        }
    }
}