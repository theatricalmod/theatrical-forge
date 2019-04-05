package com.georlegacy.general.theatrical.tiles.interfaces;

import com.georlegacy.general.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.dmx.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.Optional.Interface;

@Interface(iface = "dan200.computercraft.api.peripheral.IPeripheral", modid = "computercraft")
public class TileDMXInterface extends TileEntity implements IPeripheral {

    private final IDMXProvider idmxProvider;

    public TileDMXInterface() {
        this.idmxProvider = new DMXProvider(new DMXUniverse());
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == DMXProvider.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == DMXProvider.CAP) {
            return DMXProvider.CAP.cast(idmxProvider);
        }
        return super.getCapability(capability, facing);
    }

    @Nonnull
    @Override
    public String getType() {
        return "dmx_interface";
    }

    @Nonnull
    @Override
    public String[] getMethodNames() {
        return new String[]{"setChannel", "getChannel"};
    }

    @Nullable
    @Override
    public Object[] callMethod(@Nonnull IComputerAccess iComputerAccess,
        @Nonnull ILuaContext iLuaContext, int method, @Nonnull Object[] args)
        throws LuaException, InterruptedException {
        switch (method) {
            case 0:
                this.idmxProvider.getUniverse(world).setChannel(((Number) args[0]).intValue(), ((Number) args[1]).byteValue());
                this.sendDMXSignal(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
                break;
            case 1:
                return new Object[]{this.idmxProvider.getUniverse(world).getChannel(((Number) args[0]).intValue())};
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable IPeripheral iPeripheral) {
        return iPeripheral != null && iPeripheral.getType().equals(this.getType());
    }

    public void sendDMXSignal(int channel, int value){
        WorldDMXNetwork.getCapability(world).updateDevices();
    }

    @Override
    public void invalidate()
    {
        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);

        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }
}
