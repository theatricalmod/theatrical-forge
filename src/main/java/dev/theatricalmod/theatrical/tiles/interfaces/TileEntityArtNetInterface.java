package dev.theatricalmod.theatrical.tiles.interfaces;

import com.mojang.util.UUIDTypeAdapter;
import dev.theatricalmod.theatrical.TheatricalMod;
import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.dmx.DMXUniverse;
import dev.theatricalmod.theatrical.capability.CapabilityDMXProvider;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.client.gui.container.ContainerArtNetInterface;
import dev.theatricalmod.theatrical.network.SendArtNetToServerPacket;
import dev.theatricalmod.theatrical.network.SendDMXProviderPacket;
import dev.theatricalmod.theatrical.network.TheatricalNetworkHandler;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.UUID;

public class TileEntityArtNetInterface extends TileEntityTheatricalBase implements MenuProvider, IAcceptsCable {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityArtNetInterface tile = (TileEntityArtNetInterface) be;
        if (!level.isClientSide) {
            return;
        }
        if(tile.player != null && tile.player.equals(UUIDTypeAdapter.fromString(Minecraft.getInstance().getUser().getUuid()))){
            tile.ticks++;
            if (tile.ticks >= 2) {
                byte[] data = TheatricalMod.getArtNetManager().getClient(tile.ip).readDmxData(tile.subnet, tile.universe);
                TheatricalNetworkHandler.MAIN.sendToServer(new SendArtNetToServerPacket(pos, data));
                tile.ticks = 0;
            }
        }
    }

    private final CapabilityDMXProvider dmxProvider;
    private int subnet, universe, ticks = 0;
    private String ip = "127.0.0.1";
    private UUID player;

    public TileEntityArtNetInterface(BlockPos blockPos, BlockState blockState) {
        super(TheatricalTiles.ARTNET_INTERFACE.get(), blockPos, blockState);
        this.dmxProvider = new CapabilityDMXProvider(new DMXUniverse());
    }

    public void setPlayer(UUID player) {
        this.player = player;
    }

    public UUID getPlayer() {
        return player;
    }

    public void update(byte[] data){
        this.dmxProvider.getUniverse(level).setDmxChannels(data);
        TheatricalNetworkHandler.MAIN.send(PacketDistributor.DIMENSION.with(level::dimension), new SendDMXProviderPacket(worldPosition, data));
        sendDMXSignal();
    }

    @Override
    public CompoundTag getNBT(@Nullable CompoundTag nbtTagCompound) {
        if (nbtTagCompound == null) {
            nbtTagCompound = new CompoundTag();
        }
        nbtTagCompound.putInt("subnet", this.subnet);
        nbtTagCompound.putInt("universe", this.universe);
        nbtTagCompound.putString("ip", this.ip);
        if(this.player != null) {
            nbtTagCompound.putString("owner", this.player.toString());
        }
        return nbtTagCompound;
    }

    @Override
    public void readNBT(CompoundTag nbtTagCompound) {
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
        if (cap == TheatricalCapabilities.CAPABILITY_DMX_PROVIDER) {
            return TheatricalCapabilities.CAPABILITY_DMX_PROVIDER.orEmpty(cap, LazyOptional.of(() -> dmxProvider));
        }
        return super.getCapability(cap, direction);
    }


    public void sendDMXSignal() {
        dmxProvider.updateDevices(level, worldPosition);
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
    public Component getDisplayName() {
        return new TextComponent("ArtNet Interface");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return new ContainerArtNetInterface(i, level, worldPosition);
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.DMX};
    }
}
