package dev.theatricalmod.theatrical.tiles.interfaces;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.dmx.IDMXReceiver;
import dev.theatricalmod.theatrical.capability.CapabilityDMXReceiver;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDMXRedstoneInterface;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDMXRedstoneInterface extends TileEntityTheatricalBase implements MenuProvider, IAcceptsCable {

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityDMXRedstoneInterface tile = (TileEntityDMXRedstoneInterface) be;
        if (level.isClientSide) {
            return;
        }
        tile.ticks++;
        if (tile.ticks >= 10) {
            if(tile.getRedstoneSignal() != tile.prevSignal){
                tile.prevSignal = tile.getRedstoneSignal();
                level.updateNeighborsAt(pos, state.getBlock());
            }
            tile.ticks = 0;
        }
    }


    private final IDMXReceiver idmxReceiver;
    private int ticks,prevSignal = 0;

    public TileEntityDMXRedstoneInterface(BlockPos pos, BlockState state) {
        super(TheatricalTiles.DMX_REDSTONE_INTERFACE.get(), pos, state);
        this.idmxReceiver = new CapabilityDMXReceiver(1, 0);
    }

    @Override
    public CompoundTag getNBT(@Nullable CompoundTag compoundNBT) {
        CompoundTag tag = super.getNBT(compoundNBT);
        tag.putInt("channelCount", idmxReceiver.getChannelCount());
        tag.putInt("channelStartPoint", idmxReceiver.getStartPoint());
        return tag;
    }

    @Override
    public void readNBT(CompoundTag compoundNBT) {
        super.readNBT(compoundNBT);
        idmxReceiver.setChannelCount(compoundNBT.getInt("channelCount"));
        idmxReceiver.setDMXStartPoint(compoundNBT.getInt("channelStartPoint"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == TheatricalCapabilities.CAPABILITY_DMX_RECEIVER) {
            return LazyOptional.of(() -> (T) idmxReceiver);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        if (hasLevel()) {
//            level.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
        super.setRemoved();
    }

    @Override
    public void setLevel(Level p_155231_) {
        super.setLevel(p_155231_);
        if (hasLevel()) {
//            level.getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
        }
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.DMX, CableType.DIMMED_POWER};
    }

    public IDMXReceiver getIdmxReceiver() {
        return idmxReceiver;
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("DMX->RS Interface");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return new ContainerDMXRedstoneInterface(i, level, worldPosition);
    }

    public int getRedstoneSignal(){
        return (int) ((Byte.toUnsignedInt(idmxReceiver.getChannel(0)) * 15 ) / 255F);
    }
}
