package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.ConnectableType;
import dev.theatricalmod.theatrical.capability.CapabilityConnectable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityCable extends BlockEntity {

    private CableType type;
    private CapabilityConnectable capabilityConnectable;

    public TileEntityCable(BlockPos pos, BlockState state) {
        super(TheatricalTiles.CABLE.get(), pos, state);
        capabilityConnectable = new CapabilityConnectable(pos, new ConnectableType[0]);
    }

    public void setCableType(CableType cableType){
        type = cableType;
        this.capabilityConnectable.setAllowedConnectionTypes(new ConnectableType[]{cableType.getConnectableType()});
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        updateWorldNetworks();
    }

    @Override
    public void setRemoved() {
        updateWorldNetworks();
        super.setRemoved();
    }

    private void updateWorldNetworks() {
    }
}
