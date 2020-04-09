package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ContainerIntelligentFixture extends Container {

    public final TileEntityIntelligentFixture blockEntity;
    protected final World world;

    public ContainerIntelligentFixture(int id, World world, BlockPos pos) {
        super(TheatricalContainers.INTELLIGENT_FIXTURE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityIntelligentFixture) world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
