package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerBasicLightingConsole extends Container {

    public final TileEntityBasicLightingControl blockEntity;
    protected final World world;

    public ContainerBasicLightingConsole(int id, World world, BlockPos pos) {
        super(TheatricalContainers.BASIC_LIGHTING_CONSOLE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityBasicLightingControl) world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
