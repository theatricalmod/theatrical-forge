package com.georlegacy.general.theatrical.tiles.interfaces;

import ch.bildspur.artnet.ArtNetClient;
import com.georlegacy.general.theatrical.api.capabilities.WorldDMXNetwork;
import com.georlegacy.general.theatrical.api.capabilities.provider.DMXProvider;
import com.georlegacy.general.theatrical.api.capabilities.provider.IDMXProvider;
import com.georlegacy.general.theatrical.api.dmx.DMXUniverse;
import com.georlegacy.general.theatrical.handlers.ArtnetHandler;
import com.georlegacy.general.theatrical.util.ArtnetThread;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.annotation.Nullable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;

public class TileArtNetInterface extends TileEntity implements ITickable {

    private final IDMXProvider idmxProvider;

    private int subnet, universe, ticks = 0;
    private String ip = "";

    private ArtNetClient client;
    private boolean errored = false;

    public TileArtNetInterface() {
        this.idmxProvider = new DMXProvider(new DMXUniverse());
        client = new ArtNetClient();
    }

    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new NBTTagCompound();
        }
        nbtTagCompound.setInteger("subnet", this.subnet);
        nbtTagCompound.setInteger("universe", this.universe);
        nbtTagCompound.setString("ip", this.ip);
        return nbtTagCompound;
    }

    public void readNBT(NBTTagCompound nbtTagCompound) {
        subnet = nbtTagCompound.getInteger("subnet");
        universe = nbtTagCompound.getInteger("universe");
        ip = nbtTagCompound.getString("ip");
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        readNBT(compound);
        super.readFromNBT(compound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = getNBT(compound);
        return super.writeToNBT(compound);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 1, getNBT(null));
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound nbtTagCompound = super.getUpdateTag();
        nbtTagCompound = getNBT(nbtTagCompound);
        return nbtTagCompound;
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        readNBT(tag);
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        NBTTagCompound tag = pkt.getNbtCompound();
        readNBT(tag);
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == DMXProvider.CAP) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == DMXProvider.CAP) {
            return DMXProvider.CAP.cast(idmxProvider);
        }
        return super.getCapability(capability, facing);
    }

    public void sendDMXSignal(){
        WorldDMXNetwork.getCapability(world).updateDevices();
    }

    public int getSubnet() {
        return subnet;
    }

    public int getUniverse() {
        return universe;
    }

    public void setSubnet(int subnet) {
        this.subnet = subnet;
    }

    public void setUniverse(int universe) {
        this.universe = universe;
    }

    public String getIp() {
        return ip;
    }

    public boolean startClient(){
        if(client.isRunning()){
            client.stop();
        }
        try {
            InetAddress.getByName(ip);
            new ArtnetThread(ip, client).start();
            errored = false;
        } catch (UnknownHostException e) {
            e.printStackTrace();
            errored = true;
            world.getClosestPlayer(pos.getX(), pos.getY(), pos.getZ(), 2, false).sendMessage(new
                TextComponentString("We were unable to find a network interface on that IP."));
        }
        return errored;
    }

    public void setIp(String ip) {
        this.ip = ip;
        if(client != null) {
            client.stop();
            startClient();
        }
    }

    public void disconnectClient(){
        client.stop();
    }

    @Override
    public void update() {
        if(world.isRemote)
        {
            return;
        }
        if(errored){
            return;
        }
        if(!client.isRunning()){
            if(!startClient()){
                return;
            }
        }
        byte[] data = ArtnetHandler.getClient().readDmxData(subnet, universe);
        this.idmxProvider.getUniverse(world).setDmxChannels(data);
        sendDMXSignal();
    }

    @Override
    public void invalidate()
    {
        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }

        disconnectClient();
        super.invalidate();
    }

    @Override
    public void setWorld(World world)
    {
        super.setWorld(world);

        if (hasWorld())
        {
            WorldDMXNetwork.getCapability(getWorld()).setRefresh(true);
        }
    }
}
