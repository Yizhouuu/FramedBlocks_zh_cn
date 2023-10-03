package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.render.Quaternions;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.CornerType;

public final class CornerSlopeOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (!type.isHorizontal())
        {
            //Back edge
            OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

            //Bottom face
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);

            //Slope
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 1, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 1, 1);
        }
        else
        {
            //Back face
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);

            //Back edge
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 0, 1);

            //Center slope edge
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 1, 1);

            //Side slope edges
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 1, 1);
            OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 1);
        }
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        CornerType type = state.getValue(PropertyHolder.CORNER_TYPE);
        if (!type.isHorizontal())
        {
            if (type.isTop())
            {
                OutlineRender.mirrorHorizontally(poseStack, true);
            }
        }
        else
        {
            if (!type.isRight())
            {
                poseStack.mulPose(Quaternions.ZP_90);
            }
            if (type.isTop())
            {
                poseStack.mulPose(type.isRight() ? Quaternions.ZN_90 : Quaternions.ZP_90);
            }
        }
    }
}
