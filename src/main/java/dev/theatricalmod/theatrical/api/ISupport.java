package dev.theatricalmod.theatrical.api;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISupport {

    float[] getLightTransforms(World world, BlockPos pos, Direction facing);

}
