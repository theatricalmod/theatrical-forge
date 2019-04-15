package dev.theatricalmod.theatrical.client.models.truss;

import dev.theatricalmod.theatrical.blocks.rigging.BlockSquareTruss;
import dev.theatricalmod.theatrical.tiles.rigging.TileSquareTruss;
import dev.theatricalmod.theatrical.util.ClientUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Nullable;
import javax.vecmath.Matrix4f;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelRotation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

public class ModelTrussBaked implements IBakedModel {

    public final TextureAtlasSprite particle;
    public final List<BakedQuad> north, south, west, east, down, up;


    public ModelTrussBaked(ModelTruss truss, TextureAtlasSprite p, ModelTruss.ModelCallback c){
        particle = p;
        north = c.get(truss.modelNorth, ModelRotation.X0_Y0);
        south = c.get(truss.modelSouth, ModelRotation.X0_Y0);
        west = c.get(truss.modelWest, ModelRotation.X0_Y0);
        east = c.get(truss.modelEast, ModelRotation.X0_Y0);
        up = c.get(truss.modelUp, ModelRotation.X0_Y0);
        down = c.get(truss.modelDown, ModelRotation.X0_Y0);
    }


    public boolean isConnected(EnumFacing facing, BlockPos pos, World world){
        return world.getBlockState(pos.offset(facing)).getBlock() instanceof BlockSquareTruss;
    }

    public List<BakedQuad> getQuads(EnumFacing facing){
        switch(facing){
            case EAST:
                return east;
            case DOWN:
                return down;
            case UP:
                return up;
            case SOUTH:
                return south;
            case NORTH:
                return north;
            case WEST:
                return west;
        }
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side,
        long rand) {

        TileSquareTruss tileSquareTruss = null;

        if(state instanceof IExtendedBlockState){
            tileSquareTruss = ((IExtendedBlockState)state).getValue(BlockSquareTruss.TRUSS);
        }

        if(tileSquareTruss == null){
            return Collections.emptyList();
        }
        HashMap<EnumFacing, List<BakedQuad>> quads = new HashMap<>();
        BlockPos pos = tileSquareTruss.getPos();
        World world = tileSquareTruss.getWorld();
        for(EnumFacing facing : EnumFacing.VALUES){
            if(!isConnected(facing, pos, world)){
                quads.put(facing, getQuads(facing));
            }
        }
        if(isConnected(EnumFacing.DOWN, pos, world)){
            for(EnumFacing facing : EnumFacing.HORIZONTALS){
                if(quads.containsKey(facing)) {
                    List<BakedQuad> quads1 = new ArrayList<>(quads.get(facing));
                    quads1.remove(18);
                    quads1.remove(18);
                    quads1.remove(18);
                    quads1.remove(18);
                    quads1.remove(18);
                    quads1.remove(18);
                    quads.replace(facing, quads1);
                }
            }
        }
        List<BakedQuad> actualQuads = new ArrayList<>();
        for(List<BakedQuad> quad : quads.values()){
            actualQuads.addAll(quad);
        }
        return ClientUtils.optimize(actualQuads);
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
        return ItemOverrideList.NONE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(
        TransformType cameraTransformType) {
        return ModelTruss.handleModelPerspective(this, cameraTransformType);
    }
}
