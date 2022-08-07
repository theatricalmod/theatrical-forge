package dev.theatricalmod.theatrical.tiles.lights;

import dev.theatricalmod.theatrical.api.CableType;
import dev.theatricalmod.theatrical.api.IAcceptsCable;
import dev.theatricalmod.theatrical.api.capabilities.power.ITheatricalPowerStorage;
import dev.theatricalmod.theatrical.api.capabilities.power.TheatricalPower;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.block.BlockHangable;
import dev.theatricalmod.theatrical.block.light.BlockGenericFixture;
import dev.theatricalmod.theatrical.client.gui.container.ContainerGenericFixture;
import dev.theatricalmod.theatrical.tiles.TheatricalTiles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEntityGenericFixture extends TileEntityFixture implements MenuProvider, ITheatricalPowerStorage, IAcceptsCable {
    public static <T extends BlockEntity> void tick(Level level, BlockPos pos, BlockState state, T be) {
        TileEntityGenericFixture tile = (TileEntityGenericFixture) be;
        TileEntityFixture.tick(level, pos, state, be);
        if (level != null && !level.isClientSide) {
            tile.prevPan = tile.getPan();
            tile.prevTilt = tile.getTilt();
            tile.prevFocus = tile.getFocus();
            if (tile.trackingEntity != null) {
                double distance = Math.sqrt(tile.trackingEntity.distanceToSqr(tile.getBlockPos().getX(), tile.trackingEntity.getY(), tile.getBlockPos().getZ()));
                double height = tile.getBlockPos().getY() - tile.trackingEntity.getEyeY();
                double someCalc = height / distance;
                tile.setTilt(-(int) Math.toDegrees(Math.atan(someCalc)));
                Direction facing = tile.getBlockState().getValue(BlockHangable.FACING);
                double x = tile.getBlockPos().getX() - tile.trackingEntity.getX();
                double z = tile.getBlockPos().getZ() - tile.trackingEntity.getZ();
                double calc = Math.atan2(x, z);
                int pan = (int) Math.toDegrees(calc);
                pan = pan - (int) facing.toYRot();
                if (pan < -180) {
                    pan += 360;
                } else if (pan > 180) {
                    pan -= 360;
                }
                tile.setPan(pan);
            }
            if (tile.power != tile.lastPower) {
                tile.lastPower = tile.power;
                level.sendBlockUpdated(tile.worldPosition, level.getBlockState(tile.worldPosition), level.getBlockState(tile.worldPosition), 11);
            }
            if (tile.power > 0) {
                int energyExtracted = Math.min(tile.power, Math.min(tile.maxExtract, tile.energyCost));
                tile.power -= energyExtracted;
            }
        }
    }
    public int lastPower = 0;
    public int energyUsage, energyCost;

    private int power;
    private final int maxReceive = 255;
    private final int maxExtract = 255;

    private Entity trackingEntity;

    public TileEntityGenericFixture(BlockPos blockPos, BlockState blockState) {
        super(TheatricalTiles.GENERIC_LIGHT.get(), blockPos, blockState);
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
    public CompoundTag getNBT(@Nullable CompoundTag compoundNBT) {
        CompoundTag compoundNBT1 = super.getNBT(compoundNBT);
        compoundNBT1.putInt("lastPower", lastPower);
        return compoundNBT1;
    }

    @Override
    public void readNBT(CompoundTag compoundNBT) {
        super.readNBT(compoundNBT);
        if (compoundNBT.contains("lastPower")) {
            lastPower = compoundNBT.getInt("lastPower");
        }
    }

    @Override
    public float getMaxLightDistance() {
        return 20;
    }

    @Override
    public Class<? extends Block> getBlock() {
        return BlockGenericFixture.class;
    }

    @Override
    public boolean shouldTrace() {
        return power > 0;
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

    public void setTrackingEntity(Entity trackingEntity) {
        this.trackingEntity = trackingEntity;
    }

    public Entity getTrackingEntity() {
        return trackingEntity;
    }

    @Override
    public float getIntensity() {
        return lastPower;
    }
    @Override
    public Component getDisplayName() {
        return new TextComponent("Generic Fixture");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory playerInventory, Player playerEntity) {
        return new ContainerGenericFixture(i, getLevel(), getBlockPos());
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
        return 255;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return true;
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
        return new CableType[]{CableType.DIMMED_POWER};
    }
}
