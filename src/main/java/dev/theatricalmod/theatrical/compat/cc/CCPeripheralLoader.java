package dev.theatricalmod.theatrical.compat.cc;

import dan200.computercraft.api.ComputerCraftAPI;
import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.api.peripheral.IPeripheralProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class CCPeripheralLoader {
    public static void init(){
        ComputerCraftAPI.registerPeripheralProvider(new IPeripheralProvider() {
            @Nonnull
            @Override
            public LazyOptional<IPeripheral> getPeripheral(@Nonnull World world, @Nonnull BlockPos blockPos, @Nonnull Direction direction) {
                TileEntity te = world.getTileEntity(blockPos);
                if(te != null && te.getClass().getName().startsWith("dev.theatricalmod.theatrical") && te instanceof IPeripheral){
                    return LazyOptional.of(() -> (IPeripheral) te);
                }
                return LazyOptional.empty();
            }
        });
    }
}
