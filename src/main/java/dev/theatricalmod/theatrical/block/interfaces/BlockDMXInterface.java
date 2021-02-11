package dev.theatricalmod.theatrical.block.interfaces;

import dev.theatricalmod.theatrical.block.TheatricalBlocks;
import net.minecraft.block.Block;

public class BlockDMXInterface extends Block {

    public BlockDMXInterface() {
        super(TheatricalBlocks.BASE_PROPERTIES.notSolid());
    }

}
