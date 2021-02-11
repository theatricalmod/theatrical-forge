package dev.theatricalmod.theatrical.util;

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class CapabilityStorageProvider<T> implements IStorage<T> {

    @Nullable
    @Override
    public INBT writeNBT(Capability<T> capability, T instance, Direction side) {
        return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, Direction side, INBT nbt) {
        if (nbt != null && instance instanceof INBTSerializable) {
            ((INBTSerializable) instance).deserializeNBT(nbt);
        }
    }
}
