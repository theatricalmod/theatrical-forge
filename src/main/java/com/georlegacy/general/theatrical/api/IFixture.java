package com.georlegacy.general.theatrical.api;

import net.minecraft.block.Block;

public interface IFixture {

    float getPower();

    float getMaxLightDistance();

    Class<? extends Block> getBlock();

}
