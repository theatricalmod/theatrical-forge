package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public class ContainerBasicLightingConsole extends AbstractContainerMenu {

    public final TileEntityBasicLightingControl blockEntity;
    protected final Level world;

    public ContainerBasicLightingConsole(int id, Level world, BlockPos pos) {
        super(TheatricalContainers.BASIC_LIGHTING_CONSOLE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityBasicLightingControl) world.getBlockEntity(pos);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }
}
