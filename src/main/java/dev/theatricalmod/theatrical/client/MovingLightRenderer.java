package dev.theatricalmod.theatrical.client;

import static dev.theatricalmod.theatrical.client.tile.TheatricalRenderType.MAIN_BEAM;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.light.BlockIntelligentFixture;
import dev.theatricalmod.theatrical.block.light.BlockMovingLight;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityFixture;
import dev.theatricalmod.theatrical.util.FixtureUtil;
import java.util.Random;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.client.model.data.EmptyModelData;

public class MovingLightRenderer extends TileEntityRenderer<TileEntityFixture> {

    public MovingLightRenderer(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public boolean isGlobalRenderer(TileEntityFixture te) {
        return true;
    }


    @Override
    public void render(TileEntityFixture movingLightEntity, float v, MatrixStack matrixStack, IRenderTypeBuffer iRenderTypeBuffer, int i, int i1) {
        matrixStack.push();
        IVertexBuilder iVertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.getCutout());
        BlockState blockState = movingLightEntity.getBlockState();
        boolean isFlipped = false;
        if (movingLightEntity.getBlockState().getBlock() instanceof BlockIntelligentFixture) {
            isFlipped = blockState.get(BlockIntelligentFixture.FLIPPED);
        }
        boolean isHanging = ((BlockHangable) blockState.getBlock()).isHanging(movingLightEntity.getWorld(), movingLightEntity.getPos());
        renderLight(movingLightEntity, matrixStack, iVertexBuilder, movingLightEntity.getBlockState().get(BlockMovingLight.FACING), v, isFlipped, movingLightEntity.getBlockState(), isHanging);
        if(movingLightEntity.getIntensity() > 0) {
            IVertexBuilder iVertexBuilder1 = iRenderTypeBuffer.getBuffer(MAIN_BEAM);
    //        RenderSystem.shadeModel(GL11.GL_SMOOTH);
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.rotate(Vector3f.XP.rotationDegrees(movingLightEntity.getDefaultRotation()));
            matrixStack.translate(-0.5, -0.5, -0.5);
            matrixStack.translate(movingLightEntity.getBeamStartPosition()[0], movingLightEntity.getBeamStartPosition()[1], movingLightEntity.getBeamStartPosition()[2]);
            renderLightBeam(iVertexBuilder1, matrixStack, movingLightEntity, v, (movingLightEntity.getIntensity() * 0.4F) / 255F, movingLightEntity.getBeamWidth(), (float) movingLightEntity.getDistance(), FixtureUtil.getColorFromBE(movingLightEntity));
        }
//        RenderSystem.shadeModel(GL11.GL_FLAT);
        matrixStack.pop();
    }

    public void renderLight(TileEntityFixture movingLightEntity, MatrixStack matrixStack, IVertexBuilder iVertexBuilder, Direction facing, float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging) {
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
        if(facing.getAxis() == Direction.Axis.Z) {
            matrixStack.rotate(Vector3f.YP.rotationDegrees(facing.getOpposite().getHorizontalAngle()));
        } else  {
            matrixStack.rotate(Vector3f.YP.rotationDegrees(facing.getHorizontalAngle()));
        }
        matrixStack.translate(-0.5F, 0, -.5F);
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            if (movingLightEntity.getWorld().getBlockState(movingLightEntity.getPos().offset(Direction.UP)).getBlock() instanceof ISupport) {
                ISupport support = (ISupport) movingLightEntity.getWorld().getBlockState(movingLightEntity.getPos().offset(Direction.UP)).getBlock();
                float[] transforms = support.getLightTransforms(movingLightEntity.getWorld(), movingLightEntity.getPos(), facing);
                matrixStack.translate(transforms[0], transforms[1], transforms[2]);
            } else {
                matrixStack.translate(0, 0.19, 0);
            }
        }
        if (isFlipped) {
            matrixStack.translate(0.5F, 0.5, .5F);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
            matrixStack.translate(-0.5F, -0.5, -.5F);
        }
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            matrixStack.translate(0, 0.19, 0);
        }
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(movingLightEntity.getWorld(), staticBakedModel, movingLightEntity.getBlockState(), movingLightEntity.getPos(), matrixStack, iVertexBuilder, false, new Random(), 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            matrixStack.translate(0, 0.19, 0);
        }
        float[] pans = movingLightEntity.getPanRotationPosition();
//        float[] pans = new float[]{0.5F, 0, 0.41F};
        matrixStack.translate(pans[0], pans[1], pans[2]);
        matrixStack.rotate(Vector3f.YP.rotationDegrees((movingLightEntity.prevPan + ((movingLightEntity.getPan()) - movingLightEntity.prevPan) * partialTicks)));
        matrixStack.translate(-pans[0], -pans[1], -pans[2]);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(movingLightEntity.getWorld(), panBakedModel, movingLightEntity.getBlockState(), movingLightEntity.getPos(), matrixStack, iVertexBuilder, false, new Random(), 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
        float[] tilts = movingLightEntity.getTiltRotationPosition();
//        float[] tilts = new float[]{0.5F, 0.3F, 0.39F};
        matrixStack.translate(tilts[0], tilts[1], tilts[2]);
        matrixStack.rotate(Vector3f.XP.rotationDegrees((movingLightEntity.prevTilt + ((movingLightEntity.getTilt()) - movingLightEntity.prevTilt) * partialTicks)));
        matrixStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModel(movingLightEntity.getWorld(), tiltBakedModel, movingLightEntity.getBlockState(), movingLightEntity.getPos(), matrixStack, iVertexBuilder, false, new Random(), 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
    }


    public void renderLightBeam(IVertexBuilder builder, MatrixStack stack, TileEntityFixture tileEntityFixture, float partialTicks, float alpha, float beamSize, float length, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);
        float width =  beamSize;
        Matrix4f m = stack.getLast().getMatrix();
        float endMultiplier = beamSize * tileEntityFixture.getFocus();
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
