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

import com.georlegacy.general.theatrical.api.ISupport;
import com.georlegacy.general.theatrical.blocks.base.BlockBase;
import com.georlegacy.general.theatrical.blocks.fixtures.base.BlockHangable;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.rigging.TileSquareTruss;
import javax.annotation.Nullable;
import net.minecraft.block.BlockLog.EnumAxis;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;

public class BlockSquareTruss extends BlockBase implements ISupport, ITileEntityProvider, IHasTileEntity {

    public static final PropertyEnum<EnumAxis> AXIS = PropertyEnum.create("axis", EnumAxis.class);

    private final AxisAlignedBB X_BOX = new AxisAlignedBB(0, 0,0, 1, 1, 1);
    private final AxisAlignedBB Z_BOX = new AxisAlignedBB(0, 0,0, 1, 1, 1);

    public static final IUnlistedProperty<TileSquareTruss> TRUSS = new IUnlistedProperty<TileSquareTruss>() {
        @Override
        public String getName() {
            return "truss";
        }

        @Override
        public boolean isValid(TileSquareTruss value) {
            return true;
        }

        @Override
        public Class<TileSquareTruss> getType() {
            return  TileSquareTruss.class;
        }

        @Override
        public String valueToString(TileSquareTruss value) {
            return TileEntity.getKey(value.getClass()).getPath();
        }
    };

    public BlockSquareTruss() {
        super("square");
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }


    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileSquareTruss.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileSquareTruss();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if(!playerIn.getHeldItem(hand).isEmpty()){
            Item item = playerIn.getHeldItem(hand).getItem();
            if(getBlockFromItem(item) instanceof BlockHangable){
                BlockHangable hangable = (BlockHangable) getBlockFromItem(item);
                BlockPos down = pos.offset(EnumFacing.DOWN);
                if(!worldIn.isAirBlock(down))
                    return false;
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
            BlockPos offFacing = pos.offset(placer.getHorizontalFacing());
            if(!worldIn.isAirBlock(offFacing))
            {
                if(worldIn.getBlockState(offFacing).getBlock() == this){
                    return this.getDefaultState().withProperty(AXIS, worldIn.getBlockState(offFacing).getValue(AXIS));
                }
            }
            return this.getDefaultState()
                .withProperty(AXIS, EnumAxis.fromFacingAxis(facing.getOpposite().getAxis()));
        } else {
            return this.getDefaultState().withProperty(AXIS,
                EnumAxis.fromFacingAxis(placer.getHorizontalFacing().getOpposite().getAxis()));
        }
    }


    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        return state.getValue(AXIS) == EnumAxis.X ? X_BOX : Z_BOX;
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new ExtendedBlockState(this, new IProperty[]{AXIS},  new IUnlistedProperty[] {TRUSS});
    }

    @Override
    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state instanceof IExtendedBlockState)
        {
            TileEntity tileEntity = world.getTileEntity(pos);

            if (tileEntity instanceof TileSquareTruss)
            {
                return ((IExtendedBlockState) state).withProperty(TRUSS, (TileSquareTruss) tileEntity);
            }
        }

        return state;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return getDefaultState().withProperty(AXIS, EnumAxis.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(AXIS).ordinal();
    }

    @Override
    public EnumFacing getBlockPlacementDirection(World world, BlockPos pos, EnumFacing facing) {
        return EnumFacing.DOWN;
    }

    @Override
    public float[] getLightTransforms(World world, BlockPos pos, EnumFacing facing) {
        return new float[]{0, 0.19F, 0};
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }
    @Override
    public boolean isBlockNormalCube(IBlockState blockState) {
        return false;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess blockAccess,
        BlockPos pos, EnumFacing side) {
        return false;
    }

}
