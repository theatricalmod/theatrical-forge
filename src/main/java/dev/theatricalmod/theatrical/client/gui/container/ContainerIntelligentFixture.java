package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

public class ContainerIntelligentFixture extends AbstractContainerMenu {

    public final TileEntityIntelligentFixture blockEntity;
    protected final Level world;

    public ContainerIntelligentFixture(int id, Level world, BlockPos pos) {
        super(TheatricalContainers.INTELLIGENT_FIXTURE.get(), id);

        this.world = world;
        this.blockEntity = (TileEntityIntelligentFixture) world.getBlockEntity(pos);
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }
}
