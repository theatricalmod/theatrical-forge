package com.georlegacy.general.theatrical.blocks;

import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDirectional extends BlockBase {

    public static final PropertyDirection FACING = PropertyDirection.create("facing",
        Plane.HORIZONTAL);

    public BlockDirectional(String name) {
        super(name);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
        EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(FACING, EnumFacing.getFront((meta & 3) + 2));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(FACING).getIndex() - 2;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, FACING);
    }
}
