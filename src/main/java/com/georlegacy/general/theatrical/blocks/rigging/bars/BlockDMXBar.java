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

package com.georlegacy.general.theatrical.blocks.rigging.bars;

import com.georlegacy.general.theatrical.api.ISupport;
import com.georlegacy.general.theatrical.blocks.base.BlockBase;
import com.georlegacy.general.theatrical.blocks.fixtures.base.BlockHangable;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.rigging.TileDMXPipe;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.Plane;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDMXBar extends BlockBase implements ISupport, ITileEntityProvider, IHasTileEntity {

    public static final PropertyDirection AXIS = PropertyDirection.create("axis",
        Plane.HORIZONTAL);

    private final AxisAlignedBB X_BOX = new AxisAlignedBB(0.35, 0, 0, 0.65, 0.2, 1);
    private final AxisAlignedBB Z_BOX = new AxisAlignedBB(0, 0, 0.4, 1, 0.2, 0.6);

    public BlockDMXBar() {
        super("dmx_bar");
        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if (!playerIn.getHeldItem(hand).isEmpty()) {
            Item item = playerIn.getHeldItem(hand).getItem();
            if (getBlockFromItem(item) instanceof BlockHangable) {
                BlockHangable hangable = (BlockHangable) getBlockFromItem(item);
                BlockPos down = pos.offset(EnumFacing.DOWN);
                if (!worldIn.isAirBlock(down)) {
                    return false;
                }
                worldIn.setBlockState(down, hangable.getDefaultState().withProperty(BlockHangable.FACING, playerIn.getHorizontalFacing()));
                return true;
            }
        }
        return super
            .onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }

    @Override
    public IBlockState getStateForPlacement(World worldIn, BlockPos pos, EnumFacing facing,
        float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (facing.getAxis().isHorizontal()) {
            return this.getDefaultState().withProperty(AXIS, facing.getOpposite());
        } else {
            return this.getDefaultState()
                .withProperty(AXIS, placer.getHorizontalFacing().getOpposite());
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
        return state.getValue(AXIS).getAxis() == Axis.X ? X_BOX : Z_BOX;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.getValue(AXIS).getAxis() == Axis.X ? X_BOX : Z_BOX;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, AXIS);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.byIndex(meta);
        if (facing.getAxis() == Axis.X || facing.getAxis() == Axis.Z) {
            return getDefaultState().withProperty(AXIS, facing);
        } else {
            return getDefaultState();
        }
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS).getIndex();
    }

    @Override
    public EnumFacing getBlockPlacementDirection() {
        return EnumFacing.DOWN;
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileDMXPipe.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDMXPipe();
    }

}
