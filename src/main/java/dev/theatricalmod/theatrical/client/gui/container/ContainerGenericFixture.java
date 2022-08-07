package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public class ContainerGenericFixture extends AbstractContainerMenu {

    public final TileEntityGenericFixture blockEntity;
    protected final Level world;

    public ContainerGenericFixture(int id, Level world, BlockPos pos) {
        super(TheatricalContainers.GENERIC_FIXTURE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityGenericFixture) world.getBlockEntity(pos);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }
}
