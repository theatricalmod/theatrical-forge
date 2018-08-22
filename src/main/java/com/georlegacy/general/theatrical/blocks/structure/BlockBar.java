package com.georlegacy.general.theatrical.blocks.structure;

import com.georlegacy.general.theatrical.blocks.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.blocks.fixtures.IFixture;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBar extends BlockDirectional {

    public BlockBar() {
        super("bar");
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if(!worldIn.isRemote){
            if(Block.getBlockFromItem(playerIn.getHeldItem(hand).getItem()) instanceof IFixture){
                worldIn.setBlockToAir(pos);
                worldIn.setBlockState(pos, TheatricalBlocks.blockFresnel.getDefaultState().withProperty(
                    BlockFresnel.ON_BAR, true).withProperty(FACING, playerIn.getHorizontalFacing().getOpposite()), 2);
                return true;
            }
        }
        return super
            .onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn,
        BlockPos pos) {
        return super.getCollisionBoundingBox(blockState, worldIn, pos);
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {

        return super.getBoundingBox(state, source, pos);
    }
}
