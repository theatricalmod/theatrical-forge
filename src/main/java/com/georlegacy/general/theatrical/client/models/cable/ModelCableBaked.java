package com.georlegacy.general.theatrical.client.models.cable;

import com.georlegacy.general.theatrical.blocks.cables.BlockCable;
import com.georlegacy.general.theatrical.items.ItemDMXCable;
import com.georlegacy.general.theatrical.tiles.cables.CableSide;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import com.georlegacy.general.theatrical.tiles.cables.TileCable;
import com.georlegacy.general.theatrical.util.ClientUtils;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.Pair;

public class ModelCableBaked implements IBakedModel {

    public final TextureAtlasSprite particle;
    public final Map<CableType, List<List<List<BakedQuad>>>> north, south, west, east, center;
    private final IBakedModel bakedItem;
    private final ItemOverrideList itemOverrideList;

    int NORTH = EnumFacing.NORTH.getIndex();
    int SOUTH = EnumFacing.SOUTH.getIndex();
    int EAST = EnumFacing.EAST.getIndex();
    int WEST = EnumFacing.WEST.getIndex();
    int DOWN = EnumFacing.DOWN.getIndex();
    int UP = EnumFacing.UP.getIndex();

    public ModelCableBaked(ModelCable cable, TextureAtlasSprite p, ModelCable.ModelCallback c) {
        particle = p;
        north = new HashMap<>();
        south = new HashMap<>();
        west = new HashMap<>();
        east = new HashMap<>();
        center = new HashMap<>();
        for (CableType cableType : CableType.values()) {
            if (cableType == CableType.NONE) {
                continue;
            }

            List<List<List<BakedQuad>>> multiplesNorth = new ArrayList<>();
            List<List<List<BakedQuad>>> multiplesSouth = new ArrayList<>();
            List<List<List<BakedQuad>>> multiplesEast = new ArrayList<>();
            List<List<List<BakedQuad>>> multiplesWest = new ArrayList<>();
            List<List<List<BakedQuad>>> multiplesCenter = new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                List<List<BakedQuad>> facingsNorth = new ArrayList<>();
                List<List<BakedQuad>> facingsSouth = new ArrayList<>();
                List<List<BakedQuad>> facingsEast = new ArrayList<>();
                List<List<BakedQuad>> facingsWest = new ArrayList<>();
                List<List<BakedQuad>> facingsCenter = new ArrayList<>();
                for (EnumFacing facing : EnumFacing.VALUES) {
                    AbstractMap.SimpleEntry<String, ResourceLocation> entry = new AbstractMap.SimpleEntry<>("0", cableType.getTexture());
                    facingsNorth.add(c.get(cable.modelNorth.get(i), ModelCable.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsSouth.add(c.get(cable.modelSouth.get(i), ModelCable.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsEast.add(c.get(cable.modelEast.get(i), ModelCable.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsWest.add(c.get(cable.modelWest.get(i), ModelCable.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsCenter.add(c.get(cable.modelCenter.get(i), ModelCable.FACE_ROTATIONS[facing.getIndex()], entry));
                }
                multiplesNorth.add(facingsNorth);
                multiplesSouth.add(facingsSouth);
                multiplesEast.add(facingsEast);
                multiplesWest.add(facingsWest);
                multiplesCenter.add(facingsCenter);
            }
            north.put(cableType, multiplesNorth);
            south.put(cableType, multiplesSouth);
            west.put(cableType, multiplesWest);
            east.put(cableType, multiplesEast);
            center.put(cableType, multiplesCenter);
        }
        bakedItem = new ModelCableBakedItem(this);

        itemOverrideList = new ItemOverrideList(Collections.emptyList()) {
            @Override
            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack,
                @Nullable World world, @Nullable EntityLivingBase entity) {
                return stack.getItem() instanceof ItemDMXCable ? bakedItem : originalModel;
            }
        };
    }

    public List<List<BakedQuad>> getFromFacing(EnumFacing facing, int face, int multiple, CableType cableType) {
        if (face == 0) {
            switch (facing.getIndex()) {
                case 3:
                    return south.get(cableType).get(multiple);
                case 2:
                    return north.get(cableType).get(multiple);
                case 5:
                    return east.get(cableType).get(multiple);
                case 4:
                    return west.get(cableType).get(multiple);
            }
        } else if (face == 1) {
            switch (facing.getIndex()) {
                case 3:
                    return north.get(cableType).get(multiple);
                case 2:
                    return south.get(cableType).get(multiple);
                case 5:
                    return east.get(cableType).get(multiple);
                case 4:
                    return west.get(cableType).get(multiple);
            }
        } else if (face == 2) {
            switch (facing.getIndex()) {
                case 1:
                    return south.get(cableType).get(multiple);
                case 0:
                    return north.get(cableType).get(multiple);
                case 5:
                    return west.get(cableType).get(multiple);
                case 4:
                    return east.get(cableType).get(multiple);
            }
        } else if (face == 3) {
            switch (facing.getIndex()) {
                case 1:
                    return south.get(cableType).get(multiple);
                case 0:
                    return north.get(cableType).get(multiple);
                case 5:
                    return east.get(cableType).get(multiple);
                case 4:
                    return west.get(cableType).get(multiple);
            }
        } else if (face == 4) {
            switch (facing.getIndex()) {
                case 1:
                    return south.get(cableType).get(multiple);
                case 0:
                    return north.get(cableType).get(multiple);
                case 2:
                    return west.get(cableType).get(multiple);
                case 3:
                    return east.get(cableType).get(multiple);
            }
        } else if (face == 5) {
            switch (facing.getIndex()) {
                case 1:
                    return south.get(cableType).get(multiple);
                case 0:
                    return north.get(cableType).get(multiple);
                case 2:
                    return east.get(cableType).get(multiple);
                case 3:
                    return west.get(cableType).get(multiple);
            }
        }
        return Collections.emptyList();
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable IBlockState state, @Nullable EnumFacing side,
        long rand) {

        TileCable tileCable = null;

        if (state instanceof IExtendedBlockState) {
            tileCable = ((IExtendedBlockState) state).getValue(BlockCable.CABLE);
        }

        if (tileCable == null) {
            return Collections.emptyList();
        }
        ArrayList<BakedQuad> quads = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (tileCable.hasSide(i)) {
                CableSide side1 = tileCable.sides[i];
                int size = side1.getTotalTypes() - 1;
                CableType type = CableType.BUNDLED;
                if (size == 0 && side1.getFirstType() != null) {
                    type = side1.getFirstType();
                }
                if (size < 0) {
                    return Collections.emptyList();
                }
                quads.addAll(center.get(type).get(size).get(i));
//                        if( tileCable.isConnected(EnumFacing.NORTH, i)  || tileCable.isConnected(EnumFacing.SOUTH, i)){
//                            quads.addAll(center_x.get(x).get(i));
//                            hasCenter = true;
//                        }else if(tileCable.isConnected(EnumFacing.EAST, i)  || tileCable.isConnected(EnumFacing.WEST, i)){
//                            quads.addAll(center_z.get(x).get(i));
//                            hasCenter = true;
//                        }
                if (i == DOWN) {
                    if (tileCable.hasSide(NORTH) || tileCable.isConnected(EnumFacing.NORTH, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(SOUTH) || tileCable.isConnected(EnumFacing.SOUTH, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(WEST) || tileCable.isConnected(EnumFacing.WEST, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(EAST) | tileCable.isConnected(EnumFacing.EAST, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == UP) {
                    if (tileCable.hasSide(NORTH) || tileCable.isConnected(EnumFacing.NORTH, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(SOUTH) || tileCable.isConnected(EnumFacing.SOUTH, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(WEST) || tileCable.isConnected(EnumFacing.WEST, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(EAST) || tileCable.isConnected(EnumFacing.EAST, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == NORTH) {
                    if (tileCable.hasSide(UP) || tileCable.isConnected(EnumFacing.UP, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(DOWN) || tileCable.isConnected(EnumFacing.DOWN, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(WEST) || tileCable.isConnected(EnumFacing.WEST, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(EAST) || tileCable.isConnected(EnumFacing.EAST, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                }
                if (i == SOUTH) {
                    if (tileCable.hasSide(UP) || tileCable.isConnected(EnumFacing.UP, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(DOWN) || tileCable.isConnected(EnumFacing.DOWN, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(WEST) || tileCable.isConnected(EnumFacing.WEST, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(EAST) || tileCable.isConnected(EnumFacing.EAST, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == EAST) {
                    if (tileCable.hasSide(UP) || tileCable.isConnected(EnumFacing.UP, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(DOWN) || tileCable.isConnected(EnumFacing.DOWN, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(NORTH) || tileCable.isConnected(EnumFacing.NORTH, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(SOUTH) || tileCable.isConnected(EnumFacing.SOUTH, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                }
                if (i == WEST) {
                    if (tileCable.hasSide(UP) || tileCable.isConnected(EnumFacing.UP, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(DOWN) || tileCable.isConnected(EnumFacing.DOWN, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(NORTH) || tileCable.isConnected(EnumFacing.NORTH, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (tileCable.hasSide(SOUTH) || tileCable.isConnected(EnumFacing.SOUTH, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                BlockPos pos = tileCable.getPos().offset(EnumFacing.byIndex(i));
                for (EnumFacing facing : EnumFacing.VALUES) {
                    BlockPos offset = pos.offset(facing);
                    IBlockState state1 = tileCable.getWorld().getBlockState(offset);
                    if (state1.getBlock() instanceof BlockCable) {
                        if (tileCable.getWorld().getTileEntity(offset) != null && tileCable.getWorld().getTileEntity(offset) instanceof TileCable) {
                            TileCable tileCable1 = (TileCable) tileCable.getWorld().getTileEntity(offset);
                            if (tileCable1.hasSide(facing.getOpposite().getIndex()) && tileCable1.sides[facing.getOpposite().getIndex()].hasAnyType(side1.getTypes())) {
                                List<List<BakedQuad>> quads1 = getFromFacing(facing, i, size, type);
                                if (quads1.size() > 0) {
                                    quads.addAll(quads1.get(i));
                                }
                            }
                        }
                    }
                }
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
