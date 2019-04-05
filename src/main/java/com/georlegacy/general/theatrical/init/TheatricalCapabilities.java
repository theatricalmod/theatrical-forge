package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import com.georlegacy.general.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import com.georlegacy.general.theatrical.api.capabilities.power.bundled.IBundledTheatricalPowerStorage;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;

public class TheatricalCapabilities {

    public static void init() {

        CapabilityManager.INSTANCE.register(IDMXProvider.class, new IStorage<IDMXProvider>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IDMXProvider> capability, IDMXProvider instance,
                EnumFacing side) {
                return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
            }

            @Override
            public void readNBT(Capability<IDMXProvider> capability, IDMXProvider instance,
                EnumFacing side, NBTBase nbt) {

                if (nbt != null && instance instanceof INBTSerializable) {
                    ((INBTSerializable) instance).deserializeNBT(nbt);
                }
            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(IDMXReceiver.class, new IStorage<IDMXReceiver>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IDMXReceiver> capability, IDMXReceiver instance,
                EnumFacing side) {
                return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
            }

            @Override
            public void readNBT(Capability<IDMXReceiver> capability, IDMXReceiver instance,
                EnumFacing side, NBTBase nbt) {

                if (nbt != null && instance instanceof INBTSerializable) {
                    ((INBTSerializable) instance).deserializeNBT(nbt);
                }
            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(IBundledTheatricalPowerStorage.class, new IStorage<IBundledTheatricalPowerStorage>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<IBundledTheatricalPowerStorage> capability, IBundledTheatricalPowerStorage instance,
                EnumFacing side) {
                return instance instanceof INBTSerializable ? ((INBTSerializable) instance).serializeNBT() : null;
            }

            @Override
            public void readNBT(Capability<IBundledTheatricalPowerStorage> capability, IBundledTheatricalPowerStorage instance,
                EnumFacing side, NBTBase nbt) {

                if (nbt != null && instance instanceof INBTSerializable) {
                    ((INBTSerializable) instance).deserializeNBT(nbt);
                }
            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(WorldDMXNetwork.class, new IStorage<WorldDMXNetwork>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<WorldDMXNetwork> capability, WorldDMXNetwork instance,
                EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<WorldDMXNetwork> capability, WorldDMXNetwork instance,
                EnumFacing side, NBTBase nbt) {
            }
        }, () -> null);

        CapabilityManager.INSTANCE.register(ITheatricalPowerStorage.class, new IStorage<ITheatricalPowerStorage>() {
            @Nullable
            @Override
            public NBTBase writeNBT(Capability<ITheatricalPowerStorage> capability, ITheatricalPowerStorage instance,
                EnumFacing side) {
                return null;
            }

            @Override
            public void readNBT(Capability<ITheatricalPowerStorage> capability, ITheatricalPowerStorage instance,
                EnumFacing side, NBTBase nbt) {
            }
        }, () -> null);
    }

}
