package dev.theatricalmod.theatrical.client.tile;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class TileEntityRendererBasicLightingDesk implements BlockEntityRenderer<TileEntityBasicLightingControl> {

    public Font font;

    public TileEntityRendererBasicLightingDesk(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    public float convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    @Override
    public void render(TileEntityBasicLightingControl tileEntityIn, float partialTicks, PoseStack matrixStackIn, MultiBufferSource iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.pushPose();
        BlockState blockState = tileEntityIn.getBlockState();
        Direction blockDirection = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING);
        matrixStackIn.translate(0.5, 0.5, 0.5);
        if(blockDirection.getAxis() == Axis.X){
            blockDirection = blockDirection.getOpposite();
        }
        matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(blockDirection.toYRot()));
        matrixStackIn.translate(-0.5, -0.5, -0.5);
        double startX = 1.5;
        byte[] faders = tileEntityIn.getFaders();
        VertexConsumer linesVertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.LINES);
        for(int i = 0; i < faders.length; i++){
            double baseY = 5.4;
            if(i >= 6){
                baseY += (i / 6) * 7;
            }
            int faderNumber = i - ((i / 6) * 6);
            renderLine(matrixStackIn, startX + (faderNumber * 1.2), baseY, linesVertexBuilder);
        }
        renderLine(matrixStackIn, 14.5, 5.4, linesVertexBuilder);
        VertexConsumer iVertexBuilder = iRenderTypeBuffer.getBuffer(TheatricalRenderType.FADER);
        for(int i = 0; i < faders.length; i++){
            double baseY = 5.4;
            if(i >= 6){
                baseY += (i / 6) * 7;
            }
            int faderNumber = i - ((i / 6) * 6);
            renderFader(matrixStackIn, startX + (faderNumber * 1.2), baseY, -((convertByteToInt(faders[i]) / 255) * 3), iVertexBuilder);
        }
        renderFader(matrixStackIn, 14.5, 5.4, -((convertByteToInt(tileEntityIn.getGrandMaster()) / 255) * 3), iVertexBuilder);
        renderStep(matrixStackIn, tileEntityIn, iRenderTypeBuffer, combinedLightIn);
        renderCurrentMode(matrixStackIn, tileEntityIn, iRenderTypeBuffer, combinedLightIn);
        matrixStackIn.popPose();
    }

    public void renderStep(PoseStack stack, TileEntityBasicLightingControl tileEntityBasicLightingControl, MultiBufferSource iRenderTypeBuffer, int combinedLightIn){
        stack.pushPose();
        stack.translate(10.7 /16D, 3 /16D, 9.3 / 16D);
        stack.scale(0.005F, -0.005F, 0.005F);
        stack.mulPose(Vector3f.XP.rotationDegrees(90F));
        font.draw(stack,"Step: " + tileEntityBasicLightingControl.getCurrentStep(), 0, 0, 0x000000);
        stack.popPose();
    }

    public void renderCurrentMode(PoseStack stack, TileEntityBasicLightingControl tileEntityBasicLightingControl, MultiBufferSource iRenderTypeBuffer, int combinedLightIn){
        stack.pushPose();
        stack.translate(10.4 /16D, 3 /16D, 8.3 / 16D);
        stack.scale(0.003F, -0.003F, 0.003F);
        stack.mulPose(Vector3f.XP.rotationDegrees(90F));
        font.draw(stack, tileEntityBasicLightingControl.isRunMode() ? "Run mode" : "Program mode", 0, 0, 0x000000);
        stack.popPose();
    }


    public void renderLine(PoseStack stack, double x, double y, VertexConsumer vertexBuilder){
        stack.pushPose();
        Matrix4f m = stack.last().pose();
        stack.translate(x / 16D, 3 / 16D,  y / 16D);
        vertexBuilder.vertex(m, 0, 0, 0).color(0, 0, 0, 255).endVertex();
        vertexBuilder.vertex(m, 0, 0, -(3 / 16F)).color(0, 0, 0, 255).endVertex();
        stack.popPose();
    }

    public void renderFader(PoseStack stack, double x, double baseY, double faderY, VertexConsumer builder){
        stack.pushPose();
        Matrix4f m = stack.last().pose();
        float height = 0.4F / 16F;
        float width = 0.6F / 16F;

        stack.translate((x / 16D) - width / 2, 3 / 16D, (baseY + faderY) / 16D);

        //right
        builder.vertex(m, width, height, 0).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, height, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, 0, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, 0, 0).color(0, 0, 0,255).endVertex();

        //front
        builder.vertex(m, 0, 0, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, 0, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, height, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, height, width).color(0, 0, 0,255).endVertex();

        //left
        builder.vertex(m, 0, 0, 0).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, 0, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, height, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, height, 0).color(0, 0, 0,255).endVertex();

        //back
        builder.vertex(m, 0, height, 0).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, height, 0).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, 0, 0).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, 0, 0).color(0, 0, 0,255).endVertex();

        //bottom
        builder.vertex(m, width, 0, 0).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, 0, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, 0, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, 0, 0).color(0, 0, 0,255).endVertex();

        //Top
        builder.vertex(m, 0, height, 0).color(0, 0, 0,255).endVertex();
        builder.vertex(m, 0, height, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, height, width).color(0, 0, 0,255).endVertex();
        builder.vertex(m, width, height, 0).color(0, 0, 0,255).endVertex();

        stack.popPose();
    }
}
