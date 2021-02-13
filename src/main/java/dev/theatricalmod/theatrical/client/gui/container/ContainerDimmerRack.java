package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

public class ContainerDimmerRack extends Container {

    public final TileEntityDimmerRack dimmerRack;
    protected final World world;
    private List<ISocapexReceiver> receivers;

    public ContainerDimmerRack(int id, World world, BlockPos pos) {
        super(TheatricalContainers.DIMMER_RACK.get(), id);

        this.world = world;
        this.dimmerRack = (TileEntityDimmerRack) world.getTileEntity(pos);
        dimmerRack.getCapability(SocapexProvider.CAP, null).ifPresent(iSocapexProvider -> {
            receivers = iSocapexProvider.getDevices(dimmerRack.getWorld(), dimmerRack.getPos());
        });
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return true;
    }


    public List<ISocapexReceiver> getDevices() {
        return receivers;
    }

    public Optional<int[]> getChannelsForReceiver(ISocapexReceiver iSocapexReceiver) {
        if (dimmerRack.getCapability(SocapexProvider.CAP, null).isPresent()) {
            return dimmerRack.getCapability(SocapexProvider.CAP, null).map(iSocapexProvider -> iSocapexProvider.getPatchedCables(iSocapexReceiver));
        }
        return Optional.empty();
    }

    @Nullable
    public SocapexPatch[] getPatch(int i) {
        if (dimmerRack.getCapability(SocapexProvider.CAP, null).isPresent()) {
            return dimmerRack.getCapability(SocapexProvider.CAP, null).orElse(null).getPatch(i);
        }
        return null;
    }

    public String getIdentifier(BlockPos pos){
        return dimmerRack.getCapability(SocapexProvider.CAP, null).orElse(null).getIdentifier(pos);
    }
}
