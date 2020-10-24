package dev.theatricalmod.theatrical.tiles.interfaces;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.IDMXReceiver;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDMXRedstoneInterface;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import dev.theatricalmod.theatrical.tiles.TileEntityTheatricalBase;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDMXRedstoneInterface extends TileEntityTheatricalBase implements ITickableTileEntity, INamedContainerProvider, IAcceptsCable {

    private final IDMXReceiver idmxReceiver;
    private int ticks,prevSignal = 0;

    public TileEntityDMXRedstoneInterface() {
        super(TheatricalTiles.DMX_REDSTONE_INTERFACE.get());
        this.idmxReceiver = new DMXReceiver(1, 0);
    }

    @Override
    public CompoundNBT getNBT(@Nullable CompoundNBT compoundNBT) {
        CompoundNBT tag = super.getNBT(compoundNBT);
        tag.putInt("channelCount", idmxReceiver.getChannelCount());
        tag.putInt("channelStartPoint", idmxReceiver.getStartPoint());
        return tag;
    }

    @Override
    public void readNBT(CompoundNBT compoundNBT) {
        super.readNBT(compoundNBT);
        idmxReceiver.setChannelCount(compoundNBT.getInt("channelCount"));
        idmxReceiver.setDMXStartPoint(compoundNBT.getInt("channelStartPoint"));
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == DMXReceiver.CAP) {
            return LazyOptional.of(() -> (T) idmxReceiver);
        }
        return super.getCapability(cap, side);
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
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.DMX, CableType.DIMMED_POWER};
    }

    public IDMXReceiver getIdmxReceiver() {
        return idmxReceiver;
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }
        ticks++;
        if (ticks >= 10) {
            if(getRedstoneSignal() != prevSignal){
                prevSignal = getRedstoneSignal();
                world.notifyNeighborsOfStateChange(pos, world.getBlockState(pos).getBlock());
            }
            ticks = 0;
        }
    }


    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("DMX->RS Interface");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new ContainerDMXRedstoneInterface(i, world, pos);
    }

    public int getRedstoneSignal(){
        return (int) ((Byte.toUnsignedInt(idmxReceiver.getChannel(0)) * 15 ) / 255F);
    }
}
