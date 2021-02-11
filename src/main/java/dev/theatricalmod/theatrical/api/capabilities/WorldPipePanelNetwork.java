package dev.theatricalmod.theatrical.api.capabilities;

import java.util.ArrayList;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

public class WorldPipePanelNetwork implements ICapabilityProvider {

    @CapabilityInject(WorldPipePanelNetwork.class)
    public static Capability<WorldPipePanelNetwork> CAP;
    private final LazyOptional<WorldPipePanelNetwork> instance = LazyOptional.of(CAP::getDefaultInstance);

//    public static WorldPipePanelNetwork getCapability(World world) {
//        return world.getCapability(CAP, null);
//    }

    public final World world;
//    private List<TilePipePanel> panelList = new ArrayList<>();
    private boolean refresh = true;

    public WorldPipePanelNetwork(World world) {
        this.world = world;
    }
//
//    public void updateDevices() {
//        for (TilePipePanel provider : panelList) {
//            provider.findPipes();
//        }
//    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

//    public void tick() {
//        if (refresh) {
//            panelList.clear();
//            for (TileEntity tileEntity : world.loadedTileEntityList) {
//                if (!tileEntity.isInvalid() && tileEntity instanceof TilePipePanel) {
//                    panelList.add((TilePipePanel) tileEntity);
//                }
//            }
//
//            for (TilePipePanel provider : panelList) {
//                provider.refreshDevices();
//            }
//            refresh = false;
//        }
//    }
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == CAP ? instance.cast() : LazyOptional.empty();
    }

}
