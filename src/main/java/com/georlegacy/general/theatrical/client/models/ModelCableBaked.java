package com.georlegacy.general.theatrical.client.models;

import com.georlegacy.general.theatrical.blocks.cables.BlockDMXCable;
import com.georlegacy.general.theatrical.items.ItemDMXCable;
import com.georlegacy.general.theatrical.tiles.cables.TileDMXCable;
import com.georlegacy.general.theatrical.util.ClientUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

public class ModelCableBaked implements IBakedModel {

    public final TextureAtlasSprite particle;
    public final List<List<BakedQuad>> north, south, west, east, center;
    public final List<List<BakedQuad>> extendedNorth, extendedSouth, extendedWest, extendedEast;
    private final IBakedModel bakedItem;
    private final ItemOverrideList itemOverrideList;

    int NORTH = EnumFacing.NORTH.getIndex();
    int SOUTH = EnumFacing.SOUTH.getIndex();
    int EAST = EnumFacing.EAST.getIndex();
    int WEST = EnumFacing.WEST.getIndex();
    int DOWN = EnumFacing.DOWN.getIndex();
    int UP = EnumFacing.UP.getIndex();

    public ModelCableBaked(ModelCable cable, TextureAtlasSprite p, ModelCable.ModelCallback c){
        particle = p;
        north = new ArrayList<>();
        south = new ArrayList<>();
        west = new ArrayList<>();
        east = new ArrayList<>();
        center = new ArrayList<>();
        extendedNorth = new ArrayList<>();
        extendedSouth = new ArrayList<>();
        extendedWest = new ArrayList<>();
        extendedEast = new ArrayList<>();
        for(EnumFacing facing : EnumFacing.VALUES){
            north.add(c.get(cable.modelNorth, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            south.add(c.get(cable.modelSouth, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            east.add(c.get(cable.modelEast, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            west.add(c.get(cable.modelWest, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            center.add(c.get(cable.modelCenter, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            extendedNorth.add(c.get(cable.modelExtendedNorth, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            extendedSouth.add(c.get(cable.modelExtendedSouth, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            extendedWest.add(c.get(cable.modelExtendedWest, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
            extendedEast.add(c.get(cable.modelExtendedEast, ModelCable.FACE_ROTATIONS[facing.getIndex()]));
        }
        bakedItem = new ModelCableBakedItem(this);

        itemOverrideList = new ItemOverrideList(Collections.emptyList()){
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack,
                @Nullable World world, @Nullable EntityLivingBase entity) {
                return stack.getItem() instanceof ItemDMXCable ? bakedItem : originalModel;
            }
        };
    }

// This comment is for Alex. This is commented out cause Latvian told me to.
    public List<List<BakedQuad>> getFromFacing(EnumFacing facing, int face){
        if(face  == 0 ){
            switch(facing.getIndex()){
                case 3:
                    return extendedSouth;
                case 2:
                    return extendedNorth;
                case 5:
                    return extendedEast;
                case 4:
                    return extendedWest;
            }
        }else
        if(face  == 1 ){
            switch(facing.getIndex()){
                case 3:
                    return extendedNorth;
                case 2:
                    return extendedSouth;
                case 5:
                    return extendedEast;
                case 4:
                    return extendedWest;
            }
        }else
        if(face  == 2 ){
            switch(facing.getIndex()){
                case 1:
                    return extendedSouth;
                case 0:
                    return extendedNorth;
                case 5:
                    return extendedWest;
                case 4:
                    return extendedEast;
            }
        }else
        if(face  == 3 ){
            switch(facing.getIndex()){
                case 1:
                    return extendedSouth;
                case 0:
                    return extendedNorth;
                case 5:
                    return extendedEast;
                case 4:
                    return extendedWest;
            }
        }else
        if(face  == 4 ){
            switch(facing.getIndex()){
                case 1:
                    return extendedSouth;
                case 0:
                    return extendedNorth;
                case 2:
                    return extendedWest;
                case 3:
                    return extendedEast;
            }
        }else
        if(face  ==5 ){
            switch(facing.getIndex()){
                case 1:
                    return extendedSouth;
                case 0:
                    return extendedNorth;
                case 2:
                    return extendedEast;
                case 3:
                    return extendedWest;
            }
        }
        return center;
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
        ArrayList<BakedQuad> quads = new ArrayList<>();
        for(int i = 0; i < 6; i++){
            if(tileDMXCable.sides[i]){
                quads.addAll(center.get(i));
                if(i == DOWN ){
                    if(tileDMXCable.sides[NORTH] || tileDMXCable.isConnected(EnumFacing.NORTH, i)){
                        quads.addAll(north.get(i));
                    }
                    if(tileDMXCable.sides[SOUTH] || tileDMXCable.isConnected(EnumFacing.SOUTH, i)){
                        quads.addAll(south.get(i));
                    }
                    if(tileDMXCable.sides[WEST] || tileDMXCable.isConnected(EnumFacing.WEST, i) ){
                        quads.addAll(west.get(i));
                    }
                    if(tileDMXCable.sides[EAST]|| tileDMXCable.isConnected(EnumFacing.EAST, i)){
                        quads.addAll(east.get(i));
                    }
                }
                if(i == UP){
                    if(tileDMXCable.sides[NORTH]|| tileDMXCable.isConnected(EnumFacing.NORTH, i)){
                        quads.addAll(south.get(i));
                    }
                    if(tileDMXCable.sides[SOUTH] || tileDMXCable.isConnected(EnumFacing.SOUTH, i)){
                        quads.addAll(north.get(i));
                    }
                    if(tileDMXCable.sides[WEST] || tileDMXCable.isConnected(EnumFacing.WEST, i)){
                        quads.addAll(west.get(i));
                    }
                    if(tileDMXCable.sides[EAST]|| tileDMXCable.isConnected(EnumFacing.EAST, i)){
                        quads.addAll(east.get(i));
                    }
                }
                if(i == NORTH){
                    if(tileDMXCable.sides[UP] || tileDMXCable.isConnected(EnumFacing.UP, i)){
                        quads.addAll(south.get(i));
                    }
                    if(tileDMXCable.sides[DOWN] || tileDMXCable.isConnected(EnumFacing.DOWN, i)){
                        quads.addAll(north.get(i));
                    }
                    if(tileDMXCable.sides[WEST] || tileDMXCable.isConnected(EnumFacing.WEST, i)){
                        quads.addAll(east.get(i));
                    }
                    if(tileDMXCable.sides[EAST]  || tileDMXCable.isConnected(EnumFacing.EAST, i)){
                        quads.addAll(west.get(i));
                    }
                }
                if(i == SOUTH){
                    if(tileDMXCable.sides[UP] || tileDMXCable.isConnected(EnumFacing.UP, i)){
                        quads.addAll(south.get(i));
                    }
                    if(tileDMXCable.sides[DOWN]|| tileDMXCable.isConnected(EnumFacing.DOWN, i)){
                        quads.addAll(north.get(i));
                    }
                    if(tileDMXCable.sides[WEST]|| tileDMXCable.isConnected(EnumFacing.WEST, i)){
                        quads.addAll(west.get(i));
                    }
                    if(tileDMXCable.sides[EAST]|| tileDMXCable.isConnected(EnumFacing.EAST, i)){
                        quads.addAll(east.get(i));
                    }
                }
                if(i == EAST){
                    if(tileDMXCable.sides[UP]  || tileDMXCable.isConnected(EnumFacing.UP, i)){
                        quads.addAll(south.get(i));
                    }
                    if(tileDMXCable.sides[DOWN]  || tileDMXCable.isConnected(EnumFacing.DOWN, i)){
                        quads.addAll(north.get(i));
                    }
                    if(tileDMXCable.sides[NORTH] || tileDMXCable.isConnected(EnumFacing.NORTH, i)){
                        quads.addAll(east.get(i));
                    }
                    if(tileDMXCable.sides[SOUTH] || tileDMXCable.isConnected(EnumFacing.SOUTH, i)){
                        quads.addAll(west.get(i));
                    }
                }
                if(i == WEST){
                    if(tileDMXCable.sides[UP] || tileDMXCable.isConnected(EnumFacing.UP, i)){
                        quads.addAll(south.get(i));
                    }
                    if(tileDMXCable.sides[DOWN] || tileDMXCable.isConnected(EnumFacing.DOWN, i)){
                        quads.addAll(north.get(i));
                    }
                    if(tileDMXCable.sides[NORTH] || tileDMXCable.isConnected(EnumFacing.NORTH, i)){
                        quads.addAll(west.get(i));
                    }
                    if(tileDMXCable.sides[SOUTH] || tileDMXCable.isConnected(EnumFacing.SOUTH, i)){
                        quads.addAll(east.get(i));
                    }
                }
                BlockPos pos = tileDMXCable.getPos().offset(EnumFacing.byIndex(i));
                for(EnumFacing facing : EnumFacing.VALUES){
                    BlockPos offset = pos.offset(facing);
                    IBlockState state1 =  tileDMXCable.getWorld().getBlockState(offset);
                    if(state1.getBlock() instanceof BlockDMXCable){
                        if(tileDMXCable.getWorld().getTileEntity(offset) != null && tileDMXCable.getWorld().getTileEntity(offset) instanceof TileDMXCable){
                            TileDMXCable tileDMXCable1 =  (TileDMXCable) tileDMXCable.getWorld().getTileEntity(offset);
                            if(tileDMXCable1.sides[facing.getOpposite().getIndex()]){
                                quads.addAll(getFromFacing(facing, i).get(i));
                            }
                        }
                    }
                }
            }
        }
//        int connections = 0;
//        for(EnumFacing facing : EnumFacing.values()){
//            if(tileDMXCable.isConnected(facing)){
//                connections |= 1 << facing.getIndex();
//            }
//        }
//        for (int i = 0; i < 6; i++)
//        {
//            if ((connections & (1 << i)) != 0)
//            {
//                quads.addAll(getFromFacing(EnumFacing.byIndex(i)));
//            }
//        }
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
