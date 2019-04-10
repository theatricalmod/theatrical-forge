package com.georlegacy.general.theatrical.api;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface ISupport {

    EnumFacing getBlockPlacementDirection(World world, BlockPos pos, EnumFacing facing);

    float[] getLightTransforms(World world, BlockPos pos, EnumFacing facing);

}
