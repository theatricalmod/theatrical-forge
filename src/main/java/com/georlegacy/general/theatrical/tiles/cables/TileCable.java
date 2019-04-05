package com.georlegacy.general.theatrical.tiles.cables;

import com.georlegacy.general.theatrical.api.IAcceptsCable;
import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.power.bundled.BundledTheatricalPower;
import com.georlegacy.general.theatrical.api.capabilities.power.bundled.IBundledTheatricalPowerStorage;
import java.util.ArrayList;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileCable extends TileEntity implements IEnergyStorage, ITickable, IBundledTheatricalPowerStorage {

    public CableSide[] sides = new CableSide[6];

    public int power = 0;
    private int transferRate = 6000;

    public int[] channels = new int[8];
    public int capacityPerChannel = 255;

    private int ticksSinceLastSend = 0;

    private ArrayList<EnumFacing> sendingFace = new ArrayList<EnumFacing>();

    public TileCable() {
    }

    public NBTTagCompound writeNBT(NBTTagCompound nbtTagCompound){
        for(int i = 0; i < sides.length; i++){
            if(hasSide(i)) {
                nbtTagCompound.setTag("side_" + i, sides[i].getNBT());
            }
        }
        if (hasType(CableType.POWER)) {
            nbtTagCompound.setInteger("power", power);
        }
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbt){
        for(int i = 0; i < 6; i++){
            if(nbt.hasKey("side_" + i)) {
                sides[i] = CableSide.readNBT(nbt.getCompoundTag("side_" + i));
            }
        }
        if (nbt.hasKey("power")) {
            power = nbt.getInteger("power");
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        readNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        return super.writeToNBT(writeNBT(compound));
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, writeToNBT(new NBTTagCompound()));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        return writeNBT(nbtTagCompound);
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        readNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
    }

    public boolean hasSide(int side){
        return sides[side] != null;
    }


    public boolean isConnected(EnumFacing enumFacing, int side, CableType typer) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(enumFacing));
        if (tileEntity == null) {
            return false;
        }
        if (tileEntity instanceof TileCable) {
            TileCable tileCable = (TileCable) tileEntity;
            if (tileCable.sides[side] != null) {
                return tileCable.sides[side].hasType(typer);
            }
            return false;
        }
        if (enumFacing == EnumFacing.EAST || enumFacing == EnumFacing.WEST || enumFacing == EnumFacing.NORTH || enumFacing == EnumFacing.SOUTH) {
            if (!hasSide(0) && !hasSide(1)) {
                return false;
            }
        } else {
            if (!hasSide(2) && !hasSide(3) && !hasSide(4) && !hasSide(5)) {
                return false;
            }
        }
        if (sides[side] != null && sides[side].hasType(CableType.POWER)) {
            if (tileEntity.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite())) {
                return true;
            }
        }
        if (tileEntity instanceof IAcceptsCable) {
            if (sides[side].hasAnyType(((IAcceptsCable) tileEntity).getAcceptedCables())) {
                return true;
            }
        }
        return tileEntity.hasCapability(DMXReceiver.CAP, enumFacing.getOpposite()) || tileEntity.hasCapability(
            DMXProvider.CAP, enumFacing.getOpposite());
    }


    public boolean isConnected(EnumFacing enumFacing, int side) {
        TileEntity tileEntity = world.getTileEntity(pos.offset(enumFacing));
        if(tileEntity == null){
            return false;
        }
        if(tileEntity instanceof TileCable){
            TileCable tileCable = (TileCable)tileEntity;
            if (tileCable.sides[side] == null) {
                return false;
            }
            boolean hasType = false;
            for (CableType type : sides[side].getTypes()) {
                if (type != CableType.NONE) {
                    if (!hasType) {
                        if (tileCable.sides[side].hasType(type)) {
                            hasType = true;
                        }
                    }
                }
            }
            return hasType;
        }
        if(enumFacing == EnumFacing.EAST || enumFacing == EnumFacing.WEST || enumFacing == EnumFacing.NORTH || enumFacing == EnumFacing.SOUTH){
            if(!hasSide(0) && !hasSide(1)){
                return false;
            }
        } else {
            if(!hasSide(2) && !hasSide(3) && !hasSide(4) && !hasSide(5)){
                return false;
            }
        }
        if (sides[side] != null && sides[side].hasType(CableType.POWER)) {
            if (tileEntity.hasCapability(CapabilityEnergy.ENERGY, enumFacing.getOpposite())) {
                return true;
            }
        }
        if (tileEntity instanceof IAcceptsCable) {
            if (sides[side].hasAnyType(((IAcceptsCable) tileEntity).getAcceptedCables())) {
                return true;
            }
        }
        return tileEntity.hasCapability(DMXReceiver.CAP, enumFacing.getOpposite()) || tileEntity.hasCapability(
            DMXProvider.CAP, enumFacing.getOpposite());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            if (facing == null && hasType(CableType.DMX)) {
                return true;
            }
            for (int i = 0; i < 6; i++) {
                CableSide side = sides[i];
                if (side != null) {
                    if (side.hasType(CableType.DMX) && isConnected(facing, i, CableType.DMX)) {
                        return true;
                    }
                }
            }
        }
        if (capability == CapabilityEnergy.ENERGY) {
            if (facing == null && hasType(CableType.POWER)) {
                return true;
            }
            for (int i = 0; i < 6; i++) {
                CableSide side = sides[i];
                if (side != null) {
                    if (side.hasType(CableType.POWER) && isConnected(facing, i, CableType.POWER)) {
                        return true;
                    }
                }
            }
        }
        if (capability == BundledTheatricalPower.CAP) {
            if (facing == null && hasType(CableType.PATCH)) {
                return true;
            }
            for (int i = 0; i < 6; i++) {
                CableSide side = sides[i];
                if (side != null) {
                    if (side.hasType(CableType.PATCH) && isConnected(facing, i, CableType.PATCH)) {
                        return true;
                    }
                }
            }
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            if (facing == null && hasType(CableType.DMX)) {
                return null;
            }
            for (int i = 0; i < 6; i++) {
                CableSide side = sides[i];
                if (side != null) {
                    if (side.hasType(CableType.DMX) && isConnected(facing, i, CableType.DMX)) {
                        return null;
                    }
                }
            }
        }
        if (capability == CapabilityEnergy.ENERGY) {
            if (facing == null && hasType(CableType.POWER)) {
                return CapabilityEnergy.ENERGY.cast(this);
            }
            for (int i = 0; i < 6; i++) {
                CableSide side = sides[i];
                if (side != null) {
                    if (side.hasType(CableType.POWER) && isConnected(facing, i, CableType.POWER)) {
                        return CapabilityEnergy.ENERGY.cast(this);
                    }
                }
            }
        }
        if (capability == BundledTheatricalPower.CAP) {
            if (facing == null && hasType(CableType.PATCH)) {
                return BundledTheatricalPower.CAP.cast(this);
            }
            for (int i = 0; i < 6; i++) {
                CableSide side = sides[i];
                if (side != null) {
                    if (side.hasType(CableType.PATCH) && isConnected(facing, i, CableType.PATCH)) {
                        return BundledTheatricalPower.CAP.cast(this);
                    }
                }
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void invalidate()
    {
        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);

        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
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
    public int[] receiveEnergy(int[] channels, boolean simulate) {
        int[] energyReceived = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canReceive(i)) {
                energyReceived[i] = 0;
                continue;
            }
            energyReceived[i] = Math.min(capacityPerChannel - this.channels[i], Math.min(this.transferRate, channels[i]));
            if (!simulate) {
                this.channels[i] += energyReceived[i];
            }
        }
        return energyReceived;
    }

    @Override
    public int[] extractEnergy(int[] channels, boolean simulate) {
        int[] energyExtracted = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canExtract(i)) {
                energyExtracted[i] = 0;
                continue;
            }
            energyExtracted[i] = Math.min(this.channels[i], Math.min(this.transferRate, channels[i]));
            if (!simulate) {
                this.channels[i] -= energyExtracted[i];
            }
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(int channel) {
        return channels[channel];
    }

    @Override
    public int getMaxEnergyStored(int channel) {
        return capacityPerChannel;
    }

    @Override
    public boolean canExtract(int channel) {
        return channels[channel] > 0;
    }

    @Override
    public boolean canReceive(int channel) {
        return channels[channel] <= capacityPerChannel;
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
        for (CableSide side : sides) {
            if (side != null) {
                if (side.hasType(type)) {
                    return true;
                }
            }

        }
        return false;
    }

    public boolean canReceiveFromFace(EnumFacing facing) {
        if (sides[facing.getIndex()] != null && !sides[facing.getIndex()].hasType(CableType.POWER)) {
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
        for (EnumFacing face : EnumFacing.VALUES) {
            TileEntity tile = world.getTileEntity(pos.offset(face));
            if (tile == null) {
                continue;
            } else if (tile instanceof TileCable) {
                TileCable cable = (TileCable) tile;
                if (cable.hasType(CableType.POWER) && power > cable.power && cable.canReceiveFromFace(face.getOpposite())) {
                    acceptors.add((IEnergyStorage) tile);
                    if (!sendingFace.contains(face)) {
                        sendingFace.add(face);
                    }
                }
            } else if (tile.getCapability(CapabilityEnergy.ENERGY, face.getOpposite()) != null) {
                IEnergyStorage energyTile = tile.getCapability(CapabilityEnergy.ENERGY, face.getOpposite());
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

    public void doBundledTransfer() {
        if (!hasType(CableType.PATCH)) {
            return;
        }
        ArrayList<IBundledTheatricalPowerStorage> acceptors = new ArrayList<>();
        for (EnumFacing face : EnumFacing.VALUES) {
            TileEntity tile = world.getTileEntity(pos.offset(face));
            if (tile == null) {
                continue;
            } else if (tile instanceof TileCable) {
                TileCable cable = (TileCable) tile;
                if (cable.hasType(CableType.PATCH)) {
                    acceptors.add((IBundledTheatricalPowerStorage) tile);
                    if (!sendingFace.contains(face)) {
                        sendingFace.add(face);
                    }
                }
            } else if (tile.getCapability(BundledTheatricalPower.CAP, face.getOpposite()) != null) {
                IBundledTheatricalPowerStorage energyTile = tile.getCapability(BundledTheatricalPower.CAP, face.getOpposite());
                if (energyTile != null && energyTile.canReceive(0)) {
                    acceptors.add(energyTile);
                }
            }
        }

        if (acceptors.size() > 0) {
            for (IBundledTheatricalPowerStorage tile : acceptors) {
                int[] drain = new int[8];
                for (int i = 0; i < channels.length; i++) {
                    drain[i] = Math.min(channels[i], transferRate);
                }
                int[] move = tile.receiveEnergy(drain, false);
                extractEnergy(move, false);
            }
        }
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }
        ticksSinceLastSend++;
        if (ticksSinceLastSend >= 10) {
            sendingFace.clear();
            ticksSinceLastSend = 0;
        }

        doEnergyTransfer();
        doBundledTransfer();
    }
}
