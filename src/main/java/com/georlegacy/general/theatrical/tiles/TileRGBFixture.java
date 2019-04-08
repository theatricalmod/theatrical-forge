package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.IRGB;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public abstract class TileRGBFixture extends TileDMXAcceptor implements IRGB, IEnergyStorage {

    private int red, green, blue = 0;

    public int power = 0;
    private int maxPower;
    private int ticksSinceUsage = 0;
    public int energyUsage, energyCost = 0;

    public TileRGBFixture(int channelCount, int channelStartPoint, int maxPower, int energyCost, int energyUsage) {
        super(channelCount, channelStartPoint);
        this.maxPower = maxPower;
        this.energyCost = energyCost;
        this.energyUsage = energyUsage;
    }


    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this);
        }
        return super.getCapability(capability, facing);
    }


    @Override
    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        nbtTagCompound = super.getNBT(nbtTagCompound);
        nbtTagCompound.setInteger("red", red);
        nbtTagCompound.setInteger("green", green);
        nbtTagCompound.setInteger("blue", blue);
        nbtTagCompound.setInteger("power", power);
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        red = nbtTagCompound.getInteger("red");
        green = nbtTagCompound.getInteger("green");
        blue = nbtTagCompound.getInteger("blue");
        power = nbtTagCompound.getInteger("power");
    }

    @Override
    public void update() {
        super.update();
        ticksSinceUsage++;
        if (ticksSinceUsage >= energyUsage) {
            ticksSinceUsage = 0;
            if ((power - energyCost) >= 0) {
                power -= energyCost;
            }
        }
    }

    @Override
    public int getColorHex() {
        return (red << 16) | (green << 8) | blue;
    }

    public int getRed() {
        return red;
    }

    public void setRed(int red) {
        if (power >= energyCost) {
            this.red = red;
        }
    }

    public int getGreen() {
        return green;
    }

    public void setGreen(int green) {
        if (power >= energyCost) {
            this.green = green;
        }
    }

    public int getBlue() {
        return blue;
    }

    public void setBlue(int blue) {
        if (power >= energyCost) {
            this.blue = blue;
        }
    }

    @Override
    public boolean shouldTrace() {
        return power > 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(Integer.MAX_VALUE, maxReceive));
        if (!simulate) {
            power += energyReceived;
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return power;
    }

    @Override
    public int getMaxEnergyStored() {
        return maxPower;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return getEnergyStored() < getMaxEnergyStored();
    }

    @Override
    public void setPan(int pan) {
        if (power >= energyCost) {
            super.setPan(pan);
        }
    }

    @Override
    public void setTilt(int tilt) {
        if (power >= energyCost) {
            super.setTilt(tilt);
        }
    }

    @Override
    public void setFocus(int focus) {
        if (power >= energyCost) {
            super.setFocus(focus);
        }
    }

    @Override
    public void setLightBlock(BlockPos lightBlock) {
        if (power >= energyCost) {
            super.setLightBlock(lightBlock);
        }
    }

}
