package com.georlegacy.general.theatrical.blocks.rigging;

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.blocks.fixtures.IFixture;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
<<<<<<< HEAD:src/main/java/com/georlegacy/general/theatrical/blocks/rigging/BlockBar.java
=======
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
>>>>>>> 03cc01933e5b4119ff66f4fbb47707c48b562720:src/main/java/com/georlegacy/general/theatrical/blocks/structure/BlockBar.java
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
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
        EntityLivingBase placer, ItemStack stack) {
        worldIn.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
    }

    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState blockState) {
        return false;
    }


    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(0, 0.9, 0.4, 1, 1.1, 0.6);
        switch(state.getValue(FACING)){
            case WEST:
                axisAlignedBB = new AxisAlignedBB(0.4, 0.9, 0, 0.6, 1.1, 1);
                break;
            case NORTH:
                axisAlignedBB = new AxisAlignedBB(0, 0.9, 0.4, 1, 1.1, 0.6);
                break;
            case SOUTH:
                axisAlignedBB = new AxisAlignedBB(0, 0.9, 0.4, 1, 1.1, 0.6);
                break;
            case EAST:
                axisAlignedBB = new AxisAlignedBB(0.4, 0.9, 0, 0.6, 1.1, 1);
                break;
        }
        return axisAlignedBB;
    }
}
