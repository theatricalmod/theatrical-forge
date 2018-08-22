package com.georlegacy.general.theatrical;

import com.georlegacy.general.theatrical.packets.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.packets.UpdateLightPacket;
import com.georlegacy.general.theatrical.packets.UpdateLightPacketHandler;
import com.georlegacy.general.theatrical.proxy.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

import static com.georlegacy.general.theatrical.util.Reference.*;

/**
 * Main class for Theatrical
 *
 * @author James Conway
 */
@Mod(modid = MOD_ID, name = NAME, version = VERSION)
public class TheatricalMain {

    @Mod.Instance
    public static TheatricalMain instance;

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event) {
        proxy.preInit();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {
        proxy.registerModelBakeryVariants();
        proxy.registerColorBlocks();
        TheatricalPacketHandler.INSTANCE.registerMessage(UpdateLightPacketHandler.class, UpdateLightPacket.class, 0,
            Side.CLIENT);
    }

    @Mod.EventHandler
    public static void PostInit(FMLPostInitializationEvent event) {

    }

}
