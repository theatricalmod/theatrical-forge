package com.georlegacy.general.theatrical.blocks.dimmable;

import com.georlegacy.general.theatrical.blocks.base.BlockDirectional;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.TileDimmerRack;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDimmerRack extends BlockDirectional implements IHasTileEntity, ITileEntityProvider {

    public BlockDimmerRack() {
        super("dimmer_rack");
        this.setCreativeTab(CreativeTabs.RIGGING_TAB);
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileDimmerRack.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileDimmerRack();
    }
}
