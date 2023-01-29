package dev.theatricalmod.theatrical.fixtures;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.ChannelsDefinition;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.FixtureType;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import net.minecraft.util.ResourceLocation;

public class FloodFixture extends Fixture {

    public FloodFixture() {
        super(new ResourceLocation(TheatricalMod.MOD_ID, "flood_fixture"), FixtureType.TUNGSTEN, HangableType.HOOK_BAR,
                new ResourceLocation(TheatricalMod.MOD_ID, "block/flood/flood_hook_bar"),
                new ResourceLocation(TheatricalMod.MOD_ID, "block/flood/flood_hook"),
                new ResourceLocation(TheatricalMod.MOD_ID, "block/flood/flood_body_only"),
                new ResourceLocation(TheatricalMod.MOD_ID, "block/flood/flood_handle_only"),
                new float[]{0.5F, 0.3F, 0.39F}, new float[]{0.5F, 0, 0.41F}, new float[]{0.5F, 0.24F, 0.1F}, 0, 1F, 0, 5, 0,
                255, 0, 0, new ChannelsDefinition());
    }

}
