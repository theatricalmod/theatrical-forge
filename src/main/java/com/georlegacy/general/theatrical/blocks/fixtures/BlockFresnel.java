package com.georlegacy.general.theatrical.blocks.fixtures;

import com.georlegacy.general.theatrical.blocks.BlockBase;
import com.georlegacy.general.theatrical.blocks.BlockDirectional;
import com.georlegacy.general.theatrical.items.fixtureattr.gel.Gel;
import com.georlegacy.general.theatrical.items.fixtureattr.gel.GelType;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;

public class BlockFresnel extends BlockDirectional implements ITileEntityProvider, IFixture {

    public static final PropertyBool ON_BAR = PropertyBool.create("on_bar");

    public BlockFresnel() {
        super("fresnel");
        this.setCreativeTab(CreativeTabs.fixturesTab);
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

    private TileEntityFresnel getTE(World world, BlockPos pos){
        return (TileEntityFresnel) world.getTileEntity(pos);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if(!worldIn.isRemote) {
            if (playerIn.getHeldItem(hand).getItem() instanceof Gel) {
                ItemStack itemStack = playerIn.getHeldItem(hand);
                GelType gelType = GelType.getGelType(itemStack.getMetadata());
                getTE(worldIn, pos).setGelType(gelType);
                playerIn.sendStatusMessage(new TextComponentString("Set light gel to " + gelType.getName()), false);
            }else {
                playerIn.sendStatusMessage(new TextComponentString("Light gel is " +
                    getTE(worldIn, pos).getGelType().getName()), false);
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
        return super.
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return this.getMetaFromState(state) + (state.getValue(ON_BAR) ? 1 : 0);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, ON_BAR, FACING);
    }
}
