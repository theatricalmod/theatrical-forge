package dev.theatricalmod.theatrical.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.block.light.BlockGenericFixture;
import dev.theatricalmod.theatrical.entity.FallingLightEntity;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ForgeHooksClient;

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
        TheatricalMod.LOGGER.warn("Called");
        if(blockstate.getBlock() instanceof BlockGenericFixture) {
            World world = entityIn.getWorldObj();
            if (blockstate != world.getBlockState(entityIn.getPosition()) && blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
                matrixStackIn.push();
                BlockPos blockpos = new BlockPos(entityIn.getPosX(), entityIn.getBoundingBox().maxY, entityIn.getPosZ());
                matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
                BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
                for (RenderType type : RenderType.getBlockRenderTypes()) {
                    if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
                        ForgeHooksClient.setRenderLayer(type);
                        blockrendererdispatcher.getBlockModelRenderer().renderModel(world, blockrendererdispatcher.getModelForState(Blocks.BEDROCK.getDefaultState()), blockstate, blockpos, matrixStackIn, bufferIn.getBuffer(type), false, new Random(), blockstate.getPositionRandom(entityIn.getOrigin()), OverlayTexture.NO_OVERLAY);
                    }
                }
                ForgeHooksClient.setRenderLayer(null);
                matrixStackIn.pop();
                super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
            }

//            IBakedModel staticBakedModel = ((BlockGenericFixture) blockstate.getBlock()).getFixture().getStaticModel();
//            IBakedModel hookedModel = ((BlockGenericFixture) blockstate.getBlock()).getFixture().getHookedModel();
//            //if (blockstate != world.getBlockState(entityIn.getPosition())) {
//            matrixStackIn.push();
//            BlockPos blockpos = new BlockPos(entityIn.getPosX(), entityIn.getBoundingBox().maxY, entityIn.getPosZ());
//            matrixStackIn.translate(-0.5D, 0.0D, -0.5D);
//            BlockRendererDispatcher blockrendererdispatcher = Minecraft.getInstance().getBlockRendererDispatcher();
//            for (net.minecraft.client.renderer.RenderType type : net.minecraft.client.renderer.RenderType.getBlockRenderTypes()) {
//                //if (RenderTypeLookup.canRenderInLayer(blockstate, type)) {
//                    net.minecraftforge.client.ForgeHooksClient.setRenderLayer(type);
//                    blockrendererdispatcher.getBlockModelRenderer().renderModel(world, staticBakedModel, blockstate, blockpos, matrixStackIn, bufferIn.getBuffer(type), false, new Random(), blockstate.getPositionRandom(entityIn.getOrigin()), OverlayTexture.NO_OVERLAY);
//                blockrendererdispatcher.getBlockModelRenderer().renderModel(world, hookedModel, blockstate, blockpos, matrixStackIn, bufferIn.getBuffer(type), false, new Random(), blockstate.getPositionRandom(entityIn.getOrigin()), OverlayTexture.NO_OVERLAY);
//                //}
//            }
//            net.minecraftforge.client.ForgeHooksClient.setRenderLayer(null);
//            matrixStackIn.pop();
//            super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
//            //}
        }
    }

    @Override
    public ResourceLocation getEntityTexture(FallingLightEntity entity) {
        return AtlasTexture.LOCATION_BLOCKS_TEXTURE;
    }
}
