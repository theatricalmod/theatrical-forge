package com.georlegacy.general.theatrical.tiles;

import com.georlegacy.general.theatrical.api.capabilities.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.provider.IDMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.receiver.DMXReceiver;
import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import dan200.computercraft.api.lua.ILuaContext;
import dan200.computercraft.api.lua.LuaException;
import dan200.computercraft.api.peripheral.IComputerAccess;
import dan200.computercraft.api.peripheral.IPeripheral;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
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
        if (capability == DMXReceiver.CAP) {
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
                this.idmxProvider.getUniverse(world).setChannel(((Number) args[0]).intValue(), ((Number) args[1]).intValue());
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
        for(EnumFacing facing : EnumFacing.VALUES){
            TileEntity  tileEntity = world.getTileEntity(pos.offset(facing));
            if(tileEntity != null){
                if(tileEntity.hasCapability(DMXReceiver.CAP, facing.getOpposite())){
                    tileEntity.getCapability(DMXReceiver.CAP, facing.getOpposite()).receiveDMXValues(this.idmxProvider.getUniverse(world).getDMXChannels(), facing.getOpposite(), world, tileEntity.getPos());
                }
            }
        }
    }
}
