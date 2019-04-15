package dev.theatricalmod.theatrical.api.fixtures;

import net.minecraft.block.Block;

public interface IFixture {

    float getIntensity();

    float getMaxLightDistance();

    Class<? extends Block> getBlock();

    boolean shouldTrace();

    boolean emitsLight();

}
