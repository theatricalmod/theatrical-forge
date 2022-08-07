package dev.theatricalmod.theatrical.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3f;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.light.BlockLight;
import dev.theatricalmod.theatrical.entity.FallingLightEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FallingLightRenderer extends EntityRenderer<FallingLightEntity> {
    public FallingLightRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.5F;
    }

    //Mostly copied from vanilla
    @Override
    public void render(FallingLightEntity entityIn, float entityYaw, float partialTicks, PoseStack matrixStackIn, MultiBufferSource bufferIn, int packedLightIn) {
        BlockState blockstate = entityIn.getBlockState();
        BlockPos pos = new BlockPos(entityIn.getX() + 0.5, entityIn.getY(), entityIn.getZ() + 0.5D);
        Level world = entityIn.getCommandSenderWorld();
        VertexConsumer builder = bufferIn.getBuffer(RenderType.cutout());
        Fixture fixture = ((BlockLight)blockstate.getBlock()).getFixture();
        if(fixture != null) {
            matrixStackIn.pushPose();
            matrixStackIn.translate(0.5F, 0, .5F);
            Direction facing = blockstate.getValue(BlockHangable.FACING);
            if(facing.getAxis() == Direction.Axis.Z) {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(facing.getOpposite().toYRot()));
            } else  {
                matrixStackIn.mulPose(Vector3f.YP.rotationDegrees(facing.toYRot()));
            }
            matrixStackIn.translate(-0.5F, 0, -.5F);
            matrixStackIn.translate(-.5F, 0, -.5F);
            BakedModel panBakedModel = Minecraft.getInstance().getModelManager().getModel(fixture.getPanModelLocation());
            BakedModel tiltBakedModel = Minecraft.getInstance().getModelManager().getModel(fixture.getTiltModelLocation());
            BakedModel staticBakedModel = Minecraft.getInstance().getModelManager().getModel(fixture.getStaticModelLocation());
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStackIn.last(), bufferIn.getBuffer(Sheets.translucentCullBlockSheet()), blockstate, staticBakedModel, 0, 0, 0, 0, 0, EmptyModelData.INSTANCE);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStackIn.last(), bufferIn.getBuffer(Sheets.translucentCullBlockSheet()), blockstate, panBakedModel, 0, 0, 0, 0, 0, EmptyModelData.INSTANCE);
            Minecraft.getInstance().getBlockRenderer().getModelRenderer().renderModel(matrixStackIn.last(), bufferIn.getBuffer(Sheets.translucentCullBlockSheet()), blockstate,  tiltBakedModel, 0, 0, 0, 0, 0, EmptyModelData.INSTANCE);
            matrixStackIn.popPose();
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(FallingLightEntity entity) {
        return InventoryMenu.BLOCK_ATLAS;
    }
}
