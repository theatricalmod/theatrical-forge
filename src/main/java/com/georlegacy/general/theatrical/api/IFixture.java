package com.georlegacy.general.theatrical.api;

import net.minecraft.block.Block;

public interface IFixture {

    float getIntensity();

    float getMaxLightDistance();

    Class<? extends Block> getBlock();

}
