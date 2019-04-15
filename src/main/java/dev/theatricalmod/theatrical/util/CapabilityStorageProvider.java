package dev.theatricalmod.theatrical.util;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.util.INBTSerializable;

public class CapabilityStorageProvider<T> implements IStorage<T> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<T> capability, T instance, EnumFacing side) {
        return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
    }

    @Override
    public void readNBT(Capability<T> capability, T instance, EnumFacing side, NBTBase nbt) {
        if (nbt != null && instance instanceof INBTSerializable) {
            ((INBTSerializable) instance).deserializeNBT(nbt);
        }
    }
}
