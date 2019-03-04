package com.georlegacy.general.theatrical.api.capabilities.receiver;

import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDMXReceiver {

    int getChannelCount();

    int getStartPoint();

    void receiveDMXValues(World world, BlockPos pos, DMXUniverse dmxUniverse);

    int getChannel(int index);

    void updateChannel(int index, int value);
}
