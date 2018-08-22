package com.georlegacy.general.theatrical.blocks.rigging;

import com.georlegacy.general.theatrical.blocks.base.BlockBase;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.blocks.fixtures.IFixture;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockLadderBar extends BlockBase {

    public static final PropertyEnum<EnumAxis> AXIS = PropertyEnum.create("axis", EnumAxis.class);

    public BlockLadderBar() {
        super("ladder_bar");
    }


    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
        float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if(facing.getAxis().isHorizontal()){
            return this.getDefaultState().withProperty(AXIS, EnumAxis.fromFacingAxis(facing.getOpposite().getAxis()));
        }else {
            return this.getDefaultState().withProperty(AXIS, EnumAxis.fromFacingAxis(placer.getHorizontalFacing().getOpposite().getAxis()));
        }
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
        switch(state.getValue(AXIS)){
            case Z:
                axisAlignedBB = new AxisAlignedBB(0, 0, 0.4, 1, 0.7, 0.6);
                break;
            case X:
                axisAlignedBB = new AxisAlignedBB(0.4, 0, 0, 0.6, 0.7, 1);
                break;
        }
        return axisAlignedBB;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, EnumAxis.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS).ordinal();
    }

}
