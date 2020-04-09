package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.WorldSocapexNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityPowerCable extends TileEntity implements IEnergyStorage, ITickableTileEntity {

    public TileEntityPowerCable() {
        super(TheatricalTiles.POWER_CABLE.get());
    }

    public int power = 0;
    private int transferRate = 6000;

    public int[] channels = new int[8];
    public int capacityPerChannel = 255;

    private int ticksSinceLastSend = 0;

    private ArrayList<Direction> sendingFace = new ArrayList<Direction>();


    public CompoundNBT writeNBT(CompoundNBT nbtTagCompound) {
        return nbtTagCompound;
    }

    public void readNBT(CompoundNBT nbt) {
//        this.cableTypes = Arrays.stream(nbt.getIntArray("types")).mapToObj(CableType::byIndex).toArray(CableType[]::new);
        if (nbt.contains("power")) {
            power = nbt.getInt("power");
        }
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        readNBT(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        return super.write(writeNBT(compound));
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        CompoundNBT tag = pkt.getNbtCompound();
        readNBT(tag);
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        readNBT(tag);
    }

    public boolean isConnected(Direction direction, CableType cableType) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(direction));
        if (tileEntity == null) {
            return false;
        }
        if (tileEntity instanceof TileEntityPowerCable) {
            TileEntityPowerCable tileEntityPowerCable = (TileEntityPowerCable) tileEntity;
            return tileEntityPowerCable.hasType(cableType);
        }
        if (hasType(CableType.POWER)) {
            if (tileEntity.getCapability(CapabilityEnergy.ENERGY, direction.getOpposite()).isPresent()) {
                return true;
            }
        }
        if (tileEntity instanceof IAcceptsCable) {
            if (hasAnyType(((IAcceptsCable) tileEntity).getAcceptedCables())) {
                return true;
            }
        }
        return tileEntity.getCapability(DMXReceiver.CAP, direction.getOpposite()).isPresent() || tileEntity.getCapability(
            DMXProvider.CAP, direction.getOpposite()).isPresent();
    }


    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityEnergy.ENERGY) {
            if (side == null && hasType(CableType.POWER)) {
                return LazyOptional.of(() -> (T) this);
            }
            if (hasType(CableType.POWER) && isConnected(side, CableType.POWER)) {
                return LazyOptional.of(() -> (T) this);
            }
        }
        if (cap == SocapexReceiver.CAP) {
            if (side == null && hasType(CableType.SOCAPEX)) {
                return LazyOptional.empty();
            }
            if (hasType(CableType.SOCAPEX) && isConnected(side, CableType.SOCAPEX)) {
                return LazyOptional.empty();
            }
        }
        return super.getCapability(cap, side);
    }


    @Override
    public void remove() {
        if (hasWorld()) {
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> {
                worldDMXNetwork.setRefresh(true);
            });
            world.getCapability(WorldSocapexNetwork.CAP).ifPresent(worldSocapexNetwork -> {
                worldSocapexNetwork.setRefresh(true);
            });
        }
        super.remove();
    }

    @Override
    public void setWorldAndPos(World p_226984_1_, BlockPos p_226984_2_) {
        super.setWorldAndPos(p_226984_1_, p_226984_2_);
        if (hasWorld()) {
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> {
                worldDMXNetwork.setRefresh(true);
            });
            world.getCapability(WorldSocapexNetwork.CAP).ifPresent(worldSocapexNetwork -> {
                worldSocapexNetwork.setRefresh(true);
            });
        }
    }


    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(transferRate, maxReceive));
        if (!simulate) {
            power += energyReceived;
        }
//        world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        int energyExtracted = Math.min(getEnergyStored(), Math.min(transferRate, maxExtract));
        if (!simulate) {
            power -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return power;
    }

    @Override
    public int getMaxEnergyStored() {
        return 5;
    }

    @Override
    public boolean canExtract() {
        return getEnergyStored() != 0;
    }

    @Override
    public boolean canReceive() {
        return getMaxEnergyStored() != getEnergyStored();
    }

    public boolean hasType(CableType type) {
//        for (CableType cType : cableTypes) {
//            if (cType != null) {
//                if(cType == type){
//                    return true;
//                }
//            }
//
//        }
        return true;
    }

    public boolean hasAnyType(CableType[] types) {
        for (CableType type : types) {
            if (type != CableType.NONE) {
                if (this.hasType(type)) {
                    return true;
                }
            }
        }
        return false;
    }

