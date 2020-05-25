package dev.theatricalmod.theatrical.tiles.interfaces;

import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.DMXProvider;
import dev.theatricalmod.theatrical.api.capabilities.dmx.provider.IDMXProvider;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.client.gui.container.ContainerArtNetInterface;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
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
import net.minecraft.world.World;
import net.minecraft.world.dimension.Dimension;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.PacketDistributor;

public class TileEntityArtNetInterface extends TileEntity implements ITickableTileEntity, INamedContainerProvider, IAcceptsCable {

    private final IDMXProvider idmxProvider;

    private int subnet, universe, ticks = 0;
    private String ip = "127.0.0.1";

    public TileEntityArtNetInterface() {
        super(TheatricalTiles.ARTNET_INTERFACE.get());
        this.idmxProvider = new DMXProvider(new DMXUniverse());
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        ticks++;
        if (ticks >= 2) {
            byte[] data = TheatricalMod.getArtNetManager().getClient(ip).readDmxData(this.subnet, this.universe);
            this.idmxProvider.getUniverse(world).setDmxChannels(data);
            Dimension dimension = world.dimension;
            TheatricalNetworkHandler.MAIN.send(PacketDistributor.DIMENSION.with(dimension::getType), new SendDMXProviderPacket(pos, data));
            sendDMXSignal();
            ticks = 0;
        }
    }

    public CompoundNBT getNBT(@Nullable CompoundNBT nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new CompoundNBT();
        }
        nbtTagCompound.putInt("subnet", this.subnet);
        nbtTagCompound.putInt("universe", this.universe);
        nbtTagCompound.putString("ip", this.ip);
        return nbtTagCompound;
    }

    public void readNBT(CompoundNBT nbtTagCompound) {
        subnet = nbtTagCompound.getInt("subnet");
        universe = nbtTagCompound.getInt("universe");
        ip = nbtTagCompound.getString("ip");
    }

    @Override
    public void read(CompoundNBT compound) {
        readNBT(compound);
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = getNBT(compound);
        return super.write(compound);
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readNBT(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(CompoundNBT tag) {
        readNBT(tag);
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(pos, 0, getNBT(null));
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return getNBT(null);
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
        if (hasWorld()) {
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
