package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerGenericFixture extends Container {

    public final TileEntityGenericFixture blockEntity;
    protected final World world;

    public ContainerGenericFixture(int id, World world, BlockPos pos) {
        super(TheatricalContainers.GENERIC_FIXTURE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityGenericFixture) world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
