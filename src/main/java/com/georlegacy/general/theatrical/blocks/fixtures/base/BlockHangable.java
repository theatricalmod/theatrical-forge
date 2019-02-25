package com.georlegacy.general.theatrical.blocks.fixtures.base;

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockHangable extends BlockDirectional implements IBarAttachable {

    public static final PropertyBool ON_BAR = PropertyBool.create("on_bar");

    public BlockHangable(String name) {
        super(name);
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

    @Override
    public boolean isOnBar(World world, BlockPos pos) {
        return world.getBlockState(pos).getValue(ON_BAR);
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
        EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state.withProperty(ON_BAR, false), placer, stack);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int bool = meta >> 2;
        int facing = meta & 3;
        EnumFacing facing1 = EnumFacing.byIndex((facing) + 2);

        return this.getDefaultState().withProperty(FACING, facing1).withProperty(ON_BAR, bool == 1);
    }


    @Override
    public int getMetaFromState(IBlockState state) {
        int facingBits = state.getValue(FACING).getIndex() - 2;
        int boolBits = state.getValue(ON_BAR) ? 1 : 0;
        boolBits <<= 2;
        boolBits |= facingBits;
        return boolBits;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ON_BAR, FACING);
    }



}
