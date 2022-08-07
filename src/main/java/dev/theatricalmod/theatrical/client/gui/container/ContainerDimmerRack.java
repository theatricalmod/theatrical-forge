package dev.theatricalmod.theatrical.client.gui.container;

import dev.theatricalmod.theatrical.api.capabilities.socapex.ISocapexReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;

//TODO: Fix socapex!
public class ContainerDimmerRack extends AbstractContainerMenu {

    public final TileEntityDimmerRack dimmerRack;
    protected final Level world;
    private List<ISocapexReceiver> receivers;

    public ContainerDimmerRack(int id, Level world, BlockPos pos) {
        super(TheatricalContainers.DIMMER_RACK.get(), id);

        this.world = world;
        this.dimmerRack = (TileEntityDimmerRack) world.getBlockEntity(pos);
//        dimmerRack.getCapability(SocapexProvider.CAP, null).ifPresent(iSocapexProvider -> {
//            receivers = iSocapexProvider.getDevices(dimmerRack.getLevel(), dimmerRack.getBlockPos());
//        });
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return true;
    }


    public List<ISocapexReceiver> getDevices() {
        return receivers;
    }

    public Optional<int[]> getChannelsForReceiver(ISocapexReceiver iSocapexReceiver) {
//        if (dimmerRack.getCapability(SocapexProvider.CAP, null).isPresent()) {
//            return dimmerRack.getCapability(SocapexProvider.CAP, null).map(iSocapexProvider -> iSocapexProvider.getPatchedCables(iSocapexReceiver));
//        }
        return Optional.empty();
    }

    @Nullable
    public SocapexPatch[] getPatch(int i) {
//        if (dimmerRack.getCapability(SocapexProvider.CAP, null).isPresent()) {
//            return dimmerRack.getCapability(SocapexProvider.CAP, null).orElse(null).getPatch(i);
//        }
        return null;
    }

    public String getIdentifier(BlockPos pos){
        return "";
//        return dimmerRack.getCapability(SocapexProvider.CAP, null).orElse(null).getIdentifier(pos);
    }
}
