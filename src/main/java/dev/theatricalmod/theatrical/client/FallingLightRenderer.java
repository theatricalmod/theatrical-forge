package dev.theatricalmod.theatrical.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.light.BlockLight;
import dev.theatricalmod.theatrical.entity.FallingLightEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.EmptyModelData;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class FallingLightRenderer extends EntityRenderer<FallingLightEntity> {
    public FallingLightRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn);
        this.shadowSize = 0.5F;
    }

    //Mostly copied from vanilla
    @Override
    public void render(FallingLightEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        BlockState blockstate = entityIn.getBlockState();
        BlockPos pos = new BlockPos(entityIn.getPosX() + 0.5, entityIn.getPosY(), entityIn.getPosZ() + 0.5D);
        World world = entityIn.getEntityWorld();
        IVertexBuilder builder = bufferIn.getBuffer(RenderType.getCutout());
        Fixture fixture = ((BlockLight)blockstate.getBlock()).getFixture();
        if(fixture != null) {
            matrixStackIn.push();
            matrixStackIn.translate(0.5F, 0, .5F);
            Direction facing = blockstate.get(BlockHangable.FACING);
            if(facing.getAxis() == Direction.Axis.Z) {
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(facing.getOpposite().getHorizontalAngle()));
            } else  {
                matrixStackIn.rotate(Vector3f.YP.rotationDegrees(facing.getHorizontalAngle()));
            }
            matrixStackIn.translate(-0.5F, 0, -.5F);
            matrixStackIn.translate(-.5F, 0, -.5F);
            IBakedModel panBakedModel = Minecraft.getInstance().getModelManager().getModel(fixture.getPanModelLocation());
            IBakedModel tiltBakedModel = Minecraft.getInstance().getModelManager().getModel(fixture.getTiltModelLocation());
            IBakedModel staticBakedModel = Minecraft.getInstance().getModelManager().getModel(fixture.getStaticModelLocation());
            Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelFlat(world, staticBakedModel, blockstate, pos, matrixStackIn, builder, false, new Random(), 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
            Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelFlat(world, panBakedModel, blockstate, pos, matrixStackIn, builder, false, new Random(), 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
            Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer().renderModelFlat(world, tiltBakedModel, blockstate, pos, matrixStackIn, builder, false, new Random(), 0, OverlayTexture.NO_OVERLAY, EmptyModelData.INSTANCE);
            matrixStackIn.pop();
            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        }
    }

    @Override
    public ResourceLocation getEntityTexture(FallingLightEntity entity) {
        return PlayerContainer.LOCATION_BLOCKS_TEXTURE;
    }
}
