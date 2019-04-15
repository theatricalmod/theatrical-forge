package dev.theatricalmod.theatrical.client.models.cable;

import dev.theatricalmod.theatrical.util.ClientUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;

public class ModelCableBakedItem implements IBakedModel {

    public final ModelCableBaked parent;
    private List<BakedQuad> quads;

    public ModelCableBakedItem(ModelCableBaked p){
        parent = p;
        quads = new ArrayList<>();
        quads = Collections.unmodifiableList(ClientUtils.optimize(quads));
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side,
        long rand) {
        return quads;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isBuiltInRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return parent.particle;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public org.apache.commons.lang3.tuple.Pair<? extends IBakedModel, javax.vecmath.Matrix4f> handlePerspective(
        ItemCameraTransforms.TransformType cameraTransformType)
    {
        return ModelCable.handleModelPerspective(this, cameraTransformType);
    }
}
