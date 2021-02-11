package dev.theatricalmod.theatrical.compat.cc;

import net.minecraftforge.fml.ModList;

public class CCCompat {

    public static void register(){
        if(isLoaded()){
            CCPeripheralLoader.init();
        }
    }

    public static boolean isLoaded(){
        return ModList.get().isLoaded("computercraft");
    }

}
