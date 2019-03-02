package com.georlegacy.general.theatrical.client.models;

import com.georlegacy.general.theatrical.blocks.cables.BlockCable;
import com.georlegacy.general.theatrical.blocks.cables.BlockDMXCable;
import com.georlegacy.general.theatrical.tiles.cables.TileDMXCable;
import com.georlegacy.general.theatrical.util.ClientUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

public class ModelCableBaked implements IBakedModel {

    public final TextureAtlasSprite particle;
    public final List<BakedQuad> north, south, west, east, center;
    private final IBakedModel bakedItem;
    private final ItemOverrideList itemOverrideList;

    public ModelCableBaked(ModelCable cable, TextureAtlasSprite p, ModelCable.ModelCallback c){
        particle = p;
        north = c.get(cable.modelNorth, ModelRotation.X0_Y0);
        south = c.get(cable.modelSouth, ModelRotation.X0_Y0);
        west = c.get(cable.modelWest, ModelRotation.X0_Y0);
        east = c.get(cable.modelEast, ModelRotation.X0_Y0);
        center = c.get(cable.modelCenter, ModelRotation.X0_Y0);
        bakedItem = new ModelCableBakedItem(this);

        itemOverrideList = new ItemOverrideList(Collections.emptyList()){
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack,
                @Nullable World world, @Nullable EntityLivingBase entity) {
                Block block = Block.getBlockFromItem(stack.getItem());
                return block instanceof BlockCable ? bakedItem : originalModel;
            }
        };
    }

    public List<BakedQuad> getFromFacing(EnumFacing facing){
        switch(facing){
            case SOUTH:
                return south;
            case NORTH:
                return north;
            case EAST:
                return east;
            case WEST:
                return west;
            default:
                return center;
        }
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side,
        long rand) {

        TileDMXCable tileDMXCable = null;

        if(state instanceof IExtendedBlockState){
            tileDMXCable = ((IExtendedBlockState)state).getValue(BlockDMXCable.CABLE);
        }

        if(tileDMXCable == null){
            return Collections.emptyList();
        }

        int connections = 0;
        for(EnumFacing facing : EnumFacing.values()){
            if(tileDMXCable.isConnected(facing)){
                connections |= 1 << facing.getIndex();
            }
        }
        List<BakedQuad> quads = new ArrayList<>();
        quads.addAll(center);
        for (int i = 0; i < 6; i++)
        {
            if ((connections & (1 << i)) != 0)
            {
                quads.addAll(getFromFacing(EnumFacing.byIndex(i)));
            }
        }

        return ClientUtils.optimize(quads);
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
        return particle;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return itemOverrideList;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
        TransformType cameraTransformType) {
        return ModelCable.handleModelPerspective(this, cameraTransformType);
    }
}
