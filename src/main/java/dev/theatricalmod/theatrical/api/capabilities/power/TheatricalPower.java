package dev.theatricalmod.theatrical.api.capabilities.power;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.INBTSerializable;

public class TheatricalPower implements ITheatricalPowerStorage, INBTSerializable<CompoundTag> {

    public static final Capability<ITheatricalPowerStorage> CAP = CapabilityManager.get(new CapabilityToken<ITheatricalPowerStorage>() {});

    private int power;
    private final int capacity;
    private final int maxReceive;
    private final int maxExtract;

    public TheatricalPower(){
        this(100, 100, 100, 0);
    }

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
    public CompoundTag serializeNBT() {
        CompoundTag tagCompound = new CompoundTag();
        tagCompound.putInt("power", power);
        return tagCompound;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
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
