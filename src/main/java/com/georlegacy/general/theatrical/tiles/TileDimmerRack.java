package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.IAcceptsCable;
import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.power.bundled.BundledTheatricalPower;
import com.georlegacy.general.theatrical.api.capabilities.power.bundled.IBundledTheatricalPowerStorage;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import java.util.HashSet;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileDimmerRack extends TileBase implements IAcceptsCable, ITickable, IBundledTheatricalPowerStorage, IEnergyStorage {

    private int dmxStart = 0;


    private DMXReceiver dmxReceiver;

    private int[] channels;
    private int power;

    private int ticks = 0;


    public TileDimmerRack() {
        dmxReceiver = new DMXReceiver(6, dmxStart);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return true;
        }
        if (capability == BundledTheatricalPower.CAP) {
            return true;
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return DMXReceiver.CAP.cast(dmxReceiver);
        }
        if (capability == BundledTheatricalPower.CAP) {
            return BundledTheatricalPower.CAP.cast(this);
        }
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        nbtTagCompound = super.getNBT(nbtTagCompound);
        nbtTagCompound.setInteger("dmxStart", dmxStart);
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        dmxStart = nbtTagCompound.getInteger("dmxStart");
        super.readNBT(nbtTagCompound);
    }

    @Override
    public CableType[] getAcceptedCables() {
        return new CableType[]{CableType.POWER, CableType.PATCH, CableType.DMX};
    }

    @Override
    public void invalidate() {
        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }


    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }

        int totalPower = 0;
        int[] powerChannels = new int[6];
        for (int i = 0; i < 6; i++) {
            int val = dmxReceiver.getChannel(i) & 0xFF;
            totalPower += val;
            powerChannels[i] = val;
        }
        if (getEnergyStored() < 1) {
            return;
        }
        HashSet<IBundledTheatricalPowerStorage> acceptors = new HashSet<>();
        for (EnumFacing face : EnumFacing.VALUES) {
            TileEntity tile = world.getTileEntity(pos.offset(face));
            if (tile == null) {
                continue;
            } else if (tile.getCapability(BundledTheatricalPower.CAP, face.getOpposite()) != null) {
                IBundledTheatricalPowerStorage energyTile = tile.getCapability(BundledTheatricalPower.CAP, face.getOpposite());
                if (energyTile != null && energyTile.canReceive(0)) {
                    acceptors.add(energyTile);
                }
            }
        }
        if (acceptors.size() > 0) {
            for (IBundledTheatricalPowerStorage acceptor : acceptors) {
                int[] drain = new int[8];
                for (int i = 0; i < powerChannels.length; i++) {
                    drain[i] = Math.min(powerChannels[i], 255);
                }
                int[] res = acceptor.receiveEnergy(drain, true);
                boolean anyTransfer = false;
                for (int re : res) {
                    if (re > 0) {
                        anyTransfer = true;
                        break;
                    }
                }
                if (anyTransfer) {
                    acceptor.receiveEnergy(drain, false);
                    extractEnergy(totalPower, false);
                }
            }
        }
    }

    @Override
    public int[] receiveEnergy(int[] channels, boolean simulate) {
        return new int[8];
    }

    @Override
    public int[] extractEnergy(int[] channels, boolean simulate) {
        int[] energyExtracted = new int[8];
        for (int i = 0; i < channels.length; i++) {
            if (!canExtract(i)) {
                energyExtracted[i] = 0;
                continue;
            }
            energyExtracted[i] = Math.min(this.channels[i], Math.min(255, channels[i]));
            if (!simulate) {
                this.channels[i] -= energyExtracted[i];
            }
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored(int channel) {
        return 0;
    }

    @Override
    public int getMaxEnergyStored(int channel) {
        return 0;
    }

    @Override
    public boolean canExtract(int channel) {
        return getEnergyStored() > 0;
    }

    @Override
    public boolean canReceive(int channel) {
        return false;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(Integer.MAX_VALUE, maxReceive);
        if (!simulate) {
            power += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(power, Math.min(1000, maxExtract));
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
        return 1;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return getEnergyStored() < getMaxEnergyStored();
    }
}
