package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public final class FlatElevatedSlopeSlabCornerOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Bottom face
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

        //Middle edges
        OutlineRender.drawLine(builder, poseStack, 0, .5, 0, 0, .5, 1);
        OutlineRender.drawLine(builder, poseStack, 0, .5, 0, 1, .5, 0);

        //Vertical edges
        OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, .5, 0);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, .5, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, .5, 0);

        //Slope
        OutlineRender.drawLine(builder, poseStack, 0, .5, 1, 1, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 0, .5, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, .5, 0, 1, 1, 1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        if (state.getValue(FramedProperties.TOP))
        {
            OutlineRender.mirrorHorizontally(poseStack, true);
        }
    }
}
