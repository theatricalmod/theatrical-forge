/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical;

import static com.georlegacy.general.theatrical.util.Reference.CLIENT_PROXY_CLASS;
import static com.georlegacy.general.theatrical.util.Reference.COMMON_PROXY_CLASS;
import static com.georlegacy.general.theatrical.util.Reference.MOD_ID;
import static com.georlegacy.general.theatrical.util.Reference.NAME;
import static com.georlegacy.general.theatrical.util.Reference.VERSION;

import com.georlegacy.general.theatrical.api.capabilities.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.provider.IDMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.receiver.IDMXReceiver;
import com.georlegacy.general.theatrical.guis.handlers.TheatricalGuiHandler;
import com.georlegacy.general.theatrical.handlers.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.integration.cc.ComputerCraftIntegration;
import com.georlegacy.general.theatrical.proxy.CommonProxy;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTBase;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;

/**
 * Main class for Theatrical
 *
 * @author James Conway
 */
@Mod(modid = MOD_ID, name = NAME, version = VERSION, dependencies = "after:computercraft;after:mcmultipart")
public class TheatricalMain {

    @Mod.Instance
    public static TheatricalMain instance;

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = COMMON_PROXY_CLASS)
    public static CommonProxy proxy;


    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        TheatricalPacketHandler.init();
        TheatricalPacketHandler.clientInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new TheatricalGuiHandler());
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

                if (nbt != null && instance instanceof INBTSerializable)
                {
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

                if (nbt != null && instance instanceof INBTSerializable)
                {
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
    }

    private void initComputer() {
        ComputerCraftIntegration.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (Loader.isModLoaded("computercraft")) {
            initComputer();
        }
    }

    @Mod.EventHandler
    public void PostInit(FMLPostInitializationEvent event) {

    }
}
