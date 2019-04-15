package dev.theatricalmod.theatrical.tiles;

import dev.theatricalmod.theatrical.api.ChannelType;
import dev.theatricalmod.theatrical.api.capabilities.dmx.receiver.DMXReceiver;
import dev.theatricalmod.theatrical.api.fixtures.Fixture;
import dev.theatricalmod.theatrical.api.fixtures.IRGB;
import dev.theatricalmod.theatrical.blocks.fixtures.BlockIntelligentFixture;
import dev.theatricalmod.theatrical.util.FixtureUtils;
import elucent.albedo.lighting.ILightProvider;
import elucent.albedo.lighting.Light;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.fml.common.Optional;

@Optional.Interface(iface = "elucent.albedo.lighting.ILightProvider", modid = "albedo")
public class TileMovingHead extends TileDMXAcceptor implements IRGB, IEnergyStorage, ILightProvider {

    private int red, green, blue = 0;

    public int power = 0;
    private int maxPower;
    private int ticksSinceUsage = 0;
    public int energyUsage, energyCost, channelStartPoint = 0;

    @Override
    public void setFixture(Fixture fixture) {
        super.setFixture(fixture);
        if (fixture != null) {
            this.maxPower = fixture.getMaxEnergy();
            this.energyCost = fixture.getEnergyUse();
            this.energyUsage = fixture.getEnergyUseTimer();
            this.getIdmxReceiver().setChannelCount(fixture.getChannelCount());
        }
    }

//    public TileMovingHead(int channelCount, int channelStartPoint, int maxPower, int energyCost, int energyUsage) {
//        super(channelCount, channelStartPoint);
//        this.maxPower = maxPower;
//        this.energyCost = energyCost;
//        this.energyUsage = energyUsage;
//    }


    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY) {
            return CapabilityEnergy.ENERGY.cast(this);
        }
        return super.getCapability(capability, facing);
    }


    @Override
    public NBTTagCompound getNBT(@Nullable NBTTagCompound nbtTagCompound) {
        nbtTagCompound = super.getNBT(nbtTagCompound);
        nbtTagCompound.setInteger("red", red);
        nbtTagCompound.setInteger("green", green);
        nbtTagCompound.setInteger("blue", blue);
        nbtTagCompound.setInteger("power", power);
        return nbtTagCompound;
    }

    @Override
    public void readNBT(NBTTagCompound nbtTagCompound) {
        super.readNBT(nbtTagCompound);
        red = nbtTagCompound.getInteger("red");
        green = nbtTagCompound.getInteger("green");
        blue = nbtTagCompound.getInteger("blue");
        power = nbtTagCompound.getInteger("power");
    }

    public void setRed(int red) {
        if (power >= energyCost) {
            this.red = red;
        }
    }

    public void setGreen(int green) {
        if (power >= energyCost) {
            this.green = green;
        }
    }

    public void setBlue(int blue) {
        if (power >= energyCost) {
            this.blue = blue;
        }
    }

    @Override
    public boolean shouldTrace() {
        return power > 0;
    }

    @Override
    public boolean emitsLight() {
        return false;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        if (!canReceive()) {
            return 0;
        }

        int energyReceived = Math.min(getMaxEnergyStored() - getEnergyStored(), Math.min(Integer.MAX_VALUE, maxReceive));
        if (!simulate) {
            power += energyReceived;
            markDirty();
            world.notifyBlockUpdate(pos, world.getBlockState(pos), world.getBlockState(pos), 3);
        }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        return 0;
    }

    @Override
    public int getEnergyStored() {
        return power;
    }

    @Override
    public int getMaxEnergyStored() {
        if (getFixture() != null) {
            return getFixture().getMaxEnergy();
        }
        return 0;
    }

    @Override
    public boolean canExtract() {
        return false;
    }

    @Override
    public boolean canReceive() {
        return getEnergyStored() < getMaxEnergyStored();
    }

    @Override
    public void setPan(int pan) {
        if (power >= energyCost) {
            super.setPan(pan);
        }
    }

    @Override
    public void setFocus(int focus) {
        if (power >= energyCost) {
            super.setFocus(focus);
        }
    }

    @Override
    public void setLightBlock(BlockPos lightBlock) {
        if (power >= energyCost) {
            super.setLightBlock(lightBlock);
        }
    }

    @Override
    public void setTilt(int tilt) {
        if (power >= energyCost) {
            super.setTilt(MathHelper.clamp(tilt, -90, 90));
        }
    }

    public int getRed() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.RED)));
        }
        return 0;
    }

    public int getGreen() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.GREEN)));
        }
        return 0;
    }

    public int getBlue() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.BLUE)));
        }
        return 0;
    }

    @Override
    public int getPan() {
        if (this.power < this.energyCost) {
            return prevPan;
        }
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return (int) ((convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.PAN))) * 360) / 255F);
        }
        return prevPan;
    }

    @Override
    public int getTilt() {
        if (this.power < this.energyCost) {
            return prevTilt;
        }
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return (int) ((convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.TILT))) * 180) / 255F) - 90;
        }
        return prevTilt;
    }

    @Override
    public int getFocus() {
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return convertByteToInt(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.FOCUS)));
        }
        return prevFocus;
    }

    @Override
    public float getIntensity() {
        if (this.power < this.energyCost) {
            return 0;
        }
        if (getFixture() != null && getFixture().getChannelsDefinition() != null) {
            return convertByte(getCapability(DMXReceiver.CAP, EnumFacing.SOUTH).getChannel(getFixture().getChannelsDefinition().getChannel(ChannelType.INTENSITY)));
        }
        return 0;
    }

    public float convertByte(byte val) {
        return val & 0xFF;
    }

    public int convertByteToInt(byte val) {
        return val & 0xFF;
    }

    @Override
    public void update() {
        super.update();
        ticksSinceUsage++;
        if (ticksSinceUsage >= energyUsage) {
            ticksSinceUsage = 0;
            if ((power - energyCost) >= 0) {
                power -= energyCost;
            }
        }
        prevTilt = getTilt();
        prevPan = getPan();
    }

    @Override
    public int getColorHex() {
        return (getRed() << 16) | (getGreen() << 8) | getBlue();
    }

    @Optional.Method(modid = "albedo")
    @Override
    public Light provideLight() {
        if (getLightBlock() == null) {
            return null;
        }
        int value = world.getBlockState(pos).getLightValue(world, pos);
        int color = FixtureUtils.getColorFromTE(this);
        int r = (color >> 16) & 0xFF;
        int g = (color >> 8) & 0xFF;
        int b = color & 0xFF;
        return Light.builder().pos(getLightBlock())
            .color(r, g, b, ((this.getIntensity() * 0.009F) / 255))
            .radius(16).build();
    }

    @Override
    public Class<? extends Block> getBlock() {
        return BlockIntelligentFixture.class;
    }

    @Override
    public float getRayTraceRotation() {
        if (getFixture() != null) {
            return world.getBlockState(pos).getValue(BlockIntelligentFixture.FLIPPED) ? getFixture().getRayTraceRotation() : 0;
        }
        return 0;
    }
}
