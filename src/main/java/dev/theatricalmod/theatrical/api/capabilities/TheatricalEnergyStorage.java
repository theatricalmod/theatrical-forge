package dev.theatricalmod.theatrical.api.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class TheatricalEnergyStorage extends EnergyStorage implements INBTSerializable<Tag> {

    public TheatricalEnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public void setEnergy(int energy){
        this.energy = energy;
    }

    @Override
    public Tag serializeNBT() {
        return IntTag.valueOf(getEnergyStored());
    }

    @Override
    public void deserializeNBT(Tag nbt) {
        if(!(nbt instanceof IntTag intTag))
            throw new IllegalArgumentException("Invalid NBT!");
        setEnergy(intTag.getAsInt());
    }
}
