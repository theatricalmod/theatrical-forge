package dev.theatricalmod.theatrical.fixtures;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.ChannelsDefinition;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.FixtureType;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import net.minecraft.resources.ResourceLocation;

public class FixtureFresnel extends Fixture {

    public FixtureFresnel() {
        super(new ResourceLocation(TheatricalMod.MOD_ID, "fresnel_fixture"), FixtureType.TUNGSTEN, HangableType.HOOK_BAR,
            new ResourceLocation(TheatricalMod.MOD_ID, "block/fresnel/fresnel_hook_bar"), new ResourceLocation(TheatricalMod.MOD_ID, "block/fresnel/fresnel_hook"), new ResourceLocation(TheatricalMod.MOD_ID, "block/fresnel/fresnel_body_only"),
            new ResourceLocation(TheatricalMod.MOD_ID, "block/fresnel/fresnel_handle_only"),
            new float[]{0.5F, 0.3F, 0.39F}, new float[]{0.5F, 0, 0.41F}, new float[]{0.5F, 0.24F, 0.1F}, 0, 0.25F, 0, 25, 0,
            255, 0, 0, new ChannelsDefinition());
    }
}
