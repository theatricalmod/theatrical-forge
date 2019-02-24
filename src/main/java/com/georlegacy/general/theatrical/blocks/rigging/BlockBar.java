/*
 * Copyright 2018 Theatrical Team (James Conway (615283) & Stuart (Rushmead)) and it's contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.georlegacy.general.theatrical.blocks.rigging;

import com.georlegacy.general.theatrical.blocks.base.BlockBase;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.init.TheatricalBlocks;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockBar extends BlockBase {

    public static final PropertyDirection AXIS =PropertyDirection.create("axis",
        Plane.HORIZONTAL);

    private final AxisAlignedBB X_BOX = new AxisAlignedBB(0.4, 0.9, 0, 0.6, 1.1, 1);
    private final AxisAlignedBB Z_BOX =  new AxisAlignedBB(0, 0.9, 0.4, 1, 1.1, 0.6);

    public BlockBar() {
        super("bar");
        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if(!worldIn.isRemote){
            if(Block.getBlockFromItem(playerIn.getHeldItem(hand).getItem()) instanceof IHasTileEntity){
                if(playerIn.getHorizontalFacing().getAxis() == state.getValue(AXIS).getAxis()){
                    worldIn.setBlockToAir(pos);
                    worldIn.setBlockState(pos, TheatricalBlocks.BLOCK_FRESNEL.getDefaultState().withProperty(
                        BlockFresnel.ON_BAR, true).withProperty(BlockFresnel.FACING,
                        playerIn.getHorizontalFacing()), 2);
                    return true;
                }
            }
        }
        return super
            .onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
        float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if(facing.getAxis().isHorizontal()){
            return this.getDefaultState().withProperty(AXIS, facing.getOpposite());
        }else {
            return this.getDefaultState().withProperty(AXIS,placer.getHorizontalFacing().getOpposite());
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
        return state.getValue(AXIS).getAxis() == EnumFacing.Axis.X ? X_BOX : Z_BOX;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, EnumFacing.byIndex(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS).getIndex();
    }

}
