package com.georlegacy.general.theatrical.guis.handlers;

import com.georlegacy.general.theatrical.guis.fixtures.containers.ContainerFresnel;
import com.georlegacy.general.theatrical.guis.fixtures.containers.ContainerIntelligentFixture;
import com.georlegacy.general.theatrical.guis.fixtures.gui.GuiFresnel;
import com.georlegacy.general.theatrical.guis.fixtures.gui.GuiIntelligentFixture;
import com.georlegacy.general.theatrical.guis.handlers.enumeration.GUIID;
import com.georlegacy.general.theatrical.guis.interfaces.ContainerArtNetInterface;
import com.georlegacy.general.theatrical.guis.interfaces.GuiArtNetInterface;
import com.georlegacy.general.theatrical.tiles.TileDMXAcceptor;
import com.georlegacy.general.theatrical.tiles.fixtures.TileFresnel;
import com.georlegacy.general.theatrical.tiles.interfaces.TileArtNetInterface;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class TheatricalGuiHandler implements IGuiHandler {

    @Nullable
    @Override
    public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y,
        int z) {
        switch (GUIID.getByNid(id)) {
            case FIXTURE_FRESNEL:
                return new ContainerFresnel(player.inventory,
                    (TileFresnel) world.getTileEntity(new BlockPos(x, y, z)));
            case FIXTURE_MOVING_HEAD:
                return new ContainerIntelligentFixture(player.inventory, (TileDMXAcceptor) world.getTileEntity(new BlockPos(x, y, z)));
            case ARTNET_INTERFACE:
                return new ContainerArtNetInterface(player.inventory, (TileArtNetInterface) world.getTileEntity(new BlockPos(x, y, z)));
            default:
                return null;
        }
    }

    @Nullable
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y,
        int z) {
        switch (GUIID.getByNid(id)) {
            case FIXTURE_FRESNEL:
                return new GuiFresnel(
                    (TileFresnel) world.getTileEntity(new BlockPos(x, y, z)),
                    (ContainerFresnel) getServerGuiElement(id, player, world, x, y, z));
            case FIXTURE_MOVING_HEAD:
                return new GuiIntelligentFixture((TileDMXAcceptor) world.getTileEntity(new BlockPos(x, y, z)), (ContainerIntelligentFixture) getServerGuiElement(id, player, world, x, y, z));
            case ARTNET_INTERFACE:
                return new GuiArtNetInterface((TileArtNetInterface) world.getTileEntity(new BlockPos(x, y, z)), (ContainerArtNetInterface) getServerGuiElement(id, player, world, x,y,z));
            default:
                return null;
        }
    }
}
