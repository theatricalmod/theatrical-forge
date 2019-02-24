package com.georlegacy.general.theatrical.integration.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ComputerCraftIntegration {


    public static void init() {
        ComputerCraftAPI.registerPeripheralProvider(new IPeripheralProvider() {
            @Nullable
            @Override
            public IPeripheral getPeripheral(@Nonnull World world, @Nonnull BlockPos blockPos,
                @Nonnull EnumFacing enumFacing) {
                TileEntity te = world.getTileEntity(blockPos);
                return te != null && te.getClass().getName()
                    .startsWith("com.georlegacy.general.theatrical") && te instanceof IPeripheral
                    ? (IPeripheral) te : null;
            }
        });
    }

}
