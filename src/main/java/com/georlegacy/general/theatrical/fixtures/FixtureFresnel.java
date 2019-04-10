package com.georlegacy.general.theatrical.fixtures;

import com.georlegacy.general.theatrical.TheatricalConfig;
import com.georlegacy.general.theatrical.api.ChannelsDefinition;
import com.georlegacy.general.theatrical.api.fixtures.Fixture;
import com.georlegacy.general.theatrical.api.fixtures.FixtureType;
import com.georlegacy.general.theatrical.api.fixtures.HangableType;
import com.georlegacy.general.theatrical.util.Reference;
import net.minecraft.util.ResourceLocation;

public class FixtureFresnel extends Fixture {

    public FixtureFresnel() {
        super(new ResourceLocation(Reference.MOD_ID, "fresnel"), FixtureType.TUNGSTEN, HangableType.HOOK_BAR,
            new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_hook_bar"), new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_hook"), new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_body_only"),
            new ResourceLocation(Reference.MOD_ID, "block/fresnel/fresnel_handle_only"),
            new float[]{0.7F, -.75F, -.64F}, new float[]{0.5F, 0, -.6F}, new float[]{0F, -1.5F, -1F}, 0, 0.25F, 0, 25, 0,
            TheatricalConfig.general.fresnelEnergyCost, TheatricalConfig.general.fresnelEnergyUsage, 0, new ChannelsDefinition());
    }
}
