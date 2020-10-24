package dev.theatricalmod.theatrical.tiles.interfaces;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.client.gui.container.ContainerArtNetInterface;
import dev.theatricalmod.theatrical.network.SendArtNetToServerPacket;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.Dimension;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.UUID;

public class TileEntityArtNetInterface extends TileEntityTheatricalBase implements ITickableTileEntity, INamedContainerProvider, IAcceptsCable {

    private final IDMXProvider idmxProvider;

    private int subnet, universe, ticks = 0;
    private String ip = "127.0.0.1";
    private UUID player;

    public TileEntityArtNetInterface() {
        super(TheatricalTiles.ARTNET_INTERFACE.get());
        this.idmxProvider = new DMXProvider(new DMXUniverse());
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public UUID getPlayer() {
        return player;
    }

    @Override
    public void tick() {
        if (!world.isRemote) {
            return;
        }
        if(player.equals(UUID.fromString(Minecraft.getInstance().getSession().getPlayerID()))){
            ticks++;
            if (ticks >= 2) {
                byte[] data = TheatricalMod.getArtNetManager().getClient(ip).readDmxData(this.subnet, this.universe);
                TheatricalNetworkHandler.MAIN.sendToServer(new SendArtNetToServerPacket(pos, data));
                ticks = 0;
            }
        }
    }

    public void update(byte[] data){
        this.idmxProvider.getUniverse(world).setDmxChannels(data);
        TheatricalNetworkHandler.MAIN.send(PacketDistributor.DIMENSION.with(world::getDimensionKey), new SendDMXProviderPacket(pos, data));
        sendDMXSignal();
    }

    @Override
    public CompoundNBT getNBT(@Nullable CompoundNBT nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new CompoundNBT();
        }
        nbtTagCompound.putInt("subnet", this.subnet);
        nbtTagCompound.putInt("universe", this.universe);
        nbtTagCompound.putString("ip", this.ip);
        nbtTagCompound.putString("owner", this.player.toString());
        return nbtTagCompound;
    }

    @Override
    public void readNBT(CompoundNBT nbtTagCompound) {
        subnet = nbtTagCompound.getInt("subnet");
        universe = nbtTagCompound.getInt("universe");
        ip = nbtTagCompound.getString("ip");
        if(nbtTagCompound.contains("owner")) {
            this.player = UUID.fromString(nbtTagCompound.getString("owner"));
        }
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction direction) {
        if (cap == DMXProvider.CAP) {
            return DMXProvider.CAP.orEmpty(cap, LazyOptional.of(() -> idmxProvider));
        }
        return super.getCapability(cap, direction);
    }


    public void sendDMXSignal() {
        idmxProvider.updateDevices(world, pos);
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

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public void remove() {
        if (hasWorld()) {
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
        super.remove();
    }

    @Override
    public void setWorldAndPos(World p_226984_1_, BlockPos p_226984_2_) {
        super.setWorldAndPos(p_226984_1_, p_226984_2_);
        if(hasWorld()){
            world.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
    }


    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("ArtNet Interface");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new ContainerArtNetInterface(i, world, pos);
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.DMX};
    }
}
