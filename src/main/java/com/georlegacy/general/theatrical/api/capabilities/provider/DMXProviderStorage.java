package com.georlegacy.general.theatrical.api.capabilities.provider;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class DMXProviderStorage implements Capability.IStorage<IDMXProvider> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IDMXProvider> capability, IDMXProvider instance,
        EnumFacing side) {
        NBTTagCompound nbt = new NBTTagCompound();
        instance.serialize(nbt);
        return nbt;
    }

    @Override
    public void readNBT(Capability<IDMXProvider> capability, IDMXProvider instance,
        EnumFacing side, NBTBase nbt) {
        instance.deserialize((NBTTagCompound) nbt);
    }
}
