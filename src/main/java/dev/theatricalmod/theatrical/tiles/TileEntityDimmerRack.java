package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.WorldSocapexNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.WorldDMXNetwork;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

public class TileEntityDimmerRack extends TileEntity implements INamedContainerProvider, ITickableTileEntity, IEnergyStorage, IAcceptsCable {

    private int dmxStart = 0;

    private DMXReceiver dmxReceiver;
    private SocapexProvider socapexProvider;

    private int[] channels;
    private int power;

    private int ticks = 0;

    public TileEntityDimmerRack() {
        super(TheatricalTiles.DIMMER_RACK.get());
        dmxReceiver = new DMXReceiver(6, dmxStart);
        socapexProvider = new SocapexProvider();
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Dimmer Rack");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
        return new ContainerDimmerRack(i, world, getPos());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == DMXReceiver.CAP) {
            return DMXReceiver.CAP.orEmpty(cap, LazyOptional.of(() -> dmxReceiver));
        }
        if (cap == SocapexProvider.CAP) {
            return SocapexProvider.CAP.orEmpty(cap, LazyOptional.of(() -> socapexProvider));
        }
        if (cap == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() -> this));
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void read(CompoundNBT compound) {
        dmxStart = compound.getInt("dmxStart");
        if (compound.contains("provider")) {
            socapexProvider.deserializeNBT(compound.getCompound("provider"));
        }
        super.read(compound);
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound = super.write(compound);
        compound.putInt("dmxStart", dmxStart);
        compound.put("provider", socapexProvider.serializeNBT());
        return super.write(compound);
    }

    @Override
    public CableType[] getAcceptedCables() {
        return new CableType[]{CableType.POWER, CableType.SOCAPEX, CableType.DMX};
    }

    @Override
    public void remove() {
        if (hasWorld()) {
            getWorld().getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
            getWorld().getCapability(WorldSocapexNetwork.CAP).ifPresent(worldSocapexNetwork -> worldSocapexNetwork.setRefresh(true));
        }
        super.remove();
    }

    @Override
    public void validate() {
        if (hasWorld()) {
            getWorld().getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
            getWorld().getCapability(WorldSocapexNetwork.CAP).ifPresent(worldSocapexNetwork -> worldSocapexNetwork.setRefresh(true));
        }
        super.validate();
    }

    @Override
    public void tick() {
        if (world.isRemote) {
            return;
        }

        int totalPower = 0;
        int[] powerChannels = new int[6];
        for (int i = 0; i < 6; i++) {
            int val = dmxReceiver.getChannel(i) & 0xFF;
            totalPower += val;
            powerChannels[i] = val;
        }
        if (getEnergyStored() < 1) {
            return;
        }
        socapexProvider.receiveSocapex(powerChannels, false);
        socapexProvider.updateDevices(world, pos);
        extractEnergy(totalPower, false);
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(Integer.MAX_VALUE, maxReceive);
        if (!simulate) {
            power += energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        int energyExtracted = Math.min(power, Math.min(1000, maxExtract));
        if (!simulate) {
            power -= energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return power;
    }

    @Override
    public int getMaxEnergyStored() {
        return 1;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return getEnergyStored() < getMaxEnergyStored();
    }

    public int getDmxStart() {
        return dmxStart;
    }

}
