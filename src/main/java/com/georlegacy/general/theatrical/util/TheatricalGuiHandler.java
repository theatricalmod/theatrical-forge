package com.georlegacy.general.theatrical.util;

import com.georlegacy.general.theatrical.guis.fixtures.containers.ContainerFresnel;
import com.georlegacy.general.theatrical.guis.fixtures.gui.GuiFresnel;
import com.georlegacy.general.theatrical.tiles.fixtures.TileEntityFresnel;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class TheatricalGuiHandler implements IGuiHandler {

    public static final int FRENSEL = 0;

    @Nullable
    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y,
        int z) {
        switch(ID){
            case FRENSEL:
                return new ContainerFresnel(player.inventory, (TileEntityFresnel) world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y,
        int z) {
        switch(ID){
            case FRENSEL:
                return new GuiFresnel((TileEntityFresnel)world.getTileEntity(new BlockPos(x, y, z)), (ContainerFresnel) getServerGuiElement(ID, player, world, x, y, z));
            default:
                return null;
        }
    }
}
