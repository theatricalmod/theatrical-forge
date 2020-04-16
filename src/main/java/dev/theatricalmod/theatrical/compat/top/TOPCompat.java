package dev.theatricalmod.theatrical.compat.top;

import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.ModList;

public class TOPCompat {

    public static void register(){
        if(isLoaded()){
            InterModComms.sendTo("theoneprobe", "getTheOneProbe", TOPInfoProvider::new);
        }
    }

    public static boolean isLoaded(){
        return ModList.get().isLoaded("theoneprobe");
    }

}
