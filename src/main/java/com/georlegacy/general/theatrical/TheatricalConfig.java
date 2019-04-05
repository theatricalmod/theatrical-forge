package com.georlegacy.general.theatrical;


import com.georlegacy.general.theatrical.util.Reference;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID)
@Config(modid = Reference.MOD_ID, category = "")
@Config.LangKey(Reference.MOD_ID)
public class TheatricalConfig {

    @Config.LangKey("stat.generalButton")
    public static final General general = new General();

    public static class General {

        @Config.Comment("The amount of energy a moving head uses")
        public int movingHeadEnergyCost = 1;

        @Config.Comment("How often the Moving head uses energy in ticks")
        public int movingHeadEnergyUsage = 10;

        @Config.Comment("The amount of energy a fresnel uses")
        public int fresnelEnergyCost = 1;

        @Config.Comment("How often the fresnel uses energy in ticks")
        public int fresnelEnergyUsage = 10;
    }

    public static void sync() {
        ConfigManager.sync(Reference.MOD_ID, Type.INSTANCE);
    }

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Reference.MOD_ID)) {
            sync();
        }
    }

}
