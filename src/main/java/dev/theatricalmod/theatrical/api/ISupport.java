package dev.theatricalmod.theatrical.api;

import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

public interface ISupport {

    Direction getBlockPlacementDirection(IWorldReader world, BlockPos pos, Direction facing);

    float[] getLightTransforms(World world, BlockPos pos, Direction facing);

}
