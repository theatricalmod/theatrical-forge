package dev.theatricalmod.theatrical.api.capabilities.dmx.provider;

import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IDMXProvider {

    byte[] sendDMXValues(DMXUniverse dmxUniverse);

    DMXUniverse getUniverse(World world);

    void updateDevices(World world, BlockPos controllerPos);

    void refreshDevices();
}
