package com.georlegacy.general.theatrical.blocks.interfaces;

import com.georlegacy.general.theatrical.TheatricalMain;
import com.georlegacy.general.theatrical.blocks.base.BlockBase;
import com.georlegacy.general.theatrical.blocks.fixtures.base.IHasTileEntity;
import com.georlegacy.general.theatrical.guis.handlers.enumeration.GUIID;
import com.georlegacy.general.theatrical.tabs.base.CreativeTabs;
import com.georlegacy.general.theatrical.tiles.interfaces.TileArtnetInterface;
import javax.annotation.Nullable;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockArtnetInterface extends BlockBase implements ITileEntityProvider, IHasTileEntity {

    public BlockArtnetInterface() {
        super("artnet_interface");
        this.setCreativeTab(CreativeTabs.FIXTURES_TAB);
    }

    @Override
    public Class<? extends TileEntity> getTileEntity() {
        return TileArtnetInterface.class;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return new TileArtnetInterface();
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if(worldIn.getTileEntity(pos) != null){
            TileEntity tileEntity = worldIn.getTileEntity(pos);
            if (tileEntity instanceof TileArtnetInterface) {
                ((TileArtnetInterface) tileEntity).disconnectClient();
            }
        }
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state,
        EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY,
        float hitZ) {
        if (!worldIn.isRemote) {
            if (!playerIn.isSneaking()) {
                playerIn.openGui(TheatricalMain.instance, GUIID.ARTNET_INTERFACE.getNid(), worldIn,
                    pos.getX(), pos.getY(), pos.getZ());
            }
        }
        return super
            .onBlockActivated(worldIn, pos, state, playerIn, hand, facing, hitX, hitY, hitZ);
    }
}
