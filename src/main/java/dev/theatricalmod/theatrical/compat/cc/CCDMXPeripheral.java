package dev.theatricalmod.theatrical.compat.cc;

import dan200.computercraft.api.lua.IArguments;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.lua.MethodResult;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IDynamicPeripheral;
import dan200.computercraft.api.peripheral.IPeripheral;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityDMXInterface;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Supplier;

public class CCDMXPeripheral implements IDynamicPeripheral {

    private final Supplier<DMXProvider> idmxProviderSupplier;
    private final Supplier<TileEntity> tileSupplier;

    public CCDMXPeripheral(Supplier<DMXProvider> idmxProviderSupplier, Supplier<TileEntity> tileSupplier){
        this.idmxProviderSupplier = idmxProviderSupplier;
        this.tileSupplier = tileSupplier;
    }

    @Nonnull
    @Override
    public String getType() {
        return "dmx";
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[]{"setChannel", "getChannel"};
    }

    @Nonnull
    @Override
    public MethodResult callMethod(@Nonnull IComputerAccess access, @Nonnull ILuaContext context, int method, @Nonnull IArguments args) throws LuaException {
        IDMXProvider provider = idmxProviderSupplier.get();
        switch (method) {
            case 0:
                provider.getUniverse(null).setChannel(args.getInt(0), (byte) args.getInt(1));
                provider.sendDMXValues(provider.getUniverse(null));
                break;
            case 1:
                return MethodResult.of(provider.getUniverse(null).getChannel((args.getInt(0))));
        }
        throw new LuaException("invalid method");
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        if(iPeripheral instanceof CCDMXPeripheral) {
            if(idmxProviderSupplier.get() == null){
                return false;
            }
            return ((CCDMXPeripheral)iPeripheral).idmxProviderSupplier.get() == idmxProviderSupplier.get();
        }
        return false;
    }
}
