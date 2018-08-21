package com.georlegacy.general.theatrical.init;

import com.georlegacy.general.theatrical.blocks.BlockTest;
import com.georlegacy.general.theatrical.blocks.fixtures.BlockFresnel;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

public class TheatricalBlocks {

    public static final List<Block> BLOCKS = new ArrayList<>();

    public static BlockFresnel blockFresnel = new BlockFresnel();

}
