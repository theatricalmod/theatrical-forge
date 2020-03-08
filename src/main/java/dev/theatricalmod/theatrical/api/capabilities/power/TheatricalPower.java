package dev.theatricalmod.theatrical.api.capabilities.power;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class TheatricalPower implements ITheatricalPowerStorage, INBTSerializable<CompoundNBT> {

    @CapabilityInject(ITheatricalPowerStorage.class)
    public static Capability<ITheatricalPowerStorage> CAP;

    private int power;
    private int capacity;
    private int maxReceive;
    private int maxExtract;

    public TheatricalPower(int capacity, int maxReceive, int maxExtract) {
        this(capacity, maxReceive, maxExtract, 0);
    }

    public TheatricalPower(int capacity, int maxReceive, int maxExtract, int power) {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
        this.power = Math.max(0, Math.min(capacity, power));
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tagCompound = new CompoundNBT();
        tagCompound.putInt("power", power);
        return tagCompound;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        power = nbt.getInt("power");
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(capacity - power, Math.min(this.maxReceive, maxReceive));
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

        int energyExtracted = Math.min(power, Math.min(this.maxExtract, maxExtract));
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
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }
}
