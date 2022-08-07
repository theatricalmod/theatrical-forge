package dev.theatricalmod.theatrical.tiles.lights;

import dev.theatricalmod.theatrical.TheatricalConfigHandler;
import dev.theatricalmod.theatrical.api.ChannelType;
import dev.theatricalmod.theatrical.api.capabilities.TheatricalEnergyStorage;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.IRGB;
import dev.theatricalmod.theatrical.block.light.BlockIntelligentFixture;
import dev.theatricalmod.theatrical.block.light.BlockMovingLight;
import dev.theatricalmod.theatrical.capability.TheatricalCapabilities;
import dev.theatricalmod.theatrical.client.gui.container.ContainerIntelligentFixture;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityIntelligentFixture extends TileEntityFixtureDMXAcceptor implements IRGB, MenuProvider {
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityIntelligentFixture tile = (TileEntityIntelligentFixture) be;
        if(tile.getFixture() == null){
            return;
        }
        if(tile.isPowered()){
            if(TheatricalConfigHandler.COMMON.consumePower.get()) {
                tile.energyStorage.extractEnergy(tile.getFixture().getEnergyUse(), false);
            }
            TileEntityFixture.tick(level, pos, state, be);
        }
        tile.prevPan = tile.getPan();
        tile.prevTilt = tile.getTilt();
        tile.prevFocus = tile.getFocus();
    }
    private final TheatricalEnergyStorage energyStorage;

    public TileEntityIntelligentFixture(BlockPos blockPos, BlockState blockState) {
        super(TheatricalTiles.MOVING_LIGHT.get(), blockPos, blockState);
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
    public CompoundTag getNBT(@Nullable CompoundTag compoundNBT) {
        if(compoundNBT == null){
            compoundNBT = new CompoundTag();
        }
        compoundNBT.put("energy", energyStorage.serializeNBT());
        return super.getNBT(compoundNBT);
    }

    @Override
    public void readNBT(CompoundTag compoundNBT) {
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
        if(isPowered()) {
            return this.getLightBlock() == null || prevPan != getPan() || prevTilt != getTilt();
        }
        return false;
    }

    @Override
    public boolean emitsLight() {
        return super.emitsLight();
    }

    @Override
    public boolean isUpsideDown() {
        return this.getBlockState().getValue(BlockIntelligentFixture.HANGING);
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
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && isPowered()) {
            return getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.RED)))).orElse(0);
        }
        return 0;
    }

    public int getGreen() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && isPowered()){
            return getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.GREEN)))).orElse(0);
        }
        return 0;
    }

    public int getBlue() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && isPowered()) {
            return getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).map(idmxReceiver1 -> convertByteToInt(idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.BLUE)))).orElse(0);
        }
        return 0;
    }

    @Override
    public int getPan() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null && isPowered()) {
            return (int) ((convertByteToInt(getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.PAN))).orElse((byte) prevPan)) * 360) / 255F);
        }
        return prevPan;
    }

    @Override
    public int getTilt() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null && isPowered()) {
            return (int) ((convertByteToInt(getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.TILT))).orElse((byte) prevTilt)) * 180) / 255F) - 90;
        }
        return prevTilt;
    }

    @Override
    public int getFocus() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return (int) ((convertByteToInt(getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.FOCUS))).orElse((byte) prevTilt)) * 50) / 255F);
        }
        return prevFocus;
    }

    @Override
    public float getIntensity() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null  && isPowered()){
            return convertByte(getCapability(TheatricalCapabilities.CAPABILITY_DMX_RECEIVER, Direction.SOUTH).map(idmxReceiver1 -> idmxReceiver1.getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.INTENSITY))).orElse((byte) 0));
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

    public boolean isPowered(){
        if(!TheatricalConfigHandler.COMMON.consumePower.get()){
            return true;
        }
        return energyStorage.getEnergyStored() >= getFixture().getEnergyUse();
    }

    @Override
    public float getRayTraceRotation() {
        if (getFixture() != null) {
            return this.getBlockState().getValue(BlockIntelligentFixture.HANGING) ? 0 : getFixture().getRayTraceRotation();
        }
        return 0;
    }


    @Override
    public Component getDisplayName() {
        return new TextComponent("Intelligent Fixture");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerIntelligentFixture(i, getLevel(), getBlockPos());
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
