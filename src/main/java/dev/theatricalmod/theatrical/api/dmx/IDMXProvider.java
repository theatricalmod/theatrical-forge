package dev.theatricalmod.theatrical.api.dmx;

import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public interface IDMXProvider {

    byte[] sendDMXValues(DMXUniverse dmxUniverse);

    DMXUniverse getUniverse(Level world);

    void updateDevices(Level world, BlockPos controllerPos);

    void refreshDevices();
}
