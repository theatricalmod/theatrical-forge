package com.georlegacy.general.theatrical.api.capabilities;

import com.georlegacy.general.theatrical.tiles.TilePipePanel;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class WorldPipePanelNetwork implements ICapabilityProvider {

    @CapabilityInject(WorldPipePanelNetwork.class)
    public static Capability<WorldPipePanelNetwork> CAP;

    public static WorldPipePanelNetwork getCapability(World world) {
        return world.getCapability(CAP, null);
    }

    public final World world;
    private List<TilePipePanel> panelList = new ArrayList<>();
    private boolean refresh = true;

    public WorldPipePanelNetwork(World world) {
        this.world = world;
    }

    public void updateDevices() {
        for (TilePipePanel provider : panelList) {
            provider.findPipes();
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
                if (!tileEntity.isInvalid() && tileEntity instanceof TilePipePanel) {
                    panelList.add((TilePipePanel) tileEntity);
                }
            }

            for (TilePipePanel provider : panelList) {
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
