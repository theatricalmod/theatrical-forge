package dev.theatricalmod.theatrical.api.capabilities.dmx;

import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;

public class WorldDMXNetwork implements ICapabilityProvider {

    @CapabilityInject(WorldDMXNetwork.class)
    public static final Capability<WorldDMXNetwork> CAP = null;
    private final LazyOptional<WorldDMXNetwork> instance = LazyOptional.of(CAP::getDefaultInstance);

    private final HashMap<IDMXProvider, BlockPos> providerList = new HashMap<>();
    private boolean refresh = true;
    public WorldDMXNetwork() {
    }

//    public void updateDevices(){
//        for(IDMXProvider provider : providerList.keySet()){
//            provider.updateDevices(world, providerList.get(provider));
//        }
//    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void tick(World world){
        if(refresh){
            providerList.clear();
            for(TileEntity tileEntity : world.loadedTileEntityList){
                if(!tileEntity.isRemoved() && tileEntity.getCapability(DMXProvider.CAP).isPresent()){
                    providerList.put(tileEntity.getCapability(DMXProvider.CAP).orElse(null), tileEntity.getPos());
                }
            }

            for(IDMXProvider provider : providerList.keySet()){
                provider.refreshDevices();
            }
            refresh = false;
        }
    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAP ? instance.cast() : LazyOptional.empty();
    }

}
