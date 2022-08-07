package dev.theatricalmod.theatrical.block.light;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import dev.theatricalmod.theatrical.fixtures.TheatricalFixtures;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BlockMovingLight extends BlockIntelligentFixture {

    public BlockMovingLight() {
        super(TheatricalFixtures.MOVING_LIGHT, TheatricalBlocks.LIGHT_PROPERTIES);
    }

}
