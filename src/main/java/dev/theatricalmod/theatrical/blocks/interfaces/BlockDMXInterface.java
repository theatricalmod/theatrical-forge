package dev.theatricalmod.theatrical.blocks.interfaces;

import dev.theatricalmod.theatrical.blocks.base.BlockBase;
import dev.theatricalmod.theatrical.blocks.fixtures.base.IHasTileEntity;
import dev.theatricalmod.theatrical.tabs.base.CreativeTabs;
import dev.theatricalmod.theatrical.tiles.interfaces.TileDMXInterface;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockDMXInterface extends BlockBase implements ITileEntityProvider, IHasTileEntity {

    public BlockDMXInterface() {
        super("dmx_interface");
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
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
