package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public class ContainerArtNetInterface extends AbstractContainerMenu {

    public final TileEntityArtNetInterface blockEntity;
    protected final Level world;

    public ContainerArtNetInterface(int id, Level world, BlockPos pos) {
        super(TheatricalContainers.ARTNET_INTERFACE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityArtNetInterface) world.getBlockEntity(pos);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }
}
