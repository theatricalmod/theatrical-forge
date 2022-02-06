package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.CableSide;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.WorldSocapexNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraftforge.client.model.data.IModelData;
import net.minecraftforge.client.model.data.ModelDataMap;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import java.util.EnumMap;
import java.util.Map;

public class TileEntityCable extends TileEntityTheatricalBase {

    private static final ModelProperty<CableSide> NORTH_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<CableSide> SOUTH_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<CableSide> EAST_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<CableSide> WEST_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<CableSide> UP_PROPERTY = new ModelProperty<>();
    private static final ModelProperty<CableSide> DOWN_PROPERTY = new ModelProperty<>();

    public static final EnumMap<Direction, ModelProperty<CableSide>> PROPERTY_MAP = Util.make(new EnumMap<>(Direction.class), m -> {
        m.put(Direction.NORTH, NORTH_PROPERTY);
        m.put(Direction.SOUTH, SOUTH_PROPERTY);
        m.put(Direction.EAST, EAST_PROPERTY);
        m.put(Direction.WEST, WEST_PROPERTY);
        m.put(Direction.UP, UP_PROPERTY);
        m.put(Direction.DOWN, DOWN_PROPERTY);
    });

    private CableSide[] sides = new CableSide[6];

    public TileEntityCable() {
        super(TheatricalTiles.CABLE.get());
    }

    @Override
    public void validate() {
        super.validate();
        updateWorldNetworks();
    }

    @Override
    public void remove() {
        updateWorldNetworks();
        super.remove();
    }

    @Override
    public void readNBT(CompoundNBT nbt) {
        for(int i = 0; i < 6; i++){
            if(nbt.contains("side_" + i)){
                sides[i] = readSideNBT(nbt.getCompound("side_" + i));
            }
        }
    }

    public CableSide getSide(int i) {
        return sides[i];
    }

    public void setSide(int i, CableSide side) {
        sides[i] = side;
    }

    public CableSide newSide(){
        return new CableSide(this::isConnected);
    }

    public CableSide readSideNBT(CompoundNBT nbtTagCompound){
        CableSide side = new CableSide(this::isConnected);
        CableType[] cableTypes = new CableType[5];
        if(nbtTagCompound.contains("types")){
            int[] types = nbtTagCompound.getIntArray("types");
            for(int i = 0; i < types.length; i++){
                cableTypes[i] = CableType.byIndex(types[i]);
            }
        }
        side.setTypes(cableTypes);
        return side;
    }

    @Override
    public CompoundNBT getNBT(CompoundNBT nbt) {
        nbt = super.getNBT(nbt);
        for(int i = 0; i < sides.length; i++) {
            if(hasSide(i)) {
                nbt.put("side_" + i, sides[i].getNBT());
            }
        }
        return nbt;
    }

    public boolean hasSide(int side){
        return sides[side] != null;
    }

    @Nonnull
    @Override
    public IModelData getModelData() {
        ModelDataMap.Builder builder = new ModelDataMap.Builder();
        for(Map.Entry<Direction, ModelProperty<CableSide>> entry : PROPERTY_MAP.entrySet()) {
            Direction dir = entry.getKey();
            ModelProperty<CableSide> property = entry.getValue();
            CableSide cableSide = sides[dir.getIndex()];
            builder.withInitial(property, cableSide);
        }
        return builder.build();
    }

    private boolean isConnected(Direction direction, CableType type, int side) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(direction));
        if(tileEntity == null){
            return false;
        }
        if(tileEntity instanceof TileEntityCable){
            TileEntityCable tileCable = (TileEntityCable)tileEntity;
            if (!tileCable.hasSide(side)) {
                return false;
            }
            boolean hasType = false;
            for (CableType cableType : sides[side].getTypes()) {
                if (cableType != CableType.NONE) {
                    if (!hasType) {
                        if (tileCable.getSide(side).hasType(cableType)) {
                            hasType = true;
                        }
                    }
                }
            }
            return hasType;
        }
        if(direction == Direction.EAST || direction == Direction.WEST || direction == Direction.NORTH || direction == Direction.SOUTH){
            if(!hasSide(0) && !hasSide(1)){
                return false;
            }
        } else {
            if(!hasSide(2) && !hasSide(3) && !hasSide(4) && !hasSide(5)){
                return false;
            }
        }
        if (sides[side] != null && sides[side].hasType(CableType.POWER)) {
            if (tileEntity.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).isPresent()) {
                return true;
            }
        }
        if (tileEntity instanceof IAcceptsCable) {
            if (sides[side].hasAnyType(((IAcceptsCable) tileEntity).getAcceptedCables(direction.getOpposite()))) {
                return true;
            }
        }
        return tileEntity.getCapability(DMXReceiver.CAP, direction.getOpposite()).isPresent() || tileEntity.getCapability(
                DMXProvider.CAP, direction.getOpposite()).isPresent();
    }

    private void updateWorldNetworks() {
//        if(hasWorld()){
//            if(type == CableType.SOCAPEX){
//                world.getCapability(WorldSocapexNetwork.CAP).ifPresent(worldDMXNetwork -> {
//                    worldDMXNetwork.setRefresh(true);
//                });
//            } else if(type == CableType.DMX){
//                world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> {
//                    worldDMXNetwork.setRefresh(true);
//                });
//            }
//        }
    }
}
