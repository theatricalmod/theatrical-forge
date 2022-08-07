package dev.theatricalmod.theatrical.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import dev.theatricalmod.theatrical.TheatricalConfigHandler;
import dev.theatricalmod.theatrical.api.ISupport;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.light.BlockIntelligentFixture;
import dev.theatricalmod.theatrical.block.light.BlockMovingLight;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityFixture;
import dev.theatricalmod.theatrical.util.FixtureUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

import static dev.theatricalmod.theatrical.client.tile.TheatricalRenderType.MAIN_BEAM;

public class TileEntityFixtureRenderer implements BlockEntityRenderer<TileEntityFixture> {

    private final Double beamOpacity = TheatricalConfigHandler.CLIENT.lightBeamOpacity.get();

    public TileEntityFixtureRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public boolean shouldRenderOffScreen(TileEntityFixture te) {
        return true;
    }

    @Override
    public void render(TileEntityFixture movingLightEntity, float v, PoseStack matrixStack, MultiBufferSource iRenderTypeBuffer,
                       int combinedLightIn, int combinedOverlayin) {
        matrixStack.pushPose();
        VertexConsumer iVertexBuilder = iRenderTypeBuffer.getBuffer(RenderType.cutout());
        BlockState blockState = movingLightEntity.getBlockState();
        boolean isFlipped = false;
        if (movingLightEntity.getBlockState().getBlock() instanceof BlockIntelligentFixture) {
            isFlipped = blockState.getValue(BlockIntelligentFixture.HANGING);
        }
        boolean isHanging = ((BlockHangable) blockState.getBlock()).isHanging(movingLightEntity.getLevel(), movingLightEntity.getBlockPos());
        renderLight(movingLightEntity, matrixStack, iVertexBuilder, movingLightEntity.getBlockState().getValue(BlockMovingLight.FACING), v, isFlipped, movingLightEntity.getBlockState(), isHanging, combinedLightIn, combinedOverlayin, iRenderTypeBuffer);
        if(movingLightEntity.getIntensity() > 0) {
            VertexConsumer iVertexBuilder1 = iRenderTypeBuffer.getBuffer(MAIN_BEAM);
    //        RenderSystem.shadeModel(GL11.GL_SMOOTH);
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.mulPose(Vector3f.XP.rotationDegrees(movingLightEntity.getDefaultRotation()));
            matrixStack.translate(-0.5, -0.5, -0.5);
            matrixStack.translate(movingLightEntity.getBeamStartPosition()[0], movingLightEntity.getBeamStartPosition()[1], movingLightEntity.getBeamStartPosition()[2]);
            renderLightBeam(iVertexBuilder1, matrixStack, movingLightEntity, v, (movingLightEntity.getIntensity() * beamOpacity.floatValue()) / 255F, movingLightEntity.getBeamWidth(), (float) movingLightEntity.getDistance(), FixtureUtil.getColorFromBE(movingLightEntity));
        }
//        RenderSystem.shadeModel(GL11.GL_FLAT);
        matrixStack.popPose();
    }



