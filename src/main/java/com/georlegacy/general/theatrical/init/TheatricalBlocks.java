package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;

import com.georlegacy.general.theatrical.blocks.rigging.BlockBar;
import com.georlegacy.general.theatrical.blocks.rigging.BlockLadderBar;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;

public class TheatricalBlocks {

    public static final List<Block> BLOCKS = new ArrayList<>();

    public static BlockFresnel blockFresnel = new BlockFresnel();
    public static BlockBar blockBar = new BlockBar();
    public static BlockLadderBar blockLadderBar = new BlockLadderBar();

}
