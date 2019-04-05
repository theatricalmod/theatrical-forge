package com.georlegacy.general.theatrical.blocks.dimmable;

import com.georlegacy.general.theatrical.api.capabilities.power.bundled.BundledTheatricalPower;
import com.georlegacy.general.theatrical.api.capabilities.power.bundled.IBundledTheatricalPowerStorage;
import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.integration.top.ITOPProvider;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.TilePipePanel;
import javax.annotation.Nullable;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.ProbeMode;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPlugPanel extends BlockDirectional implements IHasTileEntity, ITileEntityProvider, ITOPProvider {

    public BlockPlugPanel() {
        super("plug_panel");
        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TilePipePanel.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TilePipePanel();
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!playerIn.isSneaking()) {
            TilePipePanel tilePipePanel = (TilePipePanel) worldIn.getTileEntity(pos);
            if (tilePipePanel != null) {
                ItemStack item = playerIn.getHeldItem(hand);
                Block block = Block.getBlockFromItem(item.getItem());
                IBlockState state1 = block.getStateFromMeta(item.getItem().getMetadata(item));
                if (block.getRenderType(state1).equals(EnumBlockRenderType.MODEL)) {
                    tilePipePanel.setState(state1);
                    if (worldIn.isRemote) {
                        worldIn.notifyBlockUpdate(pos, state, state, 11);
                    }
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }


    @Override
    @Deprecated
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);

        if (tileEntity instanceof TilePipePanel) {
            TilePipePanel core = ((TilePipePanel) tileEntity);

            if (core != null && core.getState() != Blocks.AIR.getDefaultState()) {
                return core.getState();
            }
        }

        return state;
    }

    @Override
    public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState blockState, IProbeHitData data) {
        TileEntity tileEntity = world.getTileEntity(data.getPos());

        if (tileEntity instanceof TilePipePanel) {
            TilePipePanel tile = (TilePipePanel) tileEntity;
            IBundledTheatricalPowerStorage bundledTheatricalPowerStorage = tile.getCapability(BundledTheatricalPower.CAP, null);
            for (int x = 0; x < 8; x++) {
                probeInfo.text("Channel #" + x + ": " + bundledTheatricalPowerStorage.getEnergyStored(x));
            }
        }
    }
}
