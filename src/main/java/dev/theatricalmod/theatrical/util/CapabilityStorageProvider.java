package dev.theatricalmod.theatrical.util;

import net.minecraft.core.Direction;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class CapabilityStorageProvider<T>{

    @Nullable
    public Tag writeNBT(Capability<T> capability, T instance, Direction side) {
        return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
    }

    public void readNBT(Capability<T> capability, T instance, Direction side, Tag nbt) {
        if (nbt != null && instance instanceof INBTSerializable) {
            ((INBTSerializable) instance).deserializeNBT(nbt);
        }
    }
}
