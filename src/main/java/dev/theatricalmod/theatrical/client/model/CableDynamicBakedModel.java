package dev.theatricalmod.theatrical.client.model;

import dev.theatricalmod.theatrical.api.CableSide;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.util.ClientUtils;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

public class CableDynamicBakedModel implements IDynamicBakedModel {
    public final TextureAtlasSprite particle;
    public final Map<CableType, List<List<List<BakedQuad>>>> north, south, west, east, center;
//    private final IBakedModel bakedItem;
//    private final ItemOverrideList itemOverrideList;

    int NORTH = Direction.NORTH.getIndex();
    int SOUTH = Direction.SOUTH.getIndex();
    int EAST = Direction.EAST.getIndex();
    int WEST = Direction.WEST.getIndex();
    int DOWN = Direction.DOWN.getIndex();
    int UP = Direction.UP.getIndex();

    public CableDynamicBakedModel(CableModelGeometry cable, TextureAtlasSprite p, CableModelGeometry.ModelCallback c) {
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
                for (Direction facing : Direction.values()) {
                    AbstractMap.SimpleEntry<String, ResourceLocation> entry = new AbstractMap.SimpleEntry<>("0", cableType.getTexture());
                    facingsNorth.add(c.get(cable.modelNorth.get(i), CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsSouth.add(c.get(cable.modelSouth.get(i), CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsEast.add(c.get(cable.modelEast.get(i), CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsWest.add(c.get(cable.modelWest.get(i), CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], entry));
                    facingsCenter.add(c.get(cable.modelCenter.get(i), CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], entry));
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
//        bakedItem = new ModelCableBakedItem(this);

//        itemOverrideList = new ItemOverrideList(Collections.emptyList()) {
//            @Override
//            public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack,
//                @Nullable World world, @Nullable EntityLivingBase entity) {
//                return stack.getItem() instanceof ItemDMXCable ? bakedItem : originalModel;
//            }
//        };
    }

    public List<List<BakedQuad>> getFromFacing(Direction facing, int face, int multiple, CableType cableType) {
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
    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        if(!extraData.hasProperty(CableModelData.MODEL_DATA)){
            return Collections.emptyList();
        }
        CableModelData cableModelData = extraData.getData(CableModelData.MODEL_DATA);
        if (cableModelData == null) {
            return Collections.emptyList();
        }
        ArrayList<BakedQuad> quads = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            if (cableModelData.hasSide(i)) {
                CableSide side1 = cableModelData.getSides()[i];
                int size = side1.getTotalTypes() - 1;
                CableType type = CableType.BUNDLED;
                if (size == 0 && side1.getFirstType() != null) {
                    type = side1.getFirstType();
                }
                if (size < 0) {
                    return Collections.emptyList();
                }
                quads.addAll(center.get(type).get(size).get(i));
//                        if( cableModelData.isConnected(Direction.NORTH, i)  || cableModelData.isConnected(Direction.SOUTH, i)){
//                            quads.addAll(center_x.get(x).get(i));
//                            hasCenter = true;
//                        }else if(cableModelData.isConnected(Direction.EAST, i)  || cableModelData.isConnected(Direction.WEST, i)){
//                            quads.addAll(center_z.get(x).get(i));
//                            hasCenter = true;
//                        }
                if (i == DOWN) {
                    if (cableModelData.hasSide(NORTH) || cableModelData.isConnected(Direction.NORTH, i, type)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(SOUTH) || cableModelData.isConnected(Direction.SOUTH, i, type)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(WEST) || cableModelData.isConnected(Direction.WEST, i, type)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(EAST) | cableModelData.isConnected(Direction.EAST, i, type)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == UP) {
                    if (cableModelData.hasSide(NORTH) || cableModelData.isConnected(Direction.NORTH, i, type)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(SOUTH) || cableModelData.isConnected(Direction.SOUTH, i, type)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(WEST) || cableModelData.isConnected(Direction.WEST, i, type)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(EAST) || cableModelData.isConnected(Direction.EAST, i, type)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == NORTH) {
                    if (cableModelData.hasSide(UP) || cableModelData.isConnected(Direction.UP, i, type)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(DOWN) || cableModelData.isConnected(Direction.DOWN, i, type)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(WEST) || cableModelData.isConnected(Direction.WEST, i, type)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(EAST) || cableModelData.isConnected(Direction.EAST, i, type)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                }
                if (i == SOUTH) {
                    if (cableModelData.hasSide(UP) || cableModelData.isConnected(Direction.UP, i, type)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(DOWN) || cableModelData.isConnected(Direction.DOWN, i, type)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(WEST) || cableModelData.isConnected(Direction.WEST, i, type)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(EAST) || cableModelData.isConnected(Direction.EAST, i, type)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == EAST) {
                    if (cableModelData.hasSide(UP) || cableModelData.isConnected(Direction.UP, i, type)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(DOWN) || cableModelData.isConnected(Direction.DOWN, i, type)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(NORTH) || cableModelData.isConnected(Direction.NORTH, i, type)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(SOUTH) || cableModelData.isConnected(Direction.SOUTH, i, type)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                }
                if (i == WEST) {
                    if (cableModelData.hasSide(UP) || cableModelData.isConnected(Direction.UP, i, type)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(DOWN) || cableModelData.isConnected(Direction.DOWN, i, type)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(NORTH) || cableModelData.isConnected(Direction.NORTH, i, type)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (cableModelData.hasSide(SOUTH) || cableModelData.isConnected(Direction.SOUTH, i, type)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
//                BlockPos pos = cableModelData.getPos().offset(Direction.byIndex(i));
//                for (Direction facing : Direction.VALUES) {
//                    BlockPos offset = pos.offset(facing);
//                    IBlockState state1 = cableModelData.getWorld().getBlockState(offset);
//                    if (state1.getBlock() instanceof BlockCable) {
//                        if (cableModelData.getWorld().getTileEntity(offset) != null && cableModelData.getWorld().getTileEntity(offset) instanceof TileCable) {
//                            TileCable tileCable1 = (TileCable) cableModelData.getWorld().getTileEntity(offset);
//                            if (tileCable1.hasSide(facing.getOpposite().getIndex()) && tileCable1.sides[facing.getOpposite().getIndex()].hasAnyType(side1.getTypes())) {
//                                List<List<BakedQuad>> quads1 = getFromFacing(facing, i, size, type);
//                                if (quads1.size() > 0) {
//                                    quads.addAll(quads1.get(i));
//                                }
//                            }
//                        }
//                    }
//                }
            }
        }

        return ClientUtils.optimize(quads);
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean func_230044_c_() {
        return false;
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
        return null;
    }
}
