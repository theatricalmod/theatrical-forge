package dev.theatricalmod.theatrical.api.capabilities;

import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexProvider;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import java.util.HashMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class WorldSocapexNetwork implements ICapabilityProvider {

    @CapabilityInject(WorldSocapexNetwork.class)
    public static final Capability<WorldSocapexNetwork> CAP = null;
    private final LazyOptional<WorldSocapexNetwork> instance = LazyOptional.of(CAP::getDefaultInstance);

//    public static WorldSocapexNetwork getCapability(World world) {
//        return world.getCapability(CAP).;
//    }

    private final HashMap<BlockPos, ISocapexProvider> panelList = new HashMap<>();
    private boolean refresh = true;

    public WorldSocapexNetwork() {
    }

    public void updateDevices(World world) {
        for (BlockPos blockPos : panelList.keySet()) {
            panelList.get(blockPos).updateDevices(world, blockPos);
        }
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public void tick(World world) {
        if (refresh) {
            panelList.clear();
            for (TileEntity tileEntity : world.loadedTileEntityList) {
                if (!tileEntity.isRemoved() && tileEntity.getCapability(SocapexProvider.CAP, null).isPresent()) {
                    panelList.put(tileEntity.getPos(), tileEntity.getCapability(SocapexProvider.CAP, null).orElse(null));
                }
            }

            for (ISocapexProvider provider : panelList.values()) {
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
