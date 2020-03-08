package dev.theatricalmod.theatrical.client;

import static dev.theatricalmod.theatrical.client.tile.TheatricalRenderType.MAIN_BEAM;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import dev.theatricalmod.theatrical.block.FixtureBlockEntity;
import dev.theatricalmod.theatrical.block.HangableBlock;
import dev.theatricalmod.theatrical.block.light.IntelligentFixtureBlock;
import dev.theatricalmod.theatrical.block.light.MovingLightBlock;
import dev.theatricalmod.theatrical.util.FixtureUtil;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;

public class MovingLightRenderer extends TileEntityRenderer<FixtureBlockEntity> {

    public MovingLightRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(FixtureBlockEntity te) {
        return true;
    }


    @Override
    public void render(FixtureBlockEntity movingLightEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        matrixStack.push();
        IVertexBuilder iVertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.getCutout());
        BlockState blockState = movingLightEntity.getBlockState();
        boolean isFlipped = false;
        if (movingLightEntity.getBlockState().getBlock() instanceof IntelligentFixtureBlock) {
            isFlipped = blockState.get(IntelligentFixtureBlock.FLIPPED);
        }
        boolean isHanging = ((HangableBlock) blockState.getBlock()).isHanging(movingLightEntity.getWorld(), movingLightEntity.getPos());
        renderLight(movingLightEntity, matrixStack, iVertexBuilder, movingLightEntity.getBlockState().get(MovingLightBlock.FACING), v, isFlipped, movingLightEntity.getBlockState(), isHanging);
        IVertexBuilder iVertexBuilder1 = iRenderTypeBuffer.getBuffer(MAIN_BEAM);
//        RenderSystem.shadeModel(GL11.GL_SMOOTH);
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(movingLightEntity.getDefaultRotation()));
        matrixStack.translate(-0.5, -0.5, -0.5);
        matrixStack.translate(movingLightEntity.getBeamStartPosition()[0], movingLightEntity.getBeamStartPosition()[1], movingLightEntity.getBeamStartPosition()[2]);
        if(movingLightEntity.getIntensity() > 0) {
            renderLightBeam(iVertexBuilder1, matrixStack, movingLightEntity, v, (movingLightEntity.getIntensity() * 0.4F) / 255F, movingLightEntity.getBeamWidth(), (float) movingLightEntity.getDistance(), FixtureUtil.getColorFromBE(movingLightEntity));
        }
//        RenderSystem.shadeModel(GL11.GL_FLAT);
        matrixStack.pop();
    }

    public void renderLight(FixtureBlockEntity movingLightEntity, MatrixStack matrixStack, IVertexBuilder iVertexBuilder, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
        IBakedModel panBakedModel = Minecraft.getInstance().getModelManager().getModel(movingLightEntity.getPanModel());
        IBakedModel tiltBakedModel = Minecraft.getInstance().getModelManager().getModel(movingLightEntity.getTiltModel());
        IBakedModel staticBakedModel = Minecraft.getInstance().getModelManager().getModel(movingLightEntity.getStaticModel());
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            matrixStack.translate(0, 0.175, 0);
        }
        if (movingLightEntity.getHangType() == HangableType.HOOK_BAR && isHanging) {
            matrixStack.translate(0, 0.05, 0);
        }
        matrixStack.translate(0.5F, 0, .5F);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(facing.getOpposite().getHorizontalAngle()));
        matrixStack.translate(-0.5F, 0, -.5F);
        if (isFlipped) {
            matrixStack.translate(0.5F, 0.5, .5F);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
            matrixStack.translate(-0.5F, -0.5, -.5F);
        }
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(movingLightEntity.getWorld(), staticBakedModel, movingLightEntity.getBlockState(), movingLightEntity.getPos(), matrixStack, iVertexBuilder, false, new Random(), 0, OverlayTexture.NO_OVERLAY);
        if(isHanging) {
            matrixStack.translate(0, 0.19, 0);
        }
        matrixStack.translate(0.5, 0.5, .5);
        matrixStack.rotate(Vector3f.YP.rotationDegrees((movingLightEntity.prevPan + (movingLightEntity.getPan() - movingLightEntity.prevPan) * partialTicks)));
        matrixStack.translate(-0.5, -0.5, -.5);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(movingLightEntity.getWorld(), panBakedModel, movingLightEntity.getBlockState(), movingLightEntity.getPos(), matrixStack, iVertexBuilder, false, new Random(), 0, OverlayTexture.NO_OVERLAY);
        matrixStack.translate(0.5, 0.4, .5);
        matrixStack.rotate(Vector3f.XP.rotationDegrees((movingLightEntity.prevTilt + (movingLightEntity.getTilt() - movingLightEntity.prevTilt) * partialTicks)));
        matrixStack.translate(-0.5, -0.4, -.5);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(movingLightEntity.getWorld(), tiltBakedModel, movingLightEntity.getBlockState(), movingLightEntity.getPos(), matrixStack, iVertexBuilder, false, new Random(), 0, OverlayTexture.NO_OVERLAY);
    }


    public void renderLightBeam(IVertexBuilder builder, MatrixStack stack, FixtureBlockEntity fixtureBlockEntity, float partialTicks, float alpha, float beamSize, float length, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);
        float width =  beamSize;
        Matrix4f m = stack.getLast().getMatrix();
        float endMultiplier = beamSize * fixtureBlockEntity.getFocus();
        builder.pos(m, width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.pos(m, width, width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, width, -width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0).endVertex();

        builder.pos(m, -width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.pos(m, -width, -width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, -width, width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, -width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0).endVertex();

        builder.pos(m, -width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.pos(m, -width, width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, width, width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, width * endMultiplier, width * endMultiplier, -length).color(r, g, b, 0).endVertex();

        builder.pos(m, width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.pos(m, width, -width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, -width, -width, 0).color(r, g, b, a).endVertex();
        builder.pos(m, -width * endMultiplier, -width * endMultiplier, -length).color(r, g, b, 0).endVertex();
    }

}
