package xfacthd.framedblocks.client.data.outline;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.framedblocks.api.util.client.OutlineRender;
import xfacthd.framedblocks.common.data.PropertyHolder;
import xfacthd.framedblocks.common.data.property.HorizontalRotation;

public final class VerticalSlopedStairsOutlineRenderer implements OutlineRender
{
    @Override
    public void draw(BlockState state, PoseStack poseStack, VertexConsumer builder)
    {
        // Back face
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 1, 1, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 1, 1, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 1, 1, 1, 1, 1);

        // Front face
        OutlineRender.drawLine(builder, poseStack, 0, 1, .5, 1, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0, .5, 1, 1, .5);
        OutlineRender.drawLine(builder, poseStack, 1, 0, .5, 0, 1, .5);

        // Middle face
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 1, 0, 0);
        OutlineRender.drawLine(builder, poseStack, 0, 0, 0, 0, 1, 0);
        OutlineRender.drawLine(builder, poseStack, 1, 0, 0, 0, 1, 0);

        // Horizontal side edges
        OutlineRender.drawLine(builder, poseStack, 0, 0,  0, 0, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 0, 1,  0, 0, 1, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 0,  0, 1, 0, 1);
        OutlineRender.drawLine(builder, poseStack, 1, 1, .5, 1, 1, 1);
    }

    @Override
    public void rotateMatrix(PoseStack poseStack, BlockState state)
    {
        OutlineRender.super.rotateMatrix(poseStack, state);

        HorizontalRotation rotation = state.getValue(PropertyHolder.ROTATION);
        poseStack.mulPose(SlopePanelOutlineRenderer.ROTATIONS[rotation.ordinal()]);
    }
}