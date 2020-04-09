package dev.theatricalmod.theatrical;

import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import dev.theatricalmod.theatrical.tiles.TileEntityArtNetInterface;
import dev.theatricalmod.theatrical.tiles.TileEntityDimmerRack;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityIntelligentFixture;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class TheatricalCommon {

    public void init(){
    }

    public World getWorld(){
        return null;
    }

    public void handleDMXUpdate(BlockPos pos, byte[] data){}
    public void handleProviderDMXUpdate(BlockPos pos, byte[] data){}

    public void handleUpdateDMXAddress(Context context, BlockPos pos, int address) {
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityIntelligentFixture) {
            tileEntity.getCapability(DMXReceiver.CAP).ifPresent(idmxReceiver -> {
                idmxReceiver.setDMXStartPoint(address);
                context.getSender().connection.sendPacket(tileEntity.getUpdatePacket());
            });
        }
    }

    public void handleUpdateArtNetInterface(Context context, BlockPos pos, int universe) {
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityArtNetInterface) {
            ((TileEntityArtNetInterface) tileEntity).setUniverse(universe);
            context.getSender().connection.sendPacket(tileEntity.getUpdatePacket());
        }
    }

    public void handleUpdateFixture(Context context, BlockPos pos, int pan, int tilt) {
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityGenericFixture) {
            ((TileEntityGenericFixture) tileEntity).setPan(pan);
            ((TileEntityGenericFixture) tileEntity).setTilt(tilt);
        }
    }

    public void handleChangeDimmerPatch(Context context, BlockPos pos, SocapexPatch patch, int channel, int socketNumber) {
        World world = context.getSender().world;
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityDimmerRack) {
            TileEntityDimmerRack tileEntityDimmerRack = (TileEntityDimmerRack) tileEntity;
            tileEntityDimmerRack.getCapability(SocapexProvider.CAP, null).ifPresent(iSocapexProvider -> {
                if (patch.getReceiver() != null) {
                    TileEntity receiver = world.getTileEntity(patch.getReceiver());
                    if (receiver != null) {
                        receiver.getCapability(SocapexReceiver.CAP, null).ifPresent(iSocapexReceiver -> {
                            iSocapexProvider.patch(channel, iSocapexReceiver, patch.getReceiverSocket(), socketNumber);
                        });
                    }
                } else {
                    iSocapexProvider.removePatch(channel, socketNumber);
                }
            });
        }
        world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER);
    }
}
