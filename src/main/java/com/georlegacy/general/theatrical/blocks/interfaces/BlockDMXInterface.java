package com.georlegacy.general.theatrical.blocks.interfaces;

import com.georlegacy.general.theatrical.blocks.base.BlockBase;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.TileDMXInterface;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDMXInterface extends BlockBase implements ITileEntityProvider, IHasTileEntity {

    public BlockDMXInterface() {
        super("dmx_interface");
        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileDMXInterface.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDMXInterface();
    }
}
