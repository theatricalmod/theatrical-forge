package com.georlegacy.general.theatrical.api.capabilities.receiver;

import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;

public class DMXReceiverStorage implements Capability.IStorage<IDMXReceiver> {

    @Nullable
    @Override
    public NBTBase writeNBT(Capability<IDMXReceiver> capability, IDMXReceiver instance,
        EnumFacing side) {
        NBTTagCompound nbt = new NBTTagCompound();
        instance.serialize(nbt);
        return nbt;
    }

    @Override
    public void readNBT(Capability<IDMXReceiver> capability, IDMXReceiver instance,
        EnumFacing side, NBTBase nbt) {
        instance.deserialize((NBTTagCompound) nbt);
    }
}
