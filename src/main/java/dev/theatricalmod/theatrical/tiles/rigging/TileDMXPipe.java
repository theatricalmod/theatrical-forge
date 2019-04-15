package dev.theatricalmod.theatrical.tiles.rigging;

import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.tiles.TileBase;
import dev.theatricalmod.theatrical.tiles.TileDMXAcceptor;
import dev.theatricalmod.theatrical.tiles.cables.CableType;
import javax.annotation.Nullable;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileDMXPipe extends TileBase implements ITickable, IAcceptsCable {


    private int ticks = 0;

    private DMXReceiver receiver;

    public TileDMXPipe() {
        receiver = new DMXReceiver(255, 0);
    }

    @Override
    public void invalidate() {
        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }

        super.invalidate();
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);

        if (hasWorld()) {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }


    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == DMXReceiver.CAP) {
            return DMXReceiver.CAP.cast(receiver);
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void update() {
        if (world.isRemote) {
            return;
        }
        if (world.getTileEntity(pos.offset(EnumFacing.DOWN)) != null) {
            BlockPos light = pos.offset(EnumFacing.DOWN);
            TileEntity tileEntity = world.getTileEntity(light);
            if (tileEntity instanceof TileDMXAcceptor) {
                TileDMXAcceptor tileDMXAcceptor = (TileDMXAcceptor) tileEntity;
                tileDMXAcceptor.getCapability(DMXReceiver.CAP, null).receiveDMXValues(receiver.getDmxValues(), world, light);
            }
        }
    }

    @Override
    public CableType[] getAcceptedCables() {
        return new CableType[]{CableType.DMX};
    }
}
