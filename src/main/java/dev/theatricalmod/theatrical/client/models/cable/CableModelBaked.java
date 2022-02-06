package dev.theatricalmod.theatrical.client.models.cable;

import dev.theatricalmod.theatrical.api.CableSide;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.tiles.TileEntityCable;

import java.util.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.data.IDynamicBakedModel;
import net.minecraftforge.client.model.data.IModelData;

public class CableModelBaked implements IDynamicBakedModel {

    public final TextureAtlasSprite particle;
    public final Map<CableType, List<List<List<BakedQuad>>>> north, south, west, east, center;

    int NORTH = Direction.NORTH.getIndex();
    int SOUTH = Direction.SOUTH.getIndex();
    int EAST = Direction.EAST.getIndex();
    int WEST = Direction.WEST.getIndex();
    int DOWN = Direction.DOWN.getIndex();
    int UP = Direction.UP.getIndex();

    public CableModelBaked(CableModelGeometry cable, TextureAtlasSprite p, CableModelGeometry.ModelCallback c) {
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
                    facingsNorth.add(c.get(cable.north, CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], cableType.getTexture()));
                    facingsSouth.add(c.get(cable.south, CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], cableType.getTexture()));
                    facingsEast.add(c.get(cable.east, CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], cableType.getTexture()));
                    facingsWest.add(c.get(cable.west, CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], cableType.getTexture()));
                    facingsCenter.add(c.get(cable.center, CableModelGeometry.FACE_ROTATIONS[facing.getIndex()], cableType.getTexture()));
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

    @Override
    public boolean isAmbientOcclusion() {
        return true;
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean isSideLit() {
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

    public boolean hasSide(IModelData modelData, int side) {
        Direction direction = Direction.byIndex(side);
        CableSide cableSide = modelData.getData(TileEntityCable.PROPERTY_MAP.get(direction));
        return cableSide != null;
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        ArrayList<BakedQuad> quads = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            Direction direction = Direction.byIndex(i);
            CableSide cableSide = extraData.getData(TileEntityCable.PROPERTY_MAP.get(direction));
            if (cableSide != null) {
                CableType type = cableSide.getFirstType();
                if (cableSide.getTotalTypes() > 1) {
                    type = CableType.BUNDLED;
                }
                int size = cableSide.getTotalTypes() - 1;
                if (size < 0) {
                    return Collections.emptyList();
                }
                quads.addAll(center.get(type).get(size).get(i));
                if (i == DOWN) {
                    if (hasSide(extraData, NORTH) || cableSide.isConnected(Direction.NORTH, type, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, SOUTH) || cableSide.isConnected(Direction.SOUTH, type, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, WEST) || cableSide.isConnected(Direction.WEST, type, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, EAST) | cableSide.isConnected(Direction.EAST, type, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == UP) {
                    if (hasSide(extraData, NORTH) || cableSide.isConnected(Direction.NORTH, type, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, SOUTH) || cableSide.isConnected(Direction.SOUTH, type, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, WEST) || cableSide.isConnected(Direction.WEST, type, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, EAST) || cableSide.isConnected(Direction.EAST, type, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == NORTH) {
                    if (hasSide(extraData, UP) || cableSide.isConnected(Direction.UP, type, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, DOWN) || cableSide.isConnected(Direction.DOWN, type, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, WEST) || cableSide.isConnected(Direction.WEST, type, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, EAST) || cableSide.isConnected(Direction.EAST, type, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                }
                if (i == SOUTH) {
                    if (hasSide(extraData, UP) || cableSide.isConnected(Direction.UP, type, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, DOWN) || cableSide.isConnected(Direction.DOWN, type, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, WEST) || cableSide.isConnected(Direction.WEST, type, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, EAST) || cableSide.isConnected(Direction.EAST, type, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
                if (i == EAST) {
                    if (hasSide(extraData, UP) || cableSide.isConnected(Direction.UP, type, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, DOWN) || cableSide.isConnected(Direction.DOWN, type, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, NORTH) || cableSide.isConnected(Direction.NORTH, type, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, SOUTH) || cableSide.isConnected(Direction.SOUTH, type, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                }
                if (i == WEST) {
                    if (hasSide(extraData, UP) || cableSide.isConnected(Direction.UP, type, i)) {
                        quads.addAll(south.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, DOWN) || cableSide.isConnected(Direction.DOWN, type, i)) {
                        quads.addAll(north.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, NORTH) || cableSide.isConnected(Direction.NORTH, type, i)) {
                        quads.addAll(west.get(type).get(size).get(i));
                    }
                    if (hasSide(extraData, SOUTH) || cableSide.isConnected(Direction.SOUTH, type, i)) {
                        quads.addAll(east.get(type).get(size).get(i));
                    }
                }
            }
        }
        return quads;
    }
}
