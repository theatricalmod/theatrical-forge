package dev.theatricalmod.theatrical.blocks.fixtures.base;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IBarAttachable {

    boolean isOnBar(World world, BlockPos pos);

}
