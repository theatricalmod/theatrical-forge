package dev.theatricalmod.theatrical;

import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexReceiver;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.BlockFlags;
import net.minecraftforge.fml.network.NetworkEvent.Context;
import net.minecraftforge.fml.network.PacketDistributor;

public class TheatricalCommon {

    public void init(){
    }

    public World getWorld(){
        return null;
    }

    public void handleDMXUpdate(BlockPos pos, byte[] data){}
    public void handleProviderDMXUpdate(BlockPos pos, byte[] data){}

    public void handleArtNetPacket(Context context, BlockPos pos, byte[] data){
        World world = context.getSender().world;
        TileEntity tileEntity = world.getTileEntity(pos);
        if(tileEntity instanceof TileEntityArtNetInterface){
            TileEntityArtNetInterface artNetInterface = (TileEntityArtNetInterface) tileEntity;
            if(world.getServer() != null){
                ServerPlayerEntity serverPlayerEntity = world.getServer().getPlayerList().getPlayerByUUID(artNetInterface.getPlayer());
                if(serverPlayerEntity != null){
                    if(serverPlayerEntity.hasPermissionLevel(world.getServer().getOpPermissionLevel())){
                        artNetInterface.update(data);
                    }
                }
            }
        }
    }

    public void handleUpdateDMXAddress(Context context, BlockPos pos, int address) {
        World world = context.getSender().world;
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity.getCapability(DMXReceiver.CAP, null).isPresent()) {
            tileEntity.getCapability(DMXReceiver.CAP).ifPresent(idmxReceiver -> {
                idmxReceiver.setDMXStartPoint(address);
            });
        }
        world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
    }

    public void handleUpdateArtNetInterface(Context context, BlockPos pos, int universe, String ipAddress) {
        World world = context.getSender().world;
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityArtNetInterface) {
            ((TileEntityArtNetInterface) tileEntity).setUniverse(universe);
            ((TileEntityArtNetInterface) tileEntity).setIp(ipAddress);
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
        }
    }

    public void handleUpdateFixture(Context context, BlockPos pos, int pan, int tilt) {
        World world = context.getSender().world;
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityGenericFixture) {
            ((TileEntityGenericFixture) tileEntity).setPan(pan);
            ((TileEntityGenericFixture) tileEntity).setTilt(tilt);
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
        }
    }

    public void handleConsoleFaderUpdate(Context context, BlockPos pos, int fader, int value) {
        World world = context.getSender().world;
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            ((TileEntityBasicLightingControl) tileEntity).setFader(fader, value);
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
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
                        Direction input = null;
                        if(receiver.getBlockState().hasProperty(BlockStateProperties.FACING)){
                            input = receiver.getBlockState().get(BlockStateProperties.FACING);
                        }
                        receiver.getCapability(SocapexReceiver.CAP, input).ifPresent(iSocapexReceiver -> {
                            iSocapexProvider.patch(channel, iSocapexReceiver, patch.getReceiverSocket(), socketNumber);
                        });
                    }
                } else {
                    iSocapexProvider.removePatch(channel, socketNumber);
                }
            });
        }
        world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
    }

    public void handleConsoleGo(Context context, BlockPos pos, int fadeInTicks, int fadeOutTicks){
        World world = context.getSender().world;
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            ((TileEntityBasicLightingControl) tileEntity).setFadeInTicks(fadeInTicks);
            ((TileEntityBasicLightingControl) tileEntity).setFadeOutTicks(fadeOutTicks);
            ((TileEntityBasicLightingControl) tileEntity).clickButton();
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
        }
    }
    public void handleToggleMode(Context context, BlockPos pos){
        World world = context.getSender().world;
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            ((TileEntityBasicLightingControl) tileEntity).toggleMode();
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
        }
    }
    public void handleMoveStep(Context context, BlockPos pos, boolean isForwards){
        World world = context.getSender().world;
        TileEntity tileEntity = context.getSender().world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            if(isForwards){
                ((TileEntityBasicLightingControl) tileEntity).moveForward();
            } else {
                ((TileEntityBasicLightingControl) tileEntity).moveBack();
            }
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), BlockFlags.DEFAULT_AND_RERENDER, 512);
        }
    }
}
