package dev.theatricalmod.theatrical;

import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexPatch;
import dev.theatricalmod.theatrical.tiles.control.TileEntityBasicLightingControl;
import dev.theatricalmod.theatrical.tiles.interfaces.TileEntityArtNetInterface;
import dev.theatricalmod.theatrical.tiles.lights.TileEntityGenericFixture;
import dev.theatricalmod.theatrical.tiles.power.TileEntityDimmerRack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

public class TheatricalCommon {

    public static int DEFAULT_AND_RERENDER = Block.UPDATE_NEIGHBORS | Block.UPDATE_CLIENTS | Block.UPDATE_IMMEDIATE;

    public void init(){
    }

    public Level getWorld(){
        return null;
    }

    public void handleDMXUpdate(BlockPos pos, byte[] data){}
    public void handleProviderDMXUpdate(BlockPos pos, byte[] data){}

    public void handleArtNetPacket(NetworkEvent.Context context, BlockPos pos, byte[] data){
        Level world = context.getSender().level;
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if(tileEntity instanceof TileEntityArtNetInterface){
            TileEntityArtNetInterface artNetInterface = (TileEntityArtNetInterface) tileEntity;
            if(world.getServer() != null){
                ServerPlayer serverPlayerEntity = world.getServer().getPlayerList().getPlayer(artNetInterface.getPlayer());
                if(serverPlayerEntity != null){
                    if(serverPlayerEntity.hasPermissions(world.getServer().getOperatorUserPermissionLevel())){
                        artNetInterface.update(data);
                    }
                }
            }
        }
    }

    public void handleUpdateDMXAddress(NetworkEvent.Context  context, BlockPos pos, int address) {
        Level world = context.getSender().level;
        BlockEntity tileEntity = context.getSender().level.getBlockEntity(pos);
//        if (tileEntity.getCapability(DMXReceiver.CAP, null).isPresent()) {
//            tileEntity.getCapability(DMXReceiver.CAP).ifPresent(idmxReceiver -> {
//                idmxReceiver.setDMXStartPoint(address);
//            });
//        }
        world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
    }

    public void handleUpdateArtNetInterface(NetworkEvent.Context  context, BlockPos pos, int universe, String ipAddress) {
        Level world = context.getSender().level;
        BlockEntity tileEntity = context.getSender().level.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityArtNetInterface) {
            ((TileEntityArtNetInterface) tileEntity).setUniverse(universe);
            ((TileEntityArtNetInterface) tileEntity).setIp(ipAddress);
            if(((TileEntityArtNetInterface) tileEntity).getPlayer() == null){
                ((TileEntityArtNetInterface) tileEntity).setPlayer(context.getSender().getUUID());
            }
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
        }
    }

    public void handleUpdateFixture(NetworkEvent.Context  context, BlockPos pos, int pan, int tilt) {
        Level world = context.getSender().level;
        BlockEntity tileEntity = context.getSender().level.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityGenericFixture) {
            ((TileEntityGenericFixture) tileEntity).setPan(pan);
            ((TileEntityGenericFixture) tileEntity).setTilt(tilt);
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
        }
    }

    public void handleConsoleFaderUpdate(NetworkEvent.Context  context, BlockPos pos, int fader, int value) {
        Level world = context.getSender().level;
        BlockEntity tileEntity = world.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            ((TileEntityBasicLightingControl) tileEntity).setFader(fader, value);
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
        }
    }

    public void handleChangeDimmerPatch(NetworkEvent.Context  context, BlockPos pos, SocapexPatch patch, int channel, int socketNumber) {
        Level world = context.getSender().level;
        BlockEntity tileEntity = context.getSender().level.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityDimmerRack) {
            TileEntityDimmerRack tileEntityDimmerRack = (TileEntityDimmerRack) tileEntity;
            //TODO: re-implement

//                tileEntityDimmerRack.getCapability(TheatricalCapabilities, null).ifPresent(iSocapexProvider -> {
//                if (patch.getReceiver() != null) {
//                    BlockEntity receiver = world.getBlockEntity(patch.getReceiver());
//                    if (receiver != null) {
//                        Direction input = null;
//                        if(receiver.getBlockState().hasProperty(BlockStateProperties.FACING)){
//                            input = receiver.getBlockState().getValue(BlockStateProperties.FACING);
//                        }
//                        receiver.getCapability(SocapexReceiver.CAP, input).ifPresent(iSocapexReceiver -> {
//                            iSocapexProvider.patch(channel, iSocapexReceiver, patch.getReceiverSocket(), socketNumber);
//                        });
//                    }
//                } else {
//                    iSocapexProvider.removePatch(channel, socketNumber);
//                }
//            });
        }
        world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
    }

    public void handleConsoleGo(NetworkEvent.Context  context, BlockPos pos, int fadeInTicks, int fadeOutTicks){
        Level world = context.getSender().level;
        BlockEntity tileEntity = context.getSender().level.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            ((TileEntityBasicLightingControl) tileEntity).setFadeInTicks(fadeInTicks);
            ((TileEntityBasicLightingControl) tileEntity).setFadeOutTicks(fadeOutTicks);
            ((TileEntityBasicLightingControl) tileEntity).clickButton();
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
        }
    }
    public void handleToggleMode(NetworkEvent.Context  context, BlockPos pos){
        Level world = context.getSender().level;
        BlockEntity tileEntity = context.getSender().level.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            ((TileEntityBasicLightingControl) tileEntity).toggleMode();
                world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
        }
    }
    public void handleMoveStep(NetworkEvent.Context  context, BlockPos pos, boolean isForwards){
        Level world = context.getSender().level;
        BlockEntity tileEntity = context.getSender().level.getBlockEntity(pos);
        if (tileEntity instanceof TileEntityBasicLightingControl) {
            if(isForwards){
                ((TileEntityBasicLightingControl) tileEntity).moveForward();
            } else {
                ((TileEntityBasicLightingControl) tileEntity).moveBack();
            }
            world.markAndNotifyBlock(pos, world.getChunkAt(pos), world.getBlockState(pos), world.getBlockState(pos), DEFAULT_AND_RERENDER, 512);
        }
    }
}
