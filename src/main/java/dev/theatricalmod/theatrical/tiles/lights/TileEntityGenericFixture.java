package dev.theatricalmod.theatrical.tiles.lights;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.light.BlockGenericFixture;
import dev.theatricalmod.theatrical.client.gui.container.ContainerGenericFixture;
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

public class TileEntityGenericFixture extends TileEntityFixture implements INamedContainerProvider, ITheatricalPowerStorage, IAcceptsCable {

    public int lastPower = 0;
    public int energyUsage, energyCost;

    private int power;
    private int capacity = 255;
    private int maxReceive = 255;
    private int maxExtract = 255;

    public TileEntityGenericFixture() {
        super(TheatricalTiles.GENERIC_LIGHT.get());
    }

    @Override
    public void setFixture(Fixture fixture) {
        super.setFixture(fixture);
        if (fixture != null) {
            energyCost = fixture.getEnergyUse();
            energyUsage = fixture.getEnergyUseTimer();
        }
    }

    @Override
    public CompoundNBT getNBT(@Nullable CompoundNBT compoundNBT) {
        CompoundNBT compoundNBT1 = super.getNBT(compoundNBT);
        compoundNBT1.putInt("lastPower", lastPower);
        return compoundNBT1;
    }

    @Override
    public void readNBT(CompoundNBT compoundNBT) {
        super.readNBT(compoundNBT);
        if (compoundNBT.contains("lastPower")) {
            lastPower = compoundNBT.getInt("lastPower");
        }
    }

    @Override
    public float getMaxLightDistance() {
        return 10;
    }

    @Override
    public Class<? extends Block> getBlock() {
        return BlockGenericFixture.class;
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
        return false;
    }

    @Override
    public float getBeamWidth() {
        return 0.15F;
    }

    @Override
    public float[] getBeamStartPosition() {
        if(getFixture() == null){
            return new float[3];
        }
        return getFixture().getBeamStartPosition();
    }

    @Override
    public float getIntensity() {
        return lastPower;
    }

    @Override
    public void tick() {
        super.tick();
        prevPan = getPan();
        prevTilt = getTilt();
        prevFocus = getFocus();
        if (world.isRemote) {
            return;
        }
        if (power != lastPower) {
            lastPower = power;
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 11);
        }
        if(power > 0){
            int energyExtracted = Math.min(power, Math.min(this.maxExtract, this.energyCost));
            power -= energyExtracted;
        }
    }

    @Override
    public ITextComponent getDisplayName() {
        return new StringTextComponent("Generic Fixture");
    }

    @Nullable
    @Override
    public Container createMenu(int i, PlayerInventory playerInventory, PlayerEntity playerEntity) {
        return new ContainerGenericFixture(i, getWorld(), getPos());
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(this.maxReceive, maxReceive);
        if (!simulate) {
            power = energyReceived;
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        if (!canExtract()) {
            return 0;
        }

        int energyExtracted = Math.min(power, Math.min(this.maxExtract, maxExtract));
        if (!simulate) {
            power = energyExtracted;
        }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() {
        return power;
    }

    @Override
    public int getMaxEnergyStored() {
        return capacity;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return this.maxReceive > 0;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == TheatricalPower.CAP) {
            return LazyOptional.of(() -> (T) this);
        }
        return super.getCapability(cap, side);
    }

    @Override
    public CableType[] getAcceptedCables(Direction side) {
        return new CableType[]{CableType.POWER};
    }
}
