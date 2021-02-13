package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityDMXRedstoneInterface;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerDMXRedstoneInterface extends Container {

    public final TileEntityDMXRedstoneInterface blockEntity;
    protected final World world;

    public ContainerDMXRedstoneInterface(int id, World world, BlockPos pos) {
        super(TheatricalContainers.DMX_REDSTONE_INTERFACE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityDMXRedstoneInterface) world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
