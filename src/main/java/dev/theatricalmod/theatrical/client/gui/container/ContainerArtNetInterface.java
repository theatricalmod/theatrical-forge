package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerArtNetInterface extends Container {

    public final TileEntityArtNetInterface blockEntity;
    protected final World world;

    public ContainerArtNetInterface(int id, World world, BlockPos pos) {
        super(TheatricalContainers.ARTNET_INTERFACE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityArtNetInterface) world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
