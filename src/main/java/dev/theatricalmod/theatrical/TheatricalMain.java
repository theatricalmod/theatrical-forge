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

package dev.theatricalmod.theatrical;

import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.guis.handlers.TheatricalGuiHandler;
import dev.theatricalmod.theatrical.handlers.TheatricalPacketHandler;
import dev.theatricalmod.theatrical.init.TheatricalCapabilities;
import dev.theatricalmod.theatrical.init.TheatricalFixtures;
import dev.theatricalmod.theatrical.integration.cc.ComputerCraftIntegration;
import dev.theatricalmod.theatrical.integration.top.TOPIntegration;
import dev.theatricalmod.theatrical.proxy.CommonProxy;
import dev.theatricalmod.theatrical.util.Reference;
import java.io.File;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.NAME, version = Reference.VERSION, dependencies = "after:computercraft;after:mcmultipart")
public class TheatricalMain {

    @Mod.Instance
    public static TheatricalMain instance;

    @SidedProxy(clientSide = Reference.CLIENT_PROXY_CLASS, serverSide = Reference.COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    public static File lightsDirectory;
    public static Logger logger;

    @Mod.EventHandler
    public void PreInit(FMLPreInitializationEvent event) {
        lightsDirectory = new File(event.getModConfigurationDirectory(), "theatrical/lights");
        if (!lightsDirectory.exists()) {
            lightsDirectory.mkdirs();
        }
        logger = event.getModLog();
        TheatricalFixtures.init();
        Fixture.createRegistry();
        TheatricalPacketHandler.init();
        TheatricalPacketHandler.clientInit();
        NetworkRegistry.INSTANCE.registerGuiHandler(instance, new TheatricalGuiHandler());
        TheatricalCapabilities.init();
    }

    private void initComputer() {
        ComputerCraftIntegration.init();
    }

    private void initTop() {
        TOPIntegration.init();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        if (Loader.isModLoaded("computercraft")) {
            initComputer();
        }
        if (Loader.isModLoaded("theoneprobe")) {
            initTop();
        }
    }

    @Mod.EventHandler
    public void PostInit(FMLPostInitializationEvent event) {

    }
}
