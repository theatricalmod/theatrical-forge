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

package com.georlegacy.general.theatrical.blocks.fixtures;

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.client.tesr.FixtureRenderer;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.fixtures.TileFresnel;
import com.georlegacy.general.theatrical.tiles.fixtures.TileMovingHead;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMovingHead extends BlockDirectional implements ITileEntityProvider, IHasTileEntity {


    public BlockMovingHead() {
        super("moving_head");
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(@Nonnull World worldIn, int meta) {
        return new TileMovingHead();
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileMovingHead.class;
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
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos,
        EnumFacing side) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, 1.1, 1);
        return axisAlignedBB;
    }

    @Override
    public void registerModels() {
        super.registerModels();
        ClientRegistry.bindTileEntitySpecialRenderer(TileMovingHead.class, new FixtureRenderer());
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileMovingHead tileFresnel = (TileMovingHead) worldIn.getTileEntity(pos);
        if (tileFresnel != null && tileFresnel.getLightBlock() != null) {
            worldIn.setBlockToAir(tileFresnel.getLightBlock());
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        if (world.getTileEntity(pos) instanceof TileFresnel) {
            TileMovingHead tileFresnel = (TileMovingHead) world.getTileEntity(pos);
            if (tileFresnel != null) {
                return (int) (tileFresnel.getIntensity() * 4 / 255);
            }
        }
        return super.getLightValue(state, world, pos);
    }
}
