package xfacthd.framedblocks.client.util;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.client.model.data.ModelData;
import xfacthd.framedblocks.api.FramedBlocksClientAPI;
import xfacthd.framedblocks.api.ghost.GhostRenderBehaviour;
import xfacthd.framedblocks.api.type.IBlockType;
import xfacthd.framedblocks.api.util.ConTexMode;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.client.model.FluidModel;
import xfacthd.framedblocks.client.render.BlockOutlineRenderer;
import xfacthd.framedblocks.client.render.GhostBlockRenderer;
import xfacthd.framedblocks.common.compat.create.CreateCompat;

import java.util.function.Function;

@SuppressWarnings("unused")
public final class ClientApiImpl implements FramedBlocksClientAPI
{
    private static final CtContextSupplier[] CT_CONTEXT_GETTERS = new CtContextSupplier[] {
            CreateCompat::tryGetCTContext
    };

    @Override
    public BlockColor defaultBlockColor() { return FramedBlockColor.INSTANCE; }

    @Override
    public BakedModel createFluidModel(Fluid fluid) { return FluidModel.create(fluid); }

    @Override
    public void registerOutlineRender(IBlockType type, OutlineRender render)
    {
        BlockOutlineRenderer.registerOutlineRender(type, render);
    }

    @Override
    public void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Block... blocks)
    {
        GhostBlockRenderer.registerBehaviour(behaviour, blocks);
    }

    @Override
    public void registerGhostRenderBehaviour(GhostRenderBehaviour behaviour, Item... items)
    {
        GhostBlockRenderer.registerBehaviour(behaviour, items);
    }

    @Override
    public boolean useDiscreteUVSteps() { return ClientConfig.useDiscreteUVSteps; }

    @Override
    public ConTexMode getConTexMode() { return ClientConfig.conTexMode; }

    @Override
    public Object extractCTContext(ModelData data)
    {
        for (CtContextSupplier sup : CT_CONTEXT_GETTERS)
        {
            Object ctx = sup.apply(data);
            if (ctx != null)
            {
                return ctx;
            }
        }
        return null;
    }



    @FunctionalInterface
    private interface CtContextSupplier extends Function<ModelData, Object> { }
}