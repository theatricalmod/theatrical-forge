package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityDMXRedstoneInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public class ContainerDMXRedstoneInterface extends AbstractContainerMenu {

    public final TileEntityDMXRedstoneInterface blockEntity;
    protected final Level world;

    public ContainerDMXRedstoneInterface(int id, Level world, BlockPos pos) {
        super(TheatricalContainers.DMX_REDSTONE_INTERFACE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityDMXRedstoneInterface) world.getBlockEntity(pos);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }
}
