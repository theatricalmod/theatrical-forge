package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.capabilities.WorldSocapexNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import net.minecraft.tileentity.TileEntity;

public class TileEntityCable extends TileEntity {

    private CableType type;

    public TileEntityCable() {
        super(TheatricalTiles.CABLE.get());
    }

    public void setCableType(CableType cableType){
        type = cableType;
    }

    @Override
    public void validate() {
        super.validate();
        updateWorldNetworks();
    }

    @Override
    public void remove() {
        updateWorldNetworks();
        super.remove();
    }

    private void updateWorldNetworks() {
        if(hasWorld()){
            if(type == CableType.SOCAPEX){
                world.getCapability(WorldSocapexNetwork.CAP).ifPresent(worldDMXNetwork -> {
                    worldDMXNetwork.setRefresh(true);
                });
            } else if(type == CableType.DMX){
                world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> {
                    worldDMXNetwork.setRefresh(true);
                });
            }
        }
    }
}
