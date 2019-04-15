package dev.theatricalmod.theatrical.fixtures;

import dev.theatricalmod.theatrical.TheatricalConfig;
import dev.theatricalmod.theatrical.api.ChannelType;
import dev.theatricalmod.theatrical.api.ChannelsDefinition;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.FixtureType;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import dev.theatricalmod.theatrical.util.Reference;
import net.minecraft.util.ResourceLocation;

public class FixtureMovingHead extends Fixture {


    public FixtureMovingHead() {
        super(new ResourceLocation(Reference.MOD_ID, "moving_head"), FixtureType.INTELLIGENT, HangableType.BRACE_BAR,
            new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_static"), new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_bar"),
            new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_tilt"), new ResourceLocation(Reference.MOD_ID, "block/movinghead/moving_head_pan"),
            new float[]{0.5F, -.6F, -.5F}, new float[]{0.5F, -.5F, -.5F}, new float[]{0F, -0.8F, -0.35F}, 90, 0.15F, 180F, 50, 5,
            TheatricalConfig.general.movingHeadEnergyCost, TheatricalConfig.general.movingHeadEnergyUsage, 18, new ChannelsDefinition());
        getChannelsDefinition().setChannel(ChannelType.INTENSITY, 1);
        getChannelsDefinition().setChannel(ChannelType.RED, 2);
        getChannelsDefinition().setChannel(ChannelType.GREEN, 3);
        getChannelsDefinition().setChannel(ChannelType.BLUE, 4);
        getChannelsDefinition().setChannel(ChannelType.FOCUS, 12);
        getChannelsDefinition().setChannel(ChannelType.PAN, 6);
        getChannelsDefinition().setChannel(ChannelType.TILT, 8);
    }
}
