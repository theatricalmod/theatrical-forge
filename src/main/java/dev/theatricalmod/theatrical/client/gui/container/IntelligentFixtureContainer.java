package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.block.light.IntelligentFixtureBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class IntelligentFixtureContainer extends Container {

    public final IntelligentFixtureBlockEntity blockEntity;
    protected final World world;

    public IntelligentFixtureContainer(int id, World world, BlockPos pos) {
        super(TheatricalContainers.INTELLIGENT_FIXTURE, id);

        this.world = world;
        this.blockEntity = (IntelligentFixtureBlockEntity) world.getTileEntity(pos);
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }
}
