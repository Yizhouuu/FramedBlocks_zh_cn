package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.FramedProperties;
import xfacthd.framedblocks.api.util.client.OutlineRender;

public final class InnerThreewayCornerOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        //Bottom face
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

        //Back face
        OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

        //Left face
        OutlineRender.drawLine(builder, poseStack, 1, 1, 0, 1, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 0);

        //Slope edges
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 1, 0);
        OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 0);

        //Cross
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, .5, .5, .5);
        OutlineRender.drawLine(builder, poseStack, .5, .5, .5, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 1, 0, .5, .5, .5);
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
