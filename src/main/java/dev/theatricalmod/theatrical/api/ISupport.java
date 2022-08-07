package dev.theatricalmod.theatrical.api;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;

public interface ISupport {

    float[] getLightTransforms(Level world, BlockPos pos, Direction facing);

}
