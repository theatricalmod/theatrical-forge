package com.georlegacy.general.theatrical.api.capabilities.dmx;

import com.georlegacy.general.theatrical.api.capabilities.dmx.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class WorldDMXNetwork implements ICapabilityProvider {

    @CapabilityInject(WorldDMXNetwork.class)
    public static Capability<WorldDMXNetwork> CAP;

    public static WorldDMXNetwork getCapability(World world){
        return world.getCapability(CAP, null);
    }

    public final World world;
    private HashMap<IDMXProvider, BlockPos> providerList = new HashMap<>();
    private boolean refresh = true;

    public WorldDMXNetwork(World world) {
        this.world = world;
    }

    public void updateDevices(){
        for(IDMXProvider provider : providerList.keySet()){
            provider.updateDevices(world, providerList.get(provider));
        }
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void tick(){
        if(refresh){
            providerList.clear();
            for(TileEntity tileEntity : world.loadedTileEntityList){
                if(!tileEntity.isInvalid() && tileEntity.hasCapability(DMXProvider.CAP, null)){
                    providerList.put(tileEntity.getCapability(DMXProvider.CAP, null), tileEntity.getPos());
                }
            }

            for(IDMXProvider provider : providerList.keySet()){
                provider.refreshDevices();
            }
            refresh = false;
        }
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return capability == CAP;
    }

    @Nullable
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        return capability == CAP ? (T) this : null;
    }
}