    public void renderLight(TileEntityFixture movingLightEntity, PoseStack matrixStack, VertexConsumer iVertexBuilder, Direction facing,
                            float partialTicks, boolean isFlipped, BlockState blockState, boolean isHanging, int combinedLightIn, int combinedOverlayIn, MultiBufferSource bufferIn) {
        BakedModel panBakedModel = Minecraft.getInstance().getModelManager().getModel(movingLightEntity.getPanModel());
        BakedModel tiltBakedModel = Minecraft.getInstance().getModelManager().getModel(movingLightEntity.getTiltModel());
        BakedModel staticBakedModel = Minecraft.getInstance().getModelManager().getModel(movingLightEntity.getStaticModel());
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            matrixStack.translate(0, 0.175, 0);
        }
        if (movingLightEntity.getHangType() == HangableType.HOOK_BAR && isHanging) {
            matrixStack.translate(0, 0.05, 0);
        }
        matrixStack.translate(0.5F, 0, .5F);
        if(facing.getAxis() == Direction.Axis.Z) {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(facing.getOpposite().toYRot()));
        } else  {
            matrixStack.mulPose(Vector3f.YP.rotationDegrees(facing.toYRot()));
        }
        matrixStack.translate(-0.5F, 0, -.5F);
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            if (movingLightEntity.getLevel().getBlockState(movingLightEntity.getBlockPos().relative(Direction.UP)).getBlock() instanceof ISupport) {
                ISupport support = (ISupport) movingLightEntity.getLevel().getBlockState(movingLightEntity.getBlockPos().relative(Direction.UP)).getBlock();
                float[] transforms = support.getLightTransforms(movingLightEntity.getLevel(), movingLightEntity.getBlockPos(), facing);
                matrixStack.translate(transforms[0], transforms[1], transforms[2]);
            } else {
                matrixStack.translate(0, 0.19, 0);
            }
        }
        if (isFlipped) {
            matrixStack.translate(0.5F, 0.5, .5F);
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            matrixStack.translate(-0.5F, -0.5, -.5F);
        }
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            matrixStack.translate(0, 0.19, 0);
        }
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Sheets.translucentCullBlockSheet()), blockState, staticBakedModel, 1, 1, 1, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        if (movingLightEntity.getHangType() == HangableType.BRACE_BAR && isHanging) {
            matrixStack.translate(0, 0.19, 0);
        }
        float[] pans = movingLightEntity.getPanRotationPosition();
//        float[] pans = new float[]{0.5F, 0, 0.41F};
        matrixStack.translate(pans[0], pans[1], pans[2]);
        matrixStack.mulPose(Vector3f.YP.rotationDegrees((movingLightEntity.prevPan + ((movingLightEntity.getPan()) - movingLightEntity.prevPan) * partialTicks)));
        matrixStack.translate(-pans[0], -pans[1], -pans[2]);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Sheets.translucentCullBlockSheet()),blockState, panBakedModel,1, 1, 1, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
        float[] tilts = movingLightEntity.getTiltRotationPosition();
//        float[] tilts = new float[]{0.5F, 0.3F, 0.39F};
        matrixStack.translate(tilts[0], tilts[1], tilts[2]);
        matrixStack.mulPose(Vector3f.XP.rotationDegrees((movingLightEntity.prevTilt + ((movingLightEntity.getTilt()) - movingLightEntity.prevTilt) * partialTicks)));
        matrixStack.translate(-tilts[0], -tilts[1], -tilts[2]);
        Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStack.last(), bufferIn.getBuffer(Sheets.translucentCullBlockSheet()),blockState, tiltBakedModel,1, 1, 1, combinedLightIn, combinedOverlayIn, EmptyModelData.INSTANCE);
    }


    public void renderLightBeam(VertexConsumer builder, PoseStack stack, TileEntityFixture tileEntityFixture, float partialTicks, float alpha, float beamSize, float length, int color) {
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        int a = (int) (alpha * 255);
        Matrix4f m = stack.last().pose();
        float endMultiplier = beamSize * tileEntityFixture.getFocus();
        builder.vertex(m, beamSize * endMultiplier, beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.vertex(m, beamSize, beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, beamSize, -beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, beamSize * endMultiplier, -beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();

        builder.vertex(m, -beamSize * endMultiplier, -beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.vertex(m, -beamSize, -beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, -beamSize, beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, -beamSize * endMultiplier, beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();

        builder.vertex(m, -beamSize * endMultiplier, beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.vertex(m, -beamSize, beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, beamSize, beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, beamSize * endMultiplier, beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();

        builder.vertex(m, beamSize * endMultiplier, -beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();
        builder.vertex(m, beamSize, -beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, -beamSize, -beamSize, 0).color(r, g, b, a).endVertex();
        builder.vertex(m, -beamSize * endMultiplier, -beamSize * endMultiplier, -length).color(r, g, b, 0).endVertex();
    }

}
