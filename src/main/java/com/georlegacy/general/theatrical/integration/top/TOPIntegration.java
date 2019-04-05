package com.georlegacy.general.theatrical.integration.top;

import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInterModComms;

public class TOPIntegration {


    public static void init() {
        if (Loader.isModLoaded("theoneprobe")) {
            FMLInterModComms.sendFunctionMessage("theoneprobe", "getTheOneProbe", "com.georlegacy.general.theatrical.integration.top.TOPGetter");
        }
    }

}
