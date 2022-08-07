package dev.theatricalmod.theatrical.tiles.power;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.socapex.SocapexProvider;
import dev.theatricalmod.theatrical.api.dmx.IDMXReceiver;
import dev.theatricalmod.theatrical.capability.CapabilityDMXReceiver;
import dev.theatricalmod.theatrical.client.gui.container.ContainerDimmerRack;
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
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityDimmerRack extends TileEntityTheatricalBase implements MenuProvider, IEnergyStorage, IAcceptsCable{

    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityDimmerRack tile = (TileEntityDimmerRack) be;
        if (level.isClientSide) {
            return;
        }

        int totalPower = 0;
        int[] powerChannels = new int[6];
        for (int i = 0; i < 6; i++) {
            int val = tile.dmxReceiver.getChannel(tile.dmxReceiver.getStartPoint() + i) & 0xFF;
            totalPower += val;
            powerChannels[i] = val;
        }
        if (tile.getEnergyStored() < 1) {
            return;
        }
        tile.socapexProvider.receiveSocapex(powerChannels, false);
        tile.socapexProvider.updateDevices(level, pos);
        tile.extractEnergy(totalPower, false);
    }

    private final IDMXReceiver dmxReceiver;
    private final SocapexProvider socapexProvider;

    private int[] channels;
    private int power;

    private final int ticks = 0;

    public TileEntityDimmerRack(BlockPos blockPos, BlockState blockState) {
        super(TheatricalTiles.DIMMER_RACK.get(), blockPos, blockState);
        dmxReceiver = new CapabilityDMXReceiver(6, 0);
        socapexProvider = new SocapexProvider();
    }

    @Override
    public Component getDisplayName() {
        return new TextComponent("Dimmer Rack");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory p_createMenu_2_, Player p_createMenu_3_) {
        return new ContainerDimmerRack(i, level, getBlockPos());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        //TODO : fix Caps
//        if (cap == DMXReceiver.CAP) {
//            return DMXReceiver.CAP.orEmpty(cap, LazyOptional.of(() -> dmxReceiver));
//        }
//        if (cap == SocapexProvider.CAP) {
//            return SocapexProvider.CAP.orEmpty(cap, LazyOptional.of(() -> socapexProvider));
//        }
        if (cap == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.orEmpty(cap, LazyOptional.of(() -> this));
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void readNBT(CompoundTag compound) {
        dmxReceiver.setDMXStartPoint(compound.getInt("dmxStart"));
        if (compound.contains("provider")) {
            socapexProvider.deserializeNBT(compound.getCompound("provider"));
        }
    }

    @Override
    public CompoundTag getNBT(CompoundTag compound) {
        if(compound == null){
            compound = new CompoundTag();
        }
        compound.putInt("dmxStart", dmxReceiver.getStartPoint());
        compound.put("provider", socapexProvider.serializeNBT());
        return compound;
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.DIMMED_POWER, CableType.SOCAPEX, CableType.DMX};
    }

    @Override
    public void setRemoved() {
        if (hasLevel()) {
//            getLevel().getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
//            getLevel().getCapability(WorldSocapexNetwork.CAP).ifPresent(worldSocapexNetwork -> worldSocapexNetwork.setRefresh(true));
        }
        super.setRemoved();
    }

    @Override
    public void clearRemoved() {
        if (hasLevel()) {
//            getLevel().getCapability(WorldDMXNetwork.CAP).ifPresent(worldDMXNetwork -> worldDMXNetwork.setRefresh(true));
//            getLevel().getCapability(WorldSocapexNetwork.CAP).ifPresent(worldSocapexNetwork -> worldSocapexNetwork.setRefresh(true));
        }
        super.clearRemoved();
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
        return dmxReceiver.getStartPoint();
    }

}
