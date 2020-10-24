package dev.theatricalmod.theatrical.tiles.lights;

import static dev.theatricalmod.theatrical.block.light.BlockIntelligentFixture.FLIPPED;

import dev.theatricalmod.theatrical.api.ChannelType;
import dev.theatricalmod.theatrical.api.capabilities.TheatricalEnergyStorage;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.IRGB;
import dev.theatricalmod.theatrical.block.light.BlockIntelligentFixture;
import dev.theatricalmod.theatrical.block.light.BlockMovingLight;
import dev.theatricalmod.theatrical.client.gui.container.ContainerIntelligentFixture;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;

public class TileEntityIntelligentFixture extends TileEntityFixtureDMXAcceptor implements IRGB, INamedContainerProvider {

    private TheatricalEnergyStorage energyStorage;

    public TileEntityIntelligentFixture() {
        super(TheatricalTiles.MOVING_LIGHT.get());
        this.energyStorage = new TheatricalEnergyStorage(2000, 2000);
    }

    @Override
    public void setFixture(Fixture fixture) {
        super.setFixture(fixture);
        if (fixture != null) {
            this.getIdmxReceiver().setChannelCount(fixture.getChannelCount());
        }
    }

    @Override
    public CompoundNBT getNBT(@Nullable CompoundNBT compoundNBT) {
        if(compoundNBT == null){
            compoundNBT = new CompoundNBT();
        }
        compoundNBT.put("energy", energyStorage.serializeNBT());
        return super.getNBT(compoundNBT);
    }

    @Override
    public void readNBT(CompoundNBT compoundNBT) {
        super.readNBT(compoundNBT);
        if(compoundNBT.contains("energy")){
            this.energyStorage.deserializeNBT(compoundNBT.getCompound("energy"));
        }
    }

    @Override
    public float getMaxLightDistance() {
        return 50;
    }

    @Override
    public Class<? extends Block> getBlock() {
        return BlockMovingLight.class;
    }

    @Override
    public boolean shouldTrace() {
        return true;
    }

    @Override
    public boolean emitsLight() {
        return true;
    }

    @Override
    public boolean isUpsideDown() {
        return world.getBlockState(pos).get(BlockIntelligentFixture.FLIPPED);
    }

    @Override
    public float getDefaultRotation() {
        return 90F;
    }

    @Override
    public float getBeamWidth() {
        return 0.15F;
    }

    @Override
    public float[] getBeamStartPosition() {
        return new float[]{0.5F, 0.5F, 0.15F};
    }

    public int getColorHex() {
        return (getRed() << 16) | (getGreen() << 8) | getBlue();
    }

    public int getRed() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && energyStorage.getEnergyStored() >= getFixture().getEnergyUse()) {
            return getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.RED)))).orElse(0);
        }
        return 0;
    }

    public int getGreen() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && energyStorage.getEnergyStored() >= getFixture().getEnergyUse()) {
            return getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.GREEN)))).orElse(0);
        }
        return 0;
    }

    public int getBlue() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && energyStorage.getEnergyStored() >= getFixture().getEnergyUse()) {
            return getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.BLUE)))).orElse(0);
        }
        return 0;
    }

    @Override
    public int getPan() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null && energyStorage.getEnergyStored() >= getFixture().getEnergyUse()) {
            return (int) ((convertByteToInt(getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.PAN))).orElse((byte) prevPan)) * 360) / 255F);
        }
        return prevPan;
    }

    @Override
    public int getTilt() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null && energyStorage.getEnergyStored() >= getFixture().getEnergyUse()) {
            return (int) ((convertByteToInt(getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.TILT))).orElse((byte) prevTilt)) * 180) / 255F) - 90;
        }
        return prevTilt;
    }

    @Override
    public int getFocus() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return (int) ((convertByteToInt(getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.FOCUS))).orElse((byte) prevTilt)) * 50) / 255F);
        }
        return prevFocus;
    }

    @Override
    public float getIntensity() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && energyStorage.getEnergyStored() >= getFixture().getEnergyUse()) {
            return convertByte(getCapability(DMXReceiver.CAP, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.INTENSITY))).orElse((byte) 0));
        }
        return 0;
    }


    public float convertByte(byte val) {
        return val & 0xFF;
    }

    public int convertByteToInt(byte val) {
        return Byte.toUnsignedInt(val);
    }

    @Override
    public int getExtraTilt() {
        return isUpsideDown() ? 90 : 90;
    }

    @Override
    public void tick() {
        if(getFixture() == null){
            return;
        }
        if(energyStorage.getEnergyStored() >= getFixture().getEnergyUse()){
            energyStorage.extractEnergy(getFixture().getEnergyUse(), false);
            super.tick();
        }
        prevPan = getPan();
        prevTilt = getTilt();
        prevFocus = getFocus();
    }

    @Override
    public float getRayTraceRotation() {
        if (getFixture() != null) {
            return world.getBlockState(pos).get(FLIPPED) ? getFixture().getRayTraceRotation() : 0;
        }
        return 0;
    }


    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Intelligent Fixture");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerIntelligentFixture(i, getWorld(), getPos());
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if(cap == CapabilityEnergy.ENERGY){
            return LazyOptional.of(() -> energyStorage).cast();
        }
        return super.getCapability(cap, side);
    }
}
