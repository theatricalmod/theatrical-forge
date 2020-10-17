package dev.theatricalmod.theatrical.client.tile;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

public class TileEntityRendererBasicLightingDesk extends TileEntityRenderer<TileEntityBasicLightingControl> {

    public TileEntityRendererBasicLightingDesk(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    public float convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    @Override
    public void render(TileEntityBasicLightingControl tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        BlockState blockState = tileEntityIn.getBlockState();
        Direction blockDirection = blockState.get(BlockStateProperties.HORIZONTAL_FACING);
        matrixStackIn.translate(0.5, 0.5, 0.5);
        if(blockDirection.getAxis() == Axis.X){
            blockDirection = blockDirection.getOpposite();
        }
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(blockDirection.getHorizontalAngle()));
        matrixStackIn.translate(-0.5, -0.5, -0.5);
        double startX = 1.5;
        byte[] faders = tileEntityIn.getFaders();
        IVertexBuilder linesVertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.LINES);
        for(int i = 0; i < faders.length; i++){
            double baseY = 5.4;
            if(i >= 6){
                baseY += (i / 6) * 7;
            }
            int faderNumber = i - ((i / 6) * 6);
            renderLine(matrixStackIn, startX + (faderNumber * 1.2), baseY, linesVertexBuilder);
        }
        renderLine(matrixStackIn, 14.5, 5.4, linesVertexBuilder);
        IVertexBuilder iVertexBuilder = iRenderTypeBuffer.getBuffer(TheatricalRenderType.FADER);
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
        matrixStackIn.pop();
    }

    public void renderStep(MatrixStack stack, TileEntityBasicLightingControl tileEntityBasicLightingControl, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn){
        stack.push();
        FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
        stack.translate(10.7 /16D, 3 /16D, 9.3 / 16D);
        stack.scale(0.005F, -0.005F, 0.005F);
        stack.rotate(Vector3f.XP.rotationDegrees(90F));
        fontrenderer.drawString(stack,"Step: " + tileEntityBasicLightingControl.getCurrentStep(), 0, 0, 0x000000);
        stack.pop();
    }

    public void renderCurrentMode(MatrixStack stack, TileEntityBasicLightingControl tileEntityBasicLightingControl, IRenderTypeBuffer iRenderTypeBuffer, int combinedLightIn){
        stack.push();
        FontRenderer fontrenderer = this.renderDispatcher.getFontRenderer();
        stack.translate(10.4 /16D, 3 /16D, 8.3 / 16D);
        stack.scale(0.003F, -0.003F, 0.003F);
        stack.rotate(Vector3f.XP.rotationDegrees(90F));
        fontrenderer.drawString(stack, tileEntityBasicLightingControl.isRunMode() ? "Run mode" : "Program mode", 0, 0, 0x000000);
        stack.pop();
    }


    public void renderLine(MatrixStack stack, double x, double y, IVertexBuilder vertexBuilder){
        stack.push();
        Matrix4f m = stack.getLast().getMatrix();
        stack.translate(x / 16D, 3 / 16D,  y / 16D);
        vertexBuilder.pos(m, 0, 0, 0).color(0, 0, 0, 255).endVertex();
        vertexBuilder.pos(m, 0, 0, -(3 / 16F)).color(0, 0, 0, 255).endVertex();
        stack.pop();
    }

    public void renderFader(MatrixStack stack, double x, double baseY, double faderY, IVertexBuilder builder){
        stack.push();
        Matrix4f m = stack.getLast().getMatrix();
        float height = 0.4F / 16F;
        float width = 0.6F / 16F;

        stack.translate((x / 16D) - width / 2, 3 / 16D, (baseY + faderY) / 16D);

        //right
        builder.pos(m, width, height, 0).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, height, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, 0, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, 0, 0).color(0, 0, 0,255).endVertex();

        //front
        builder.pos(m, 0, 0, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, 0, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, height, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, height, width).color(0, 0, 0,255).endVertex();

        //left
        builder.pos(m, 0, 0, 0).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, 0, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, height, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, height, 0).color(0, 0, 0,255).endVertex();

        //back
        builder.pos(m, 0, height, 0).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, height, 0).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, 0, 0).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, 0, 0).color(0, 0, 0,255).endVertex();

        //bottom
        builder.pos(m, width, 0, 0).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, 0, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, 0, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, 0, 0).color(0, 0, 0,255).endVertex();

        //Top
        builder.pos(m, 0, height, 0).color(0, 0, 0,255).endVertex();
        builder.pos(m, 0, height, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, height, width).color(0, 0, 0,255).endVertex();
        builder.pos(m, width, height, 0).color(0, 0, 0,255).endVertex();

        stack.pop();
    }
}
