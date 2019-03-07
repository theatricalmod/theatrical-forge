package com.georlegacy.general.theatrical.api.capabilities.provider;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.util.INBTSerializable;

public class DMXProvider implements IDMXProvider, INBTSerializable<NBTTagCompound> {

    @CapabilityInject(IDMXProvider.class)
    public static Capability<IDMXProvider> CAP;

    private DMXUniverse dmxUniverse;

    public DMXProvider() {}

    public DMXProvider(DMXUniverse dmxUniverse){
        this.dmxUniverse = dmxUniverse;
    }

    @Override
    public byte[] sendDMXValues(DMXUniverse dmxUniverse) {
        return dmxUniverse.getDMXChannels();
    }

    @Override
    public NBTTagCompound serializeNBT() {
        return null;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt) {

    }

    @Override
    public DMXUniverse getUniverse(World world) {
        return dmxUniverse;
    }
}
