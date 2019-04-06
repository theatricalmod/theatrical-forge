package com.georlegacy.general.theatrical.api.capabilities;

import com.georlegacy.general.theatrical.api.capabilities.socapex.ISocapexProvider;
import com.georlegacy.general.theatrical.api.capabilities.socapex.SocapexProvider;
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

public class WorldSocapexNetwork implements ICapabilityProvider {

    @CapabilityInject(WorldSocapexNetwork.class)
    public static Capability<WorldSocapexNetwork> CAP;

    public static WorldSocapexNetwork getCapability(World world) {
        return world.getCapability(CAP, null);
    }

    public final World world;
    private HashMap<BlockPos, ISocapexProvider> panelList = new HashMap<>();
    private boolean refresh = true;

    public WorldSocapexNetwork(World world) {
        this.world = world;
    }

    public void updateDevices() {
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

    public void tick() {
        if (refresh) {
            panelList.clear();
            for (TileEntity tileEntity : world.loadedTileEntityList) {
                if (!tileEntity.isInvalid() && tileEntity.hasCapability(SocapexProvider.CAP, null)) {
                    panelList.put(tileEntity.getPos(), tileEntity.getCapability(SocapexProvider.CAP, null));
                }
            }

            for (ISocapexProvider provider : panelList.values()) {
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
