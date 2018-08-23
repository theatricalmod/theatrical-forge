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

import com.georlegacy.general.theatrical.TheatricalMain;
import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.client.models.fixtures.FresnelTESR;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.Gel;
import com.georlegacy.general.theatrical.items.attr.fixture.gel.GelType;
import com.georlegacy.general.theatrical.packets.TheatricalPacketHandler;
import com.georlegacy.general.theatrical.packets.UpdateLightPacket;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import com.georlegacy.general.theatrical.util.TheatricalGuiHandler;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class BlockFresnel extends BlockDirectional implements ITileEntityProvider, IFixture {

    public static final PropertyBool ON_BAR = PropertyBool.create("on_bar");

    public BlockFresnel() {
        super("fresnel");
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileEntityFresnel();
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileEntityFresnel.class;
    }

    private TileEntityFresnel getTE(World world, BlockPos pos) {
        return (TileEntityFresnel) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
                                    EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
                                    float hitZ) {
        if(!worldIn.isRemote){
            if(!playerIn.isSneaking()){
                playerIn.openGui(TheatricalMain.instance, TheatricalGuiHandler.FRENSEL, worldIn, pos.getX(), pos.getY(), pos.getZ());
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


    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state,
                                EntityLivingBase placer, ItemStack stack) {
        super.onBlockPlacedBy(worldIn, pos, state.withProperty(ON_BAR, false), placer, stack);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        int bool = meta >> 2;
        int facing = meta & 3;
        EnumFacing facing1 = EnumFacing.getFront((facing) + 2);

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

    @Override
    public void registerModels() {
        super.registerModels();
        ClientRegistry.bindTileEntitySpecialRenderer(TileEntityFresnel.class, new FresnelTESR());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldSideBeRendered(IBlockState blockState, IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }


    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        AxisAlignedBB axisAlignedBB = new AxisAlignedBB(0, 0, 0, 1, 1.1, 1);
        return axisAlignedBB;
    }
}
