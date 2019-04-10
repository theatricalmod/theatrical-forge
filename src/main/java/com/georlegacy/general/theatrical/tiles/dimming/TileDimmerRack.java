package com.georlegacy.general.theatrical.tiles.dimming;

import com.georlegacy.general.theatrical.api.IAcceptsCable;
import com.georlegacy.general.theatrical.api.capabilities.WorldSocapexNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexProvider;
import com.georlegacy.general.theatrical.tiles.TileBase;
import com.georlegacy.general.theatrical.tiles.cables.CableType;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileDimmerRack extends TileBase implements IAcceptsCable, ITickable, IEnergyStorage {

    private int dmxStart = 0;

    private DMXReceiver dmxReceiver;
    private SocapexProvider socapexProvider;

    private int[] channels;
    private int power;

    private int ticks = 0;


    public TileDimmerRack() {
        dmxReceiver = new DMXReceiver(6, dmxStart);
        socapexProvider = new SocapexProvider();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return true;
        }
        if (capability == SocapexProvider.CAP) {
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
        if (capability == SocapexProvider.CAP) {
            return SocapexProvider.CAP.cast(socapexProvider);
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
        nbtTagCompound.setTag("provider", socapexProvider.serializeNBT());
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        dmxStart = nbtTagCompound.getInteger("dmxStart");
        if (nbtTagCompound.hasKey("provider")) {
            socapexProvider.deserializeNBT(nbtTagCompound.getCompoundTag("provider"));
        }
        super.readNBT(nbtTagCompound);
    }

    @Override
    public CableType[] getAcceptedCables() {
        return new CableType[]{CableType.POWER, CableType.SOCAPEX, CableType.DMX};
    }

    @Override
    public void invalidate() {
        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
            WorldSocapexNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
            WorldSocapexNetwork.getCapability(getWorld()).setRefresh(true);
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
        socapexProvider.receiveSocapex(powerChannels, false);
        socapexProvider.updateDevices(world, pos);
        extractEnergy(totalPower, false);
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

    public int getDmxStart() {
        return dmxStart;
    }
}
