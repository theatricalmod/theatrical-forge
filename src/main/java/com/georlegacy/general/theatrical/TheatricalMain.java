package com.georlegacy.general.theatrical;

import com.georlegacy.general.theatrical.inventory.tabs.base.TabManager;
import com.georlegacy.general.theatrical.proxy.CommonProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import static com.georlegacy.general.theatrical.util.Reference.*;

/**
 * Main class for Theatrical
 *
 * @author James Conway
 */
@Mod(modid = MOD_ID, name = NAME, version = VERSION)
public class TheatricalMain {

    private static TabManager tabManager;

    public static TabManager getTabManager() {
        return tabManager;
    }

    @Mod.Instance
    public static TheatricalMain instance;

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = COMMON_PROXY_CLASS)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public static void PreInit(FMLPreInitializationEvent event) {
        tabManager = new TabManager();
    }

    @Mod.EventHandler
    public static void init(FMLInitializationEvent event) {

    }

    @Mod.EventHandler
    public static void PostInit(FMLPostInitializationEvent event) {

    }

}