//    public CableType getFirstCableType(){
//        return Arrays.stream(cableTypes).filter(Objects::nonNull).toArray(CableType[]::new)[0];
//    }
//
//    public void removeType(CableType type){
//        if(!hasType(type)){
//            return;
//        }
//        this.cableTypes = Arrays.stream(this.cableTypes).filter(cableType -> cableType != type).toArray(CableType[]::new);
//        if(getTotalTypes() > 1){
//            world.setBlockState(pos, world.getBlockState(pos).with(CableBlock.CABLE_TYPE, CableType.BUNDLED));
//        } else {
//            world.setBlockState(pos, world.getBlockState(pos).with(CableBlock.CABLE_TYPE, getFirstCableType()));
//        }
//    }
//
//    public int getTotalTypes(){
//        return (int) Arrays.stream(this.cableTypes).filter(Objects::nonNull).count();
//    }
//
//    public boolean addType(CableType type){
//        if(!hasType(type)){
//            int availableSlot = -1;
//            for(int i = 0; i < cableTypes.length; i++){
//                if(cableTypes[i] == null || cableTypes[i].getIndex() == CableType.NONE.getIndex()){
//                    availableSlot = i;
//                    break;
//                }
//            }
//            if(availableSlot != -1){
//                cableTypes[availableSlot] = type;
//                if(getTotalTypes() > 1){
//                    world.setBlockState(pos, world.getBlockState(pos).with(CableBlock.CABLE_TYPE, CableType.BUNDLED));
//                } else {
//                    world.setBlockState(pos, world.getBlockState(pos).with(CableBlock.CABLE_TYPE, getFirstCableType()));
//                }
//                return true;
//            }
//        }
//        return false;
//    }

    public boolean canReceiveFromFace(Direction facing) {
        if (hasType(CableType.POWER)) {
            return false;
        }
        if (sendingFace.contains(facing)) {
            return false;
        }
        return canReceive();
    }

    public void doEnergyTransfer() {
        if (!hasType(CableType.POWER)) {
            return;
        }
        if (!canExtract()) {
            return;
        }
        ArrayList<IEnergyStorage> acceptors = new ArrayList<>();
        for (Direction face : Direction.values()) {
            BlockPos newPos = pos.offset(face);
            TileEntity tile = world.getTileEntity(newPos);
            if (tile == null) {
                if (face != Direction.UP && face != Direction.DOWN) {
                    tile = world.getTileEntity(newPos.offset(Direction.UP));
                    if (tile == null) {
                        tile = world.getTileEntity(newPos.offset(Direction.DOWN));
                        if (tile == null) {
                            continue;
                        }
                    }
                }
            }
            if (tile == null) {
                continue;
            } else if (tile instanceof TileEntityPowerCable) {
                TileEntityPowerCable cable = (TileEntityPowerCable) tile;
                if (cable.hasType(CableType.POWER) && power > cable.power && cable.canReceiveFromFace(face.getOpposite())) {
                    acceptors.add((IEnergyStorage) tile);
                    if (!sendingFace.contains(face)) {
                        sendingFace.add(face);
                    }
                }
            } else if (tile.getCapability(CapabilityEnergy.ENERGY, face.getOpposite()) != null) {
                IEnergyStorage energyTile = tile.getCapability(CapabilityEnergy.ENERGY, face.getOpposite()).orElse(null);
                if (energyTile != null && energyTile.canReceive()) {
                    acceptors.add(energyTile);
                }
            }
        }
        if (acceptors.size() > 0) {
            for (IEnergyStorage tile : acceptors) {
                int drain = Math.min(power, transferRate);
                if (drain > 0 && tile.receiveEnergy(drain, true) > 0) {
                    int move = tile.receiveEnergy(drain, false);
                    extractEnergy(move, false);
                }
            }
        }
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        ticksSinceLastSend++;
        if (ticksSinceLastSend >= 10) {
            sendingFace.clear();
            ticksSinceLastSend = 0;
        }

        doEnergyTransfer();
    }

}
