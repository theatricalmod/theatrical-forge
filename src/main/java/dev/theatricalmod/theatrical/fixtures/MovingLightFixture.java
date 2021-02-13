package dev.theatricalmod.theatrical.fixtures;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.ChannelType;
import dev.theatricalmod.theatrical.api.ChannelsDefinition;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.FixtureType;
import dev.theatricalmod.theatrical.api.fixtures.HangableType;
import net.minecraft.util.ResourceLocation;

public class MovingLightFixture extends Fixture {

    public static final ResourceLocation ID = new ResourceLocation(TheatricalMod.MOD_ID, "moving_head_fixture");

    public MovingLightFixture() {
        super(ID, FixtureType.INTELLIGENT, HangableType.BRACE_BAR,
            new ResourceLocation(TheatricalMod.MOD_ID, "block/moving_light/moving_head_static"), new ResourceLocation(TheatricalMod.MOD_ID, "block/moving_light/moving_head_bar"),
            new ResourceLocation(TheatricalMod.MOD_ID, "block/moving_light/moving_head_tilt"), new ResourceLocation(TheatricalMod.MOD_ID, "block/moving_light/moving_head_pan"),
            new float[]{0.5F, .6F, .5F}, new float[]{0.5F, .5F, .5F}, new float[]{0F, -0.8F, -0.35F}, 90, 0.15F, 0F, 50, 5,
            50, 0, 7, new ChannelsDefinition(), new ResourceLocation("theatrical:block/moving_head_whole"));
        getChannelsDefinition().setChannel(ChannelType.INTENSITY, 0);
        getChannelsDefinition().setChannel(ChannelType.RED, 1);
        getChannelsDefinition().setChannel(ChannelType.GREEN, 2);
        getChannelsDefinition().setChannel(ChannelType.BLUE, 3);
        getChannelsDefinition().setChannel(ChannelType.FOCUS, 4);
        getChannelsDefinition().setChannel(ChannelType.PAN, 5);
        getChannelsDefinition().setChannel(ChannelType.TILT, 6);
    }
}
