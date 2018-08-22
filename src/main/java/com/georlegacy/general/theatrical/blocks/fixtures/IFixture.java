package com.georlegacy.general.theatrical.blocks.fixtures;

import net.minecraft.tileentity.TileEntity;

public interface IFixture {

    Class<? extends TileEntity> getTileEntity();

}
